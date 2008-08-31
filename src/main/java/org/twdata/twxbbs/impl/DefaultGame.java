package org.twdata.twxbbs.impl;

import org.twdata.twxbbs.Game;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 20/08/2008
 * Time: 22:22:11
 * To change this template use File | Settings | File Templates.
 */
public class DefaultGame implements Game {
    private final char id;
    private final String name;
    private final int sectors;

    public DefaultGame(char id, String name, int sectors) {
        this.id = id;
        this.name = name;
        this.sectors = sectors;
    }

    public int getSectors() {
        return sectors;
    }

    public String getName() {
        return name;
    }

    public char getId() {
        return id;
    }
}
