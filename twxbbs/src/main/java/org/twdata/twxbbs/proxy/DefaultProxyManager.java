package org.twdata.twxbbs.proxy;

import org.apache.mina.common.IoAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import org.twdata.twxbbs.GameRegistration;

import java.security.KeyStore;
import java.net.InetSocketAddress;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 18/08/2008
 * Time: 22:20:52
 * To change this template use File | Settings | File Templates.
 */
public class DefaultProxyManager implements ProxyManager {

    private final int proxyPort;
    private final String targetHost;
    private final int targetPort;
    final IoAcceptor acceptor;

    public DefaultProxyManager(int proxyPort, String targetHost, int targetPort) {
        this.proxyPort = proxyPort;
        this.targetHost = targetHost;
        this.targetPort = targetPort;

        acceptor = new SocketAcceptor();
        ((SocketAcceptorConfig) acceptor.getDefaultConfig())
                .setReuseAddress(true);
    }

    public void start() throws IOException {
        // Create TCP/IP acceptor.
        SocketAcceptorConfig cfg = new SocketAcceptorConfig();
        acceptor
            .bind(new InetSocketAddress(proxyPort), new SessionSpecificIoHandler(targetHost, targetPort), cfg);
    }

    public void registerClient(String sessionToken, GameRegistration reg) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public void stop() {
        acceptor.unbindAll();
    }

    public boolean hasStarted() {
        return acceptor.isManaged(new InetSocketAddress(proxyPort));
    }

    public void unregisterClient(String sessionToken) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
