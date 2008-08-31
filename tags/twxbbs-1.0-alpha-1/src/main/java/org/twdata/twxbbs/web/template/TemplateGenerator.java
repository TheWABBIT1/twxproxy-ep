package org.twdata.twxbbs.web.template;

import org.twdata.twxbbs.Game;
import org.twdata.twxbbs.util.Validate;
import org.twdata.twxbbs.config.ConfigurationRefreshedEvent;
import org.twdata.twxbbs.config.Configuration;
import org.twdata.twxbbs.event.EventManager;
import org.twdata.twxbbs.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.io.IOException;
import java.io.File;
import java.util.regex.Pattern;
import java.nio.charset.Charset;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 22/08/2008
 * Time: 1:10:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class TemplateGenerator {
    private MiniTemplatorCache templateCache;
    private static final Logger log = LoggerFactory.getLogger(TemplateGenerator.class);

    public TemplateGenerator(EventManager eventManager) {
        eventManager.register(this);
    }

    @EventListener
    public synchronized void refresh(ConfigurationRefreshedEvent event) {
        Configuration config = event.getConfiguration();
        File templateDir = new File(config.getBaseDir(), "templates");
        this.templateCache = new MiniTemplatorCache(Charset.defaultCharset(), templateDir);
    }

    public synchronized boolean render(String name, HttpServletResponse res, TemplateCallback callback) throws IOException {
        Validate.notNull(templateCache);
        MiniTemplator template;
        try {
            template = templateCache.get(name);

            callback.initTemplate(template);

            res.setContentType("text/html");
            StringWriter writer = new StringWriter();
            template.generateOutput(writer);
            res.getWriter().write(writer.toString());
            return false;
        } catch (MiniTemplator.TemplateSyntaxException e) {
            log.error("Invalid template", e);
            res.sendError(500, "Internal template parsing error");
            return true;
        } catch (MiniTemplator.VariableNotDefinedException e) {
            log.error("Invalid variable", e);
            res.sendError(500, "Template variable not defined: "+e.getMessage()+" in template "+name);
            return true;
        } catch (MiniTemplator.BlockNotDefinedException e) {
            log.error("Invalid block", e);
            res.sendError(500, "Template block not defined: "+e.getMessage()+" in template "+name);
            return true;
        }
    }

    public static interface TemplateCallback {
        void initTemplate(MiniTemplator template) throws MiniTemplator.VariableNotDefinedException, MiniTemplator.BlockNotDefinedException;
    }
}
