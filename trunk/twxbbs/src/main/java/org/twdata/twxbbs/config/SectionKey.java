package org.twdata.twxbbs.config;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 24/08/2008
 * Time: 3:00:55 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SectionKey {
    String getFullName();
    String getDefaultValue();
    List<String> validate(String value);
    String name();
    String getSectionName();

    String getDisplayName();
}
