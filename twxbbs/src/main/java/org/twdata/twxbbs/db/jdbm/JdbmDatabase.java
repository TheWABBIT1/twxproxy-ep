package org.twdata.twxbbs.db.jdbm;

import org.twdata.twxbbs.db.Database;
import org.twdata.twxbbs.db.DatabaseException;
import jdbm.RecordManager;
import jdbm.htree.HTree;

import java.util.Map;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 02/09/2008
 * Time: 8:41:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class JdbmDatabase implements Database {
    private final RecordManager recordManager;

    public JdbmDatabase(RecordManager recordManager) {
        this.recordManager = recordManager;
    }

    public Map<String, String> getMap(String name) {
        // create or load fruit basket (hashtable of fruits)
        HTree htree = null;
        try {
            long recid = recordManager.getNamedObject(name);
            if ( recid != 0 ) {
                htree = HTree.load(recordManager, recid );
            } else {
                htree = HTree.createInstance(recordManager);
                recordManager.setNamedObject(name, htree.getRecid());
            }
            return new HTreeMap(htree);
        } catch (IOException e) {
            throw new DatabaseException(e);
        }
    }

    public void close() {
        try {
            recordManager.close();
        } catch (IOException e) {
            throw new DatabaseException(e);
        }
    }

    public void commit() {
        try {
            recordManager.commit();
        } catch (IOException e) {
            throw new DatabaseException(e);
        }
    }

    public void rollback() {
        try {
            recordManager.rollback();
        } catch (IOException e) {
            throw new DatabaseException(e);
        }
    }
}
