package org.twdata.twxbbs.impl;

import org.twdata.twxbbs.GameRegistration;
import org.twdata.twxbbs.Game;
import org.twdata.twxbbs.Player;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 22/08/2008
 * Time: 1:53:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultGameRegistration implements GameRegistration {
    private final Player player;
    private final Game game;

    public DefaultGameRegistration(Player player, Game game) {
        this.player = player;
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }
}
