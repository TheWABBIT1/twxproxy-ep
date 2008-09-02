package org.twdata.twxbbs.db.jdbm;

import org.twdata.twxbbs.event.EventManager;
import org.twdata.twxbbs.event.EventListener;
import org.twdata.twxbbs.Container;
import org.twdata.twxbbs.db.DatabaseManager;
import org.twdata.twxbbs.db.Database;
import org.twdata.twxbbs.db.DatabaseException;
import org.twdata.twxbbs.config.Configuration;
import org.twdata.twxbbs.config.ConfigurationRefreshedEvent;

import java.io.IOException;
import java.io.File;
import java.util.Properties;

import jdbm.RecordManagerFactory;
import jdbm.RecordManager;
import jdbm.RecordManagerOptions;
import jdbm.htree.HTree;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 02/09/2008
 * Time: 8:34:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class JdbmDatabaseManager implements DatabaseManager {
    private File dataDir;
    public JdbmDatabaseManager(EventManager eventManager) {
        eventManager.register(this);
    }

    @EventListener
    public synchronized void refresh(ConfigurationRefreshedEvent event) throws IOException {
        Configuration config = event.getConfiguration();
        File baseDir = config.getBaseDir();
        dataDir = new File(baseDir, "data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }


    public Database createDatabase(String name) {
        Properties props = new Properties();
        props.put(RecordManagerOptions.AUTO_COMMIT, "true");
        props.put(RecordManagerOptions.THREAD_SAFE, "true");
        return createDatabase(name, props);

    }

    public Database createDatabase(String name, Properties props) {
        try {
            return new JdbmDatabase(RecordManagerFactory.createRecordManager( new File(dataDir, name).getAbsolutePath(), props));
        } catch (IOException e) {
            throw new DatabaseException(e);
        }
    }
}
