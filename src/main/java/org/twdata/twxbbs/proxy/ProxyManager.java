package org.twdata.twxbbs.proxy;

import org.twdata.twxbbs.GameRegistration;
import org.twdata.twxbbs.StartableManager;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 18/08/2008
 * Time: 21:42:08
 * To change this template use File | Settings | File Templates.
 */
public interface ProxyManager extends StartableManager {

    void registerClient(String sessionToken, GameRegistration reg);

    void unregisterClient(String sessionToken);

}
