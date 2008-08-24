package org.twdata.twxbbs.config;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 24/08/2008
 * Time: 1:07:55 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Configuration {
    int getWebPort();

    String getWebBaseUrl();

    String getProxyHost();
    int getProxyPort();

    String getTwgsHost();
    int getTwgsPort();

    File getBaseDir();

    boolean isSetup();

    void refresh(File baseDir);

    String get(SectionKey key);
}
