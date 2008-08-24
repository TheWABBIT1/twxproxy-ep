package org.twdata.twxbbs.config.impl;

import org.twdata.twxbbs.config.*;
import org.twdata.twxbbs.event.EventManager;
import org.twdata.twxbbs.event.EventListener;
import org.twdata.twxbbs.Container;
import org.ini4j.Ini;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 24/08/2008
 * Time: 1:10:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class IniConfiguration implements Configuration {

    private final EventManager eventManager;
    private File baseDir;

    private final Map<SectionKey,String> values = new HashMap<SectionKey,String>();
    public static final String TWXBBS_INI = "twxbbs.ini";

    public IniConfiguration(File baseDir,  EventManager eventManager) {
        this.eventManager = eventManager;
        eventManager.register(this);
        this.baseDir = baseDir;
    }

    @EventListener
    public void containerInitialized(Container.ContainerInitializedEvent event) {
        refresh(baseDir);
    }

    public void refresh(File baseDir) {
        Ini config;
        //values.clear();
        this.baseDir = baseDir;
        try {
            Reader reader = null;
            if (baseDir != null) {
                File iniFile = new File(baseDir, TWXBBS_INI);
                if (iniFile.exists()) {
                    reader = new FileReader(iniFile);
                }
            }
            if (reader == null) {
                reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("org/twdata/twxbbs/default.ini"));
            }
            config = new Ini(reader);

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to find a suitable ini file", e);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load ini file", e);
        }

        setAndValidate(config.get(WebKey.SECTION_NAME), WebKey.values());
        setAndValidate(config.get(ProxyKey.SECTION_NAME), ProxyKey.values());
        setAndValidate(config.get(GlobalKey.SECTION_NAME), GlobalKey.values());
        eventManager.broadcast(new ConfigurationRefreshedEvent(this));


    }

    private void setAndValidate(Ini.Section section, SectionKey[] keys) {
        for (SectionKey key : keys) {
            String val = fetch(section, key);
            // Only validate new entries
            if (get(key) != null && !get(key).equals(val)) {
                List<String> errors = key.validate(val);
                if (errors.size() == 0) {
                    values.put(key, val);
                } else {
                    throw new IllegalArgumentException("Errors in setting "+key.getFullName()+": "+errors);
                }
            } else {
                values.put(key, val);
            }

        }
    }

    public int getWebPort() {
        return getInt(WebKey.Port);
    }

    public String getWebBaseUrl() {
        return get(WebKey.BaseURL);
    }

    public String getProxyHost() {
        return get(ProxyKey.Host);
    }

    public int getProxyPort() {
        return getInt(ProxyKey.Port);
    }

    public String getTwgsHost() {
        return get(ProxyKey.TWGSHost);
    }

    public int getTwgsPort() {
        return getInt(ProxyKey.TWGSPort);
    }

    public File getBaseDir() {
        return baseDir;
    }

    public boolean isSetup() {
        return getInt(GlobalKey.Setup) == 1;
    }

    public int getInt(SectionKey key) {
        return Integer.parseInt(values.get(key));
    }

    public String get(SectionKey key) {
        return values.get(key);
    }

    private String fetch(Ini.Section section, SectionKey key) {
        String obj = section.fetch(key.name());
        if (obj == null) {
            return key.getDefaultValue();
        } else {
            return obj;
        }
    }
}
