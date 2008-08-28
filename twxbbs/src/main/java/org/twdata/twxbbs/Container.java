package org.twdata.twxbbs;

import org.twdata.twxbbs.impl.StubGameAccessor;
import org.twdata.twxbbs.web.template.TemplateGenerator;
import org.twdata.twxbbs.web.*;
import org.twdata.twxbbs.proxy.ProxyManager;
import org.twdata.twxbbs.proxy.DefaultProxyManager;
import org.twdata.twxbbs.proxy.ProxyConnector;
import org.twdata.twxbbs.config.impl.IniConfiguration;
import org.twdata.twxbbs.config.Configuration;
import org.twdata.twxbbs.event.EventManager;
import org.twdata.twxbbs.event.impl.DefaultEventManager;
import org.ini4j.Ini;

import javax.servlet.Servlet;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 20/08/2008
 * Time: 22:17:43
 * To change this template use File | Settings | File Templates.
 */
public class Container {
    private Map<Class<?>,Object> objects = new HashMap<Class<?>,Object>();

    Container(File baseDir) throws IOException {


        objects.put(EventManager.class, new DefaultEventManager());
        objects.put(Configuration.class, new IniConfiguration(
                baseDir,
                get(EventManager.class)
        ));

        objects.put(GameAccessor.class, new StubGameAccessor());
        objects.put(ProxyConnector.class, new ProxyConnector());
        objects.put(TemplateGenerator.class, new TemplateGenerator(
                get(EventManager.class)
        ));
        objects.put(ProxyManager.class, new DefaultProxyManager(
                get(EventManager.class),
                get(ProxyConnector.class)));
        objects.put(GameListServlet.class, new GameListServlet(
                get(GameAccessor.class),
                get(TemplateGenerator.class)
        ));
        objects.put(GameClientServlet.class, new GameClientServlet(
                get(GameAccessor.class),
                get(ProxyManager.class),
                get(TemplateGenerator.class),
                get(Configuration.class)
        ));
        objects.put(ConfigurationServlet.class, new ConfigurationServlet(
                get(Configuration.class),
                get(TemplateGenerator.class)
        ));

        objects.put(WebManager.class, new JettyWebManager(
                get(EventManager.class),
                get(TemplateGenerator.class),
                new HashMap<String, Servlet>() {{
                    put("/games", Container.this.get(GameListServlet.class));
                    put("/game/*", Container.this.get(GameClientServlet.class));
                }}
        ));

        get(EventManager.class).broadcast(new ContainerInitializedEvent());
    }

    public <T> T get(Class<T> cls) {
        T instance =  (T) objects.get(cls);
        if (instance == null) {
            throw new IllegalArgumentException("Class "+cls+" cannot be found in the container");
        }
        return instance;
    }

    private int getConfigInt(Ini.Section sec, String key, int def) {
        String val = sec.fetch(key);
        if (val == null) {
            return def;
        } else {
            return Integer.parseInt(val);
        }
    }

    public void stop() {
        get(EventManager.class).broadcast(new ContainerStoppedEvent());
    }

    public static class ContainerInitializedEvent {}
    public static class ContainerStoppedEvent {}

}
