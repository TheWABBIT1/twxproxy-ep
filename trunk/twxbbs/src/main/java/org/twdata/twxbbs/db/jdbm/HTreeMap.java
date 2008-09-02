package org.twdata.twxbbs.db.jdbm;

import jdbm.htree.HTree;
import jdbm.helper.FastIterator;

import java.util.*;
import java.io.IOException;

import org.twdata.twxbbs.db.DatabaseException;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 02/09/2008
 * Time: 8:42:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class HTreeMap implements Map<String,String> {
    private final HTree htree;

    public HTreeMap(HTree htree) {
        if (htree == null) {
            throw new IllegalArgumentException("HTree must not be null");
        }
        this.htree = htree;
    }

    public int size() {
        int count = 0;
        try {
            FastIterator itr = htree.keys();
            while (itr.next() != null) {
                count++;
            }
        } catch (IOException e) {
            throw new DatabaseException(e);
        }
        return count;
    }

    public boolean isEmpty() {
        try {
            return (htree.keys().next() == null);
        } catch (IOException e) {
            throw new DatabaseException(e);
        }
    }

    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    public boolean containsValue(Object value) {
        try {
            FastIterator itr = htree.values();
            Object obj;
            while ((obj = itr.next()) != null) {
                if (obj.equals(value)) {
                    return true;
                }
            }
        } catch (IOException e) {
            throw new DatabaseException(e);
        }
        return false;
    }

    public String get(Object key) {
        try {
            return (String) htree.get(key);
        } catch (IOException e) {
            throw new DatabaseException(e);
        }
    }

    public String put(String key, String value) {
        try {
            htree.put(key, value);
            return value;
        } catch (IOException e) {
            throw new DatabaseException(e);
        }
    }

    public String remove(Object key) {
        try {
            htree.remove(key);
            return (String) key;
        } catch (IOException e) {
            throw new DatabaseException(e);
        }
    }

    public void putAll(Map<? extends String, ? extends String> m) {
        for (Map.Entry entry : m.entrySet()) {
            try {
                htree.put(entry.getKey(), entry.getValue());
            } catch (IOException e) {
                throw new DatabaseException(e);
            }
        }
    }

    public void clear() {
        Set<String> keys = keySet();
        for (String key : keys) {
            remove(key);
        }
    }

    public Set<String> keySet() {
        HashSet<String> keys = new HashSet<String>();
        FastIterator itr = null;
        try {
            itr = htree.keys();
            Object obj;
            while ((obj = itr.next()) != null) {
                keys.add((String) obj);
            }
        } catch (IOException e) {
            throw new DatabaseException(e);
        }
        return keys;
    }

    public Collection<String> values() {
        List<String> values = new ArrayList<String>();
        FastIterator itr = null;
        try {
            itr = htree.values();
            Object obj;
            while ((obj = itr.next()) != null) {
                values.add((String) obj);
            }
        } catch (IOException e) {
            throw new DatabaseException(e);
        }
        return values;
    }

    public Set<Entry<String, String>> entrySet() {
        HashMap<String,String> entries = new HashMap<String,String>();
        FastIterator itr = null;
        try {
            itr = htree.keys();
            Object obj;
            while ((obj = itr.next()) != null) {
                entries.put((String)obj, (String)htree.get(obj));
            }
        } catch (IOException e) {
            throw new DatabaseException(e);
        }
        return entries.entrySet();
    }
}
