package org.twdata.twxbbs.config;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 24/08/2008
 * Time: 1:34:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationRefreshedEvent {
    private final Configuration configuration;

    public ConfigurationRefreshedEvent(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
