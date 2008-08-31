package org.twdata.twxbbs.proxy.script;

import org.twdata.twxbbs.config.ConfigurationRefreshedEvent;
import org.twdata.twxbbs.config.Configuration;
import org.twdata.twxbbs.event.EventListener;
import org.twdata.twxbbs.event.EventManager;
import org.twdata.twxbbs.Container;

import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 30/08/2008
 * Time: 9:33:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultScriptManager implements ScriptManager {
    private List<Thread> applicationScriptThreads;
    private File scriptsDir;
    private Configuration configuration;
    private final Map<String,Object> applicationContext;

    public DefaultScriptManager(EventManager eventManager) {
        applicationContext = Collections.synchronizedMap(new HashMap<String,Object>());
        eventManager.register(this);
    }

    @EventListener
    public synchronized void refresh(ConfigurationRefreshedEvent event) throws IOException {
        stop();
        this.configuration = event.getConfiguration();
        scriptsDir = new File(configuration.getBaseDir(), "scripts");
        start();
    }

    private Collection<URL> collectScripts(String name) {
        Map<String,URL> scripts = new TreeMap<String,URL>();
        if (scriptsDir.exists()) {
            File sessionScriptsDir = new File(scriptsDir, name);
            if (sessionScriptsDir.exists()) {
                for (File file : sessionScriptsDir.listFiles()) {
                    if (!file.isDirectory()) {
                        try {
                            scripts.put(file.getName(), file.toURI().toURL());
                        } catch (MalformedURLException e) {
                            System.err.println("Unable to add script:"+file.getAbsolutePath());
                        }
                    }
                }
            }
        }
        return scripts.values();
    }

    public void stop() {
        if (applicationScriptThreads != null) {
            for (Thread t : applicationScriptThreads) {
                t.interrupt();
            }
            applicationScriptThreads.clear();
        }
    }

    public boolean hasStarted() {
        return (applicationScriptThreads != null && !applicationScriptThreads.isEmpty());
    }

    public void start() {
        applicationScriptThreads = startScripts(ScriptType.application, new ScriptVariablesFactory() {
            public Map<String, Object> create() {
                final Map<String,Object> vars = new HashMap<String,Object>();
                vars.put("configuration", configuration);
                return vars;
            }
        });
    }

    @EventListener
    public void shutdown(Container.ContainerStoppedEvent event) {
        stop();
    }
    
    public List<Thread> startScripts(ScriptType type, ScriptVariablesFactory varFactory) {
        List<Thread> scriptThreads = new ArrayList<Thread>();
        Map<String,Object> context = applicationContext;
        if (type != ScriptType.application) {
            context = new HashMap<String,Object>();
        }
        Collection<URL> scriptUrls = collectScripts(type.name());
        for (URL url : scriptUrls) {

            Map<String,Object> vars = varFactory.create();
            if (type != ScriptType.application) {
                vars.put("context", context);
            }
            vars.put("application", context);
            Script script = new JavascriptScript(url, vars);
            Thread t = new Thread(script);
            scriptThreads.add(t);
            System.out.println("Executing script "+url);
            t.start();
        }
        return scriptThreads;
    }
}
