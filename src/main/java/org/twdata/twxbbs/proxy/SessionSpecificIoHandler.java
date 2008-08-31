package org.twdata.twxbbs.proxy;

import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.IdleStatus;
import org.twdata.twxbbs.proxy.ProxyConnector;

/**
 * Delegates to the IoHandler defined for this session
 */
public class SessionSpecificIoHandler implements IoHandler {
    private static final String IO_HANDLER_KEY = "ioHandler";
    private final String host;
    private final int port;
    private final ProxyConnector connector;

    public SessionSpecificIoHandler(ProxyConnector connector, String host, int port) {
        this.host = host;
        this.port = port;
        this.connector = connector;
    }

    public void sessionCreated(IoSession session) throws Exception {
        connector.connect(session, host, port);
        getHandler(session).sessionCreated(session);
    }

    public void sessionOpened(IoSession session) throws Exception {
        getHandler(session).sessionOpened(session);
    }

    public void sessionClosed(IoSession session) throws Exception {
        getHandler(session).sessionClosed(session);
    }

    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        getHandler(session).sessionIdle(session, status);
    }

    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        getHandler(session).exceptionCaught(session, cause);
    }

    public void messageReceived(IoSession session, Object message) throws Exception {
        getHandler(session).messageReceived(session, message);
    }

    public void messageSent(IoSession session, Object message) throws Exception {
        getHandler(session).messageSent(session, message);
    }

    public static void setHandlerForSession(IoSession session, IoHandler handler) {
        session.setAttribute(IO_HANDLER_KEY, handler);
    }

    private IoHandler getHandler(IoSession session) {
        return (IoHandler) session.getAttribute(IO_HANDLER_KEY);
    }
}
