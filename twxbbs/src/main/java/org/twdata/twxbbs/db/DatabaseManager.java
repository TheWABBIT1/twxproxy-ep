package org.twdata.twxbbs.db;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 02/09/2008
 * Time: 8:17:11 PM
 * To change this template use File | Settings | File Templates.
 */
public interface DatabaseManager {

    Database createDatabase(String name);

    Database createDatabase(String name, Properties props);
}
