package de.mud.jta.plugin.hips;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 23/08/2008
 * Time: 2:17:08 PM
 * To change this template use File | Settings | File Templates.
 */
public interface HipsMessageListener {
    void handle(String name, String message);
}
