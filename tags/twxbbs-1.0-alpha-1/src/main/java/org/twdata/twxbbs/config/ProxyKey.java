package org.twdata.twxbbs.config;

import org.twdata.twxbbs.config.SectionKey;
import org.twdata.twxbbs.util.Validate;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 24/08/2008
 * Time: 2:51:52 PM
 * To change this template use File | Settings | File Templates.
 */
public enum ProxyKey implements SectionKey {
    Port("8023", "Proxy Port"),
    Host("localhost", "Proxy Host"),
    TWGSHost("localhost", "TWGS Host"),
    TWGSPort("2323", "TWGS Port");

    private String defaultValue;
    public static final String SECTION_NAME = "Proxy";
    private String displayName;

    ProxyKey(String def, String displayName) {
        this.defaultValue = def;
        this.displayName = displayName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getFullName() {
        return getSectionName() + "." + this.name();
    }

    public List<String> validate(String value) {
        List<String> errors = new ArrayList<String>();
        switch (this) {
            case Port :     if (!Validate.isIntegerInRange(value, 1, 65500)) errors.add("Invalid proxy port number: "+value);
                            else if (!Validate.isPortFree(value)) errors.add("Proxy port "+value+" is currently in use");
                            break;
            case Host :     if (!Validate.isNotEmpty(value)) errors.add("Proxy host must be defined");
                            break;
            case TWGSHost:  if (!Validate.isNotEmpty(value)) errors.add("TWGS host must be defined");
                            break;
            case TWGSPort:  if (!Validate.isIntegerInRange(value, 1, 65500)) errors.add("Invalid TWGS port number: "+value);
                            break;
        }
        return errors;
    }

    public String getSectionName() {
        return SECTION_NAME;
    }

    public String getDisplayName() {
        return displayName;
    }
}
