package org.twdata.twxbbs;

import java.util.Set;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 20/08/2008
 * Time: 21:39:40
 * To change this template use File | Settings | File Templates.
 */
public interface GameAccessor {

    Collection<Game> getGames();

    Game getGame(char id);

    Player getPlayer(String name);
}
