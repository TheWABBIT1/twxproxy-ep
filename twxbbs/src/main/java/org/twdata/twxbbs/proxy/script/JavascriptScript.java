package org.twdata.twxbbs.proxy.script;

import javax.script.*;
import java.net.URL;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.IOException;

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

    public JavascriptScript(URL script, ScriptApi gameApi, ScriptApi playerApi) {
        this.script = script;
        this.gameApi = gameApi;
        this.playerApi = playerApi;
    }

    public void run() {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine jsEngine = mgr.getEngineByName("JavaScript");
        Reader reader = null;
        jsEngine.put("game", gameApi);
        jsEngine.put("player", playerApi);
        try {
            reader = new InputStreamReader(script.openStream());
            jsEngine.eval(reader);
        } catch (ScriptException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        }
    }

}
