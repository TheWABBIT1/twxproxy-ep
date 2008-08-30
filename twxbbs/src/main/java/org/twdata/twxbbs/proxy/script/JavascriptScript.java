package org.twdata.twxbbs.proxy.script;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 26/08/2008
 * Time: 10:54:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class JavascriptScript implements Script {
    private URL script;
    private ScriptApi gameApi;
    private ScriptApi playerApi;
    private Map<String,Object> session;
    private Map<String, Object> appContext;

    public JavascriptScript(URL script, ScriptApi gameApi, ScriptApi playerApi, Map<String,Object> session, Map<String,Object> appContext) {
        this.script = script;
        this.gameApi = gameApi;
        this.playerApi = playerApi;
        this.session = session;
        this.appContext = appContext;
    }

    public void run() {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine jsEngine = mgr.getEngineByName("JavaScript");
        jsEngine.put("gameApi", gameApi);
        jsEngine.put("playerApi", playerApi);
        jsEngine.put("session", session);
        jsEngine.put("application", appContext);

        readScriptIntoEngine(jsEngine, getClass().getClassLoader().getResource("org/twdata/twxbbs/proxy/script/global.js"));
        readScriptIntoEngine(jsEngine, script);
    }

    private void readScriptIntoEngine(ScriptEngine jsEngine, URL script) {
        Reader scriptReader = null;
        try {
            scriptReader = new InputStreamReader(script.openStream());
            jsEngine.eval(scriptReader);
        } catch (ScriptException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            if (scriptReader != null) {
                try {
                    scriptReader.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        }
    }

}
