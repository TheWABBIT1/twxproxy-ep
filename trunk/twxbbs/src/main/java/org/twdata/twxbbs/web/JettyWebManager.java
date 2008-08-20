package org.twdata.twxbbs.web;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.resource.Resource;
import org.mortbay.resource.URLResource;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 20/08/2008
 * Time: 22:59:14
 * To change this template use File | Settings | File Templates.
 */
public class JettyWebManager implements WebManager {

    private final int port;
    private final Server server;

    public JettyWebManager(int port, Map<String, Servlet> servlets) throws IOException {
        this.port = port;
        server = new Server(port);

        ResourceHandler resource_handler=new ResourceHandler();
        resource_handler.setBaseResource(Resource.newResource(getClass().getClassLoader().getResource("org/twdata/twxbbs/web/public/")));

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler});
        server.setHandler(handlers);

        Context root = new Context(server,"/", Context.SESSIONS);
        for (Map.Entry<String,Servlet> entry : servlets.entrySet()) {
            root.addServlet(new ServletHolder(entry.getValue()), entry.getKey());
        }
    }

    public void start() throws IOException {
        try {
            server.start();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasStarted() {
        return server.isStarted();
    }
}
