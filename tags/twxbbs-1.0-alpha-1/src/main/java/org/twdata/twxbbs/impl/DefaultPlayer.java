package org.twdata.twxbbs.impl;

import org.twdata.twxbbs.Player;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 20/08/2008
 * Time: 22:25:09
 * To change this template use File | Settings | File Templates.
 */
public class DefaultPlayer implements Player {
    private final String name;
    private final String password;

    public DefaultPlayer(String name, String password) {
        this.name = name;
        this.password = password;
    }


    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
