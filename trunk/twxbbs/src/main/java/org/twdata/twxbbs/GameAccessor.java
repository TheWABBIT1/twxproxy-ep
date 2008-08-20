package org.twdata.twxbbs;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 20/08/2008
 * Time: 21:39:40
 * To change this template use File | Settings | File Templates.
 */
public interface GameAccessor {

    Set<Game> getGames();

    Player getPlayer(String name);
}
