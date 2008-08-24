package org.twdata.twxbbs.web;

import org.twdata.twxbbs.GameAccessor;
import org.twdata.twxbbs.Game;
import org.twdata.twxbbs.util.Validate;
import org.twdata.twxbbs.config.*;
import org.twdata.twxbbs.config.impl.IniConfiguration;
import org.twdata.twxbbs.web.template.MiniTemplator;
import org.twdata.twxbbs.web.template.TemplateGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ini4j.Ini;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.*;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 20/08/2008
 * Time: 21:38:47
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationServlet extends HttpServlet {
    private final Configuration configuration;
    private final TemplateGenerator generator;

    public ConfigurationServlet(Configuration configuration, TemplateGenerator templateGenerator) {
        this.configuration = configuration;
        this.generator = templateGenerator;
    }

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

        renderForm(httpServletRequest, httpServletResponse, null);
    }

    private void renderForm(final HttpServletRequest req, HttpServletResponse httpServletResponse, final Map<String,List<String>> errors) throws IOException {
        generator.render("config.mt", httpServletResponse, new TemplateGenerator.TemplateCallback() {
            public void initTemplate(MiniTemplator template) throws MiniTemplator.VariableNotDefinedException, MiniTemplator.BlockNotDefinedException {
                if (errors != null) {
                    for (Map.Entry<String,List<String>> entry : errors.entrySet()) {
                        for (String err : entry.getValue()) {
                            template.setVariable("msg", err);
                            template.addBlock("error");
                        }
                    }
                    template.addBlock("errors");
                }
                setAll(template, GlobalKey.values());
                setAll(template, WebKey.values());
                setAll(template, ProxyKey.values());
            }
            private void setAll(MiniTemplator template, SectionKey[] keys) throws MiniTemplator.VariableNotDefinedException, MiniTemplator.BlockNotDefinedException {
                for (SectionKey key : keys) {
                    if (GlobalKey.Setup == key) {
                        continue;
                    }
                    String val = req.getParameter(key.getFullName());
                    if (val == null) {
                        val = configuration.get(key);
                    }
                    template.setVariable("key", key.getFullName());
                    template.setVariable("value", val);
                    template.setVariable("displayName", key.getDisplayName());
                    template.addBlock("setting");
                }
            }
        });
    }

    protected void doPost(final HttpServletRequest req, HttpServletResponse httpServletResponse) throws ServletException, IOException {

        Ini ini = new Ini();
        Map<String,List<String>> errors = new HashMap<String,List<String>>();

        File baseDir = null;
        String dirName = req.getParameter(GlobalKey.BaseDirectory.getFullName());
        List<String> baseDirErrors = GlobalKey.BaseDirectory.validate(dirName);
        if (baseDirErrors.size() > 0) {
            errors.put(GlobalKey.BaseDirectory.getFullName(), baseDirErrors);
        } else {
            baseDir = new File(dirName);
        }
        validateAndStore(ini, req, GlobalKey.values(), errors);
        validateAndStore(ini, req, WebKey.values(), errors);
        validateAndStore(ini, req, ProxyKey.values(), errors);

        if (errors.size() == 0) {
            final String baseUrl = ini.get(WebKey.SECTION_NAME).get(WebKey.BaseURL.name());
            final String port = ini.get(WebKey.SECTION_NAME).get(WebKey.Port.name());
            generator.render("config-reload.mt", httpServletResponse, new TemplateGenerator.TemplateCallback() {
                public void initTemplate(MiniTemplator template) throws MiniTemplator.VariableNotDefinedException, MiniTemplator.BlockNotDefinedException {
                    template.setVariable("baseUrl", baseUrl);
                    template.setVariable("port", port);
                }
            });
            httpServletResponse.flushBuffer();

            Writer writer = null;
            try {
                writer = new FileWriter(new File(baseDir, IniConfiguration.TWXBBS_INI));
                ini.store(writer);
                configuration.refresh(baseDir);
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        } else {
            renderForm(req, httpServletResponse, errors);
        }
    }

    private void validateAndStore(Ini ini, HttpServletRequest req, SectionKey[] keys, Map<String,List<String>> allErrors) {
        Ini.Section section = ini.new Section(keys[0].getSectionName());
        ini.put(keys[0].getSectionName(), section);
        for (SectionKey key : keys) {
            String val = req.getParameter(key.getFullName());
            // Only validate new entries
            if (!configuration.get(key).equals(val)) {
                List<String> errors = key.validate(val);
                if (errors.size() == 0) {
                    section.put(key.name(), val);
                } else {
                    allErrors.put(key.getFullName(), errors);
                }
            } else {
                section.put(key.name(), val);
            }

        }
    }
}