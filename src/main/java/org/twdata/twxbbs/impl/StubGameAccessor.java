package org.twdata.twxbbs.impl;

import org.twdata.twxbbs.GameAccessor;
import org.twdata.twxbbs.Game;
import org.twdata.twxbbs.Player;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 20/08/2008
 * Time: 22:20:59
 * To change this template use File | Settings | File Templates.
 */
public class StubGameAccessor implements GameAccessor {

    private final Map<Character,Game> games = new HashMap<Character,Game>() {{
        put('A',new DefaultGame('A', "The first great game", 5000));
        put('B',new DefaultGame('B', "The second ok game", 10000));
        put('C',new DefaultGame('C', "The third shit game", 20000));
    }};

    private final Map<String,Player> players = new HashMap<String,Player>() {{
        put("bob", new DefaultPlayer("bob", "bob"));
        put("killer", new DefaultPlayer("killer", "bob"));
        put("sarah", new DefaultPlayer("sarah", "sarah"));
    }};
    public Collection<Game> getGames() {
        return games.values();
    }

    public Game getGame(char id) {
        return games.get(id);
    }

    public Player getPlayer(String name) {
        return players.get(name);
    }
}
