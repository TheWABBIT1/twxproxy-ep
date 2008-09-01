package org.twdata.twxbbs.proxy.script;

import org.apache.mina.common.IoSession;
import org.apache.mina.common.IoFilter;
import org.apache.mina.common.ByteBuffer;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 01/09/2008
 * Time: 9:00:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class SessionScriptRunner {
    private final IoSession ioSession;
    private final List<ScriptLexer> gameLexers;
    private final List<ScriptLexer> playerLexers;
    private final List<Thread> scriptThreads;
    private final IoFilter.NextFilter nextFilter;
    private final Map<String,Object> contextMap;
    private final ScriptManager scriptManager;

    public SessionScriptRunner(IoSession ioSession, IoFilter.NextFilter nextFilter, ScriptManager scriptManager) {
        this.ioSession = ioSession;
        this.nextFilter = nextFilter;
        this.scriptManager = scriptManager;
        gameLexers = new CopyOnWriteArrayList<ScriptLexer>();
        playerLexers = new CopyOnWriteArrayList<ScriptLexer>();
        scriptThreads = new CopyOnWriteArrayList<Thread>();
        contextMap = Collections.synchronizedMap(new HashMap<String,Object>());

        runSessionScripts();
    }

    public Map<String,Object> createSessionScriptVariables() {
        Map<String,Object> vars = new HashMap<String,Object>(){
            @Override
            public void clear() {
                gameLexers.remove(((ScriptApiImpl)get("gameApi")).getScriptLexer());
                playerLexers.remove(((ScriptApiImpl)get("playerApi")).getScriptLexer());
                super.clear();
            }
        };
        ScriptLexer gameLexer = new ScriptLexer();
        ScriptApi gameApi = new ScriptApiImpl(gameLexer, new ScriptApiImpl.TextSender() {
            public void send(String text) throws Exception {
                nextFilter.messageReceived(ioSession, ByteBuffer.wrap(text.getBytes()));
            }
        });
        ScriptLexer playerLexer = new ScriptLexer();
        ScriptApi playerApi = new ScriptApiImpl(playerLexer, new ScriptApiImpl.TextSender() {
            public void send(String text) throws Exception {
                nextFilter.filterWrite(ioSession, new IoFilter.WriteRequest(ByteBuffer.wrap(text.getBytes())));
            }
        });
        vars.put("gameApi", gameApi);
        vars.put("playerApi", playerApi);
        vars.put("session", contextMap);
        vars.put("sessionScriptRunner", this);
        gameLexers.add(gameLexer);
        playerLexers.add(playerLexer);
        return vars;
    }

    public void runSessionScripts() {
        scriptThreads.addAll(scriptManager.startScripts(ScriptType.session.name(), new SessionScriptVariablesFactory()));

    }

    public Thread run(Script script) {
        Thread t = scriptManager.startScript(script, createSessionScriptVariables());
        scriptThreads.add(t);
        return t;
    }

    public List<Thread> runAllInDirectory(String path) {
        List<Thread> list =  scriptManager.startScripts(path, new SessionScriptVariablesFactory());
        scriptThreads.addAll(list);
        return list;
    }
    
    public void stop() {
        for (Thread thread : scriptThreads) {
            if (thread.isAlive()) {
                thread.interrupt();
            }
        }
        scriptThreads.clear();
    }

    public void stopScripts(List<Thread> threads) {
        for (Thread thread : threads) {
            if (thread.isAlive()) {
                thread.interrupt();
            }
        }
        scriptThreads.removeAll(threads);
    }

    public List<ScriptLexer> getGameLexers() {
        return gameLexers;
    }

    public List<ScriptLexer> getPlayerLexers() {
        return playerLexers;
    }

    private class SessionScriptVariablesFactory implements ScriptVariablesFactory {

        public Map<String, Object> create() {
            return createSessionScriptVariables();
        }
    }
}
