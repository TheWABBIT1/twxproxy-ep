package org.twdata.twxbbs.proxy;

import org.apache.mina.common.IoAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import org.twdata.twxbbs.GameRegistration;
import org.twdata.twxbbs.proxy.script.ScriptIoFilter;
import org.twdata.twxbbs.config.ConfigurationRefreshedEvent;
import org.twdata.twxbbs.config.Configuration;
import org.twdata.twxbbs.event.EventManager;
import org.twdata.twxbbs.event.EventListener;

import java.security.KeyStore;
import java.net.InetSocketAddress;
import java.net.URL;
import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 18/08/2008
 * Time: 22:20:52
 * To change this template use File | Settings | File Templates.
 */
public class DefaultProxyManager implements ProxyManager {

    private int proxyPort;
    private String targetHost;
    private int targetPort;
    IoAcceptor acceptor;
    private ProxyConnector connector;
    private List<URL> scripts;

    public DefaultProxyManager(EventManager eventManager, ProxyConnector connector) {
        this.connector = connector;
        eventManager.register(this);
        scripts = new ArrayList<URL>();

    }

    @EventListener
    public synchronized void refresh(ConfigurationRefreshedEvent event) throws IOException {
        Configuration config = event.getConfiguration();
        this.proxyPort = config.getProxyPort();
        this.targetHost = config.getTwgsHost();
        this.targetPort = config.getTwgsPort();

        scripts.clear();
        File scriptsDir = new File(config.getBaseDir(), "scripts");
        if (scriptsDir.exists()) {
            for (File file : scriptsDir.listFiles()) {
                scripts.add(file.toURI().toURL());
            }
        }

        stop();
        acceptor = new SocketAcceptor();
        ((SocketAcceptorConfig) acceptor.getDefaultConfig())
                .setReuseAddress(true);
        start();
    }

    public synchronized void start() throws IOException {
        if (acceptor != null) {
            // Create TCP/IP acceptor.
            SocketAcceptorConfig cfg = new SocketAcceptorConfig();
            cfg.getFilterChain().addFirst("scripts", new ScriptIoFilter(scripts));
            acceptor
                .bind(new InetSocketAddress(proxyPort), new SessionSpecificIoHandler(connector, targetHost, targetPort), cfg);
            System.out.println("Proxy started on port "+proxyPort+" connecting to "+targetHost+":"+targetPort);
        } else {
            throw new IllegalStateException("Proxy server hasn't been configured yet");
        }

    }

    public void registerClient(String sessionToken, GameRegistration reg) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public synchronized void stop() {
        if (acceptor != null && hasStarted()) {
            acceptor.unbindAll();
        }
    }

    public synchronized boolean hasStarted() {
        return acceptor.isManaged(new InetSocketAddress(proxyPort));
    }

    public void unregisterClient(String sessionToken) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
