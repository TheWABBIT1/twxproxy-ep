package org.twdata.twxbbs.db;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 02/09/2008
 * Time: 8:18:11 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Database {
    Map<String,String> getMap(String name);

    void close();
    void commit();
    void rollback();
}
