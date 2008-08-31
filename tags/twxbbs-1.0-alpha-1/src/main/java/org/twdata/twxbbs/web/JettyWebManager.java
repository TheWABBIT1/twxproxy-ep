package org.twdata.twxbbs.web;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.resource.Resource;
import org.twdata.twxbbs.config.Configuration;
import org.twdata.twxbbs.config.ConfigurationRefreshedEvent;
import org.twdata.twxbbs.event.EventListener;
import org.twdata.twxbbs.event.EventManager;
import org.twdata.twxbbs.web.template.TemplateGenerator;
import org.twdata.twxbbs.Container;

import javax.servlet.Servlet;
import java.io.IOException;
import java.io.File;
import java.util.Map;
import java.net.MalformedURLException;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 20/08/2008
 * Time: 22:59:14
 * To change this template use File | Settings | File Templates.
 */
public class JettyWebManager implements WebManager {

    private Server server;
    private final Map<String, Servlet> servlets;
    private final TemplateGenerator templateGenerator;

    public JettyWebManager(EventManager eventManager, TemplateGenerator generator, Map<String, Servlet> servlets) throws IOException {
        eventManager.register(this);
        this.servlets = servlets;
        this.templateGenerator = generator;
    }


    @EventListener
    public synchronized void refresh(ConfigurationRefreshedEvent event) throws IOException {
        Configuration config = event.getConfiguration();
        stop();
        try {
            server = new Server(config.getWebPort());

            HandlerList handlers = new HandlerList();
            handlers.addHandler(new RedirectHandler("/games"));
            ResourceHandler internalHandler=new ResourceHandler();
            internalHandler.setBaseResource(Resource.newResource(getClass().getClassLoader().getResource("org/twdata/twxbbs/web/public/")));

            File web = new File(config.getBaseDir(), "web");
            if (web.exists()) {
                ResourceHandler baseDirHandler=new ResourceHandler();
                baseDirHandler.setBaseResource(Resource.newResource(new File(config.getBaseDir(), "web").toURI().toURL()));
                handlers.addHandler(baseDirHandler);
            }
            handlers.addHandler(internalHandler);
            server.setHandler(handlers);

            Context root = new Context(server,"/", Context.SESSIONS);

            if (!config.isSetup() || !config.isWebClientEnabled()) {
                root.addServlet(new ServletHolder(new ConfigurationServlet(config, templateGenerator)), "/*");
            } else {
                root.setWelcomeFiles(new String[]{"games.xhtml"});
                for (Map.Entry<String,Servlet> entry : servlets.entrySet()) {
                    root.addServlet(new ServletHolder(entry.getValue()), entry.getKey());
                }
                root.addServlet(new ServletHolder(servlets.get("/games")), "*.xhtml");
                root.addServlet(new ServletHolder(new ConfigurationServlet(config, templateGenerator)), "/admin");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Unable to set resource directory");
        }

        start();

    }

    public synchronized void start() throws IOException {
        try {
            server.start();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public synchronized void stop() {
        if (server != null && server.isStarted()) {
            try {
                server.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @EventListener
    public void shutdown(Container.ContainerStoppedEvent event) {
        stop();
    }

    public boolean hasStarted() {
        return server.isStarted();
    }
}
