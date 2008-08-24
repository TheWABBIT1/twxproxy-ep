package org.twdata.twxbbs.config;

import org.twdata.twxbbs.config.SectionKey;
import org.twdata.twxbbs.util.Validate;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 24/08/2008
 * Time: 2:52:19 PM
 * To change this template use File | Settings | File Templates.
 */
public enum GlobalKey implements SectionKey {
    Setup("0", "Setup"),
    BaseDirectory(new File(".").getAbsolutePath(), "Base Directory");

    private String defaultValue;

    public static final String SECTION_NAME = "Global";
    private String displayName;

    GlobalKey(String def, String displayName) {
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
            case Setup :            if (!Validate.isIntegerInRange(value, 0, 1)) errors.add("Invalid value: "+value);
                                    break;
            case BaseDirectory :    if (!Validate.isNotEmpty(value)) errors.add("Base directory is required");
                                    else {
                                        File baseDir = new File(value);
                                        if (!baseDir.exists()) {
                                            errors.add("Base directory "+value+" doesn't exist");
                                        } else if (!baseDir.isDirectory()) {
                                            errors.add("Base directory "+value+" is not a directory");
                                        } else if (!baseDir.canWrite()) {
                                            errors.add("Base directory "+value+" is not writable");
                                        }
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
