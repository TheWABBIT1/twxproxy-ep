package org.twdata.twxbbs.proxy.script;

import org.apache.mina.common.*;
import org.apache.mina.common.support.DefaultWriteFuture;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 26/08/2008
 * Time: 9:24:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScriptIoFilter extends IoFilterAdapter {

    private final ScriptManager scriptManager;

    public ScriptIoFilter(ScriptManager scriptManager) {
        this.scriptManager = scriptManager;
    }

    @Override
    public void messageReceived(NextFilter nextFilter, IoSession ioSession, Object o) throws Exception {
        if (o instanceof ByteBuffer) {
            ByteBuffer buffer = (ByteBuffer) o;
            for (ScriptLexer lexer : getSessionScriptRunner(ioSession).getPlayerLexers()) {
                buffer = lexer.parse(buffer);
                buffer.flip();
            }
            nextFilter.messageReceived(ioSession, buffer);
        } else {
            throw new IllegalArgumentException("Only byte buffers are supported");
        }
    }

    @Override
    public void filterWrite(NextFilter nextFilter, IoSession ioSession, WriteRequest req) throws Exception {
        Object o = req.getMessage();
        if (o instanceof ByteBuffer) {
            ByteBuffer buffer = (ByteBuffer) o;
            for (ScriptLexer lexer : getSessionScriptRunner(ioSession).getGameLexers()) {
                buffer = lexer.parse(buffer);
                buffer.flip();
            }
            WriteFuture future = new DefaultWriteFuture(ioSession, buffer);
            nextFilter.filterWrite(ioSession, new WriteRequest(buffer, future));
        } else {
            throw new IllegalArgumentException("Only byte buffers are supported");
        }
    }

    private SessionScriptRunner getSessionScriptRunner(IoSession session) {
        return ((SessionScriptRunner) session.getAttribute("sessionScriptRunner"));
    }

    @Override
    public void sessionOpened(final NextFilter nextFilter, final IoSession ioSession) throws Exception {
        final SessionScriptRunner sessionScriptRunner = new SessionScriptRunner(ioSession, nextFilter, scriptManager);
        ioSession.setAttribute("sessionScriptRunner", sessionScriptRunner);
        super.sessionOpened(nextFilter, ioSession);
    }

    @Override
    public void sessionClosed(NextFilter nextFilter, IoSession ioSession) throws Exception {
        getSessionScriptRunner(ioSession).stop();
        ioSession.removeAttribute("sessionScriptRunner");
        super.sessionClosed(nextFilter, ioSession);
    }
}
