package org.twdata.twxbbs.proxy.script;

import org.apache.mina.common.*;

import java.util.List;
import java.util.ArrayList;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 26/08/2008
 * Time: 9:24:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScriptIoFilter extends IoFilterAdapter {

    private final List<URL> scriptUrls;

    public ScriptIoFilter(List<URL> scriptUrls) {
        this.scriptUrls = scriptUrls;
    }

    @Override
    public void messageReceived(NextFilter nextFilter, IoSession ioSession, Object o) throws Exception {
        if (o instanceof ByteBuffer) {
            ByteBuffer buffer = (ByteBuffer) o;
            for (ScriptLexer lexer : getPlayerLexers(ioSession)) {
                lexer.parse(buffer);
                buffer.flip();
            }
            nextFilter.messageReceived(ioSession, buffer);
        } else {
            throw new IllegalArgumentException("Only byte buffers are supported");
        }
    }

    @Override
    public void messageSent(NextFilter nextFilter, IoSession ioSession, Object o) throws Exception {
        if (o instanceof ByteBuffer) {
            ByteBuffer buffer = (ByteBuffer) o;
            for (ScriptLexer lexer : getGameLexers(ioSession)) {
                lexer.parse(buffer);
                buffer.flip();
            }
            nextFilter.messageSent(ioSession, buffer);
        } else {
            throw new IllegalArgumentException("Only byte buffers are supported");
        }
    }

    private List<ScriptLexer> getGameLexers(IoSession session) {
        return (List<ScriptLexer>) session.getAttribute("gameLexers");
    }
    private List<ScriptLexer> getPlayerLexers(IoSession session) {
        return (List<ScriptLexer>) session.getAttribute("playerLexers");
    }

    @Override
    public void sessionOpened(final NextFilter nextFilter, final IoSession ioSession) throws Exception {
        List<Thread> scripts = new ArrayList<Thread>();
        List<ScriptLexer> gameLexers = new ArrayList<ScriptLexer>();
        List<ScriptLexer> playerLexers = new ArrayList<ScriptLexer>();
        for (URL url : scriptUrls) {
            ScriptLexer gameLexer = new ScriptLexer();
            ScriptApi gameApi = new ScriptApiImpl(gameLexer, new ScriptApiImpl.TextSender() {
                public void send(String text) throws Exception {
                    nextFilter.messageReceived(ioSession, ByteBuffer.wrap(text.getBytes()));
                }
            });
            ScriptLexer playerLexer = new ScriptLexer();
            ScriptApi playerApi = new ScriptApiImpl(playerLexer, new ScriptApiImpl.TextSender() {
                public void send(String text) throws Exception {
                    nextFilter.filterWrite(ioSession, new WriteRequest(ByteBuffer.wrap(text.getBytes())));
                }
            });
            Script script = new JavascriptScript(url, gameApi, playerApi);
            gameLexers.add(gameLexer);
            playerLexers.add(playerLexer);
            Thread t = new Thread(script);
            scripts.add(t);
            t.start();
        }
        ioSession.setAttribute("gameLexers", gameLexers);
        ioSession.setAttribute("playerLexers", playerLexers);
        ioSession.setAttribute("scriptThreads", scripts);
        super.sessionOpened(nextFilter, ioSession);
    }

    @Override
    public void sessionClosed(NextFilter nextFilter, IoSession ioSession) throws Exception {
        List<Thread> threads = (List<Thread>) ioSession.getAttribute("scriptThreads");
        for (Thread thread : threads) {
            thread.interrupt();
        }
        ioSession.removeAttribute("gameLexers");
        ioSession.removeAttribute("playerLexers");
        ioSession.removeAttribute("scriptThreads");
        // todo: stop scripts somehow?
        super.sessionClosed(nextFilter, ioSession);    //To change body of overridden methods use File | Settings | File Templates.
    }
}