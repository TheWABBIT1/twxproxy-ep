package org.twdata.twxbbs.proxy;

import org.apache.mina.common.*;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.twdata.twxbbs.proxy.SessionSpecificIoHandler;
import org.twdata.twxbbs.proxy.ClientToProxyIoHandler;
import org.twdata.twxbbs.proxy.ServerToProxyIoHandler;
import org.twdata.twxbbs.proxy.script.ScriptIoFilter;
import org.twdata.twxbbs.proxy.script.ScriptLexer;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 10/08/2008
 * Time: 15:03:46
 * To change this template use File | Settings | File Templates.
 */
public class ProxyConnector {

    public void connect(IoSession session, String host, int port) throws Exception {

        // Create TCP/IP connector.
        IoConnector connector = new SocketConnector();

        // Set connect timeout.
        ((IoConnectorConfig) connector.getDefaultConfig())
                .setConnectTimeout(30);

        ClientToProxyIoHandler handler = new ClientToProxyIoHandler(
                new ServerToProxyIoHandler(), connector, new InetSocketAddress(
                        host, port));

        SessionSpecificIoHandler.setHandlerForSession(session, handler);
        //handler.sessionCreated(session);
        //handler.sessionOpened(session);
    }
}
