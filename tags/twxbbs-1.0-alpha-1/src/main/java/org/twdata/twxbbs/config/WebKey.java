package org.twdata.twxbbs.config;

import org.twdata.twxbbs.config.SectionKey;
import org.twdata.twxbbs.util.Validate;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 24/08/2008
 * Time: 2:51:16 PM
 * To change this template use File | Settings | File Templates.
 */
public enum WebKey implements SectionKey {
    Port("8080", "Web Port"),
    BaseURL("http://localhost", "Web URL");

    private String defaultValue;
    public static final String SECTION_NAME = "Web";
    private String displayName;

    WebKey(String def, String displayName) {
        this.defaultValue = def;
        this.displayName = displayName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getFullName() {
        return SECTION_NAME + "." + this.name();
    }

    public List<String> validate(String value) {
        List<String> errors = new ArrayList<String>();
        switch (this) {
            case Port :     if (!Validate.isIntegerInRange(value, 1, 65500)) errors.add("Invalid web port number: "+value);
                            else if (!Validate.isPortFree(value)) errors.add("Web port "+value+" is currently in use");
                            break;
            case BaseURL:  if (!Validate.isNotEmpty(value)) errors.add("Web base URL must be defined"); else {
                                if (!value.toLowerCase().startsWith("http")) errors.add("Web base URL should be either HTTP or HTTPS");
                            }
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
