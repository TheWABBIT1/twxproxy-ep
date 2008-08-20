package org.twdata.twxbbs;

import org.twdata.twxbbs.impl.StubGameAccessor;
import org.twdata.twxbbs.web.template.MiniTemplatorCache;
import org.twdata.twxbbs.web.GameListServlet;
import org.twdata.twxbbs.web.WebManager;
import org.twdata.twxbbs.web.JettyWebManager;
import org.twdata.twxbbs.proxy.ProxyManager;
import org.twdata.twxbbs.proxy.DefaultProxyManager;
import org.ini4j.Ini;

import javax.servlet.Servlet;
import java.util.Map;
import java.util.HashMap;
import java.io.FileReader;
import java.io.Reader;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 20/08/2008
 * Time: 22:17:43
 * To change this template use File | Settings | File Templates.
 */
public class Container {
    private Map<Class<?>,Object> objects = new HashMap<Class<?>,Object>();

    Container(Reader iniReader) throws IOException {

        Ini config = new Ini(iniReader);
        Ini.Section proxyConfig = config.get("Proxy");
        Ini.Section webConfig = config.get("Web");

        objects.put(GameAccessor.class, new StubGameAccessor());
        objects.put(MiniTemplatorCache.class, new MiniTemplatorCache());
        objects.put(GameListServlet.class, new GameListServlet(
                get(GameAccessor.class),
                get(MiniTemplatorCache.class)
        ));
        objects.put(ProxyManager.class, new DefaultProxyManager(
                getConfigInt(proxyConfig, "Port", 8023),
                proxyConfig.fetch("TWGSHost"),
                getConfigInt(proxyConfig, "TWGSPort", 2002)
        ));
        objects.put(WebManager.class, new JettyWebManager(
                getConfigInt(webConfig, "Port", 8080),
                new HashMap<String, Servlet>() {{
                    put("/games", Container.this.get(GameListServlet.class));
                }}
        ));
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

}
