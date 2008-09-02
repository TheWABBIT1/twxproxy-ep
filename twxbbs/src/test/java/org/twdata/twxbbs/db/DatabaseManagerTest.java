package org.twdata.twxbbs.db;

import junit.framework.TestCase;
import org.twdata.twxbbs.db.jdbm.JdbmDatabaseManager;
import org.twdata.twxbbs.event.EventManager;
import org.twdata.twxbbs.event.impl.DefaultEventManager;
import org.twdata.twxbbs.config.ConfigurationRefreshedEvent;
import org.twdata.twxbbs.config.Configuration;
import com.mockobjects.dynamic.Mock;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 02/09/2008
 * Time: 9:02:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseManagerTest extends TestCase {

    public void testCreateAndPopulate() throws IOException {
        EventManager eventManager = new DefaultEventManager();
        DatabaseManager mgr = new JdbmDatabaseManager(eventManager);
        Mock mockConfiguration = new Mock(Configuration.class);
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        mockConfiguration.expectAndReturn("getBaseDir", tmpDir);
        eventManager.broadcast(new ConfigurationRefreshedEvent((Configuration) mockConfiguration.proxy()));

        Database db = mgr.createDatabase("test");
        File dbFile = new File(new File(tmpDir, "data"), "test.db");
        try {
            Map<String,String> sectors = db.getMap("sectors");
            sectors.put("foo", "bar");
            assertEquals("bar", sectors.get("foo"));
            assertTrue(sectors.containsKey("foo"));
            assertTrue(sectors.containsValue("bar"));
            assertEquals(1, sectors.size());
            assertTrue(dbFile.exists());
        } finally {
            dbFile.delete();
        }

        mockConfiguration.verify();

    }
}
