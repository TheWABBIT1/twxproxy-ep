package org.twdata.twxbbs.proxy.script;

import javax.script.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 26/08/2008
 * Time: 10:54:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class JavascriptScript implements Script {
    private final URL script;

    public JavascriptScript(URL script) {
        this.script = script;
    }

    public void run(Map<String,Object> variables) {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine jsEngine = mgr.getEngineByName("JavaScript");

        for (Map.Entry<String,Object> entry : variables.entrySet()) {
            jsEngine.put(entry.getKey(), entry.getValue());
        }
        System.out.println(this+" started");
        readScriptIntoEngine(jsEngine, getClass().getClassLoader().getResource("org/twdata/twxbbs/proxy/script/global.js"));
        readScriptIntoEngine(jsEngine, getClass().getClassLoader().getResource("org/twdata/twxbbs/proxy/script/json2.js"));
        readScriptIntoEngine(jsEngine, script);
        System.out.println(this+" finished");
        variables.clear();
    }

    private void readScriptIntoEngine(ScriptEngine jsEngine, URL script) {
        Reader scriptReader = null;

        try {
            scriptReader = new InputStreamReader(script.openStream());
            jsEngine.put(ScriptEngine.NAME, script.toString());
            jsEngine.put(ScriptEngine.FILENAME, script.toString());
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

    public String toString() {
        return "Script ["+script.toString()+"]";
    }

}
