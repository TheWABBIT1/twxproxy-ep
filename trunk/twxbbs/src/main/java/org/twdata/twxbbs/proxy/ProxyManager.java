package org.twdata.twxbbs.proxy;

import java.security.KeyStore;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 18/08/2008
 * Time: 21:42:08
 * To change this template use File | Settings | File Templates.
 */
public interface ProxyManager {

    void register(KeyStore keyStore);

    void disconnect(String key);

}
