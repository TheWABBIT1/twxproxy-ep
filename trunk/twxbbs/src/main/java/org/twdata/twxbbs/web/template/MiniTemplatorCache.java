package org.twdata.twxbbs.web.template;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * A cache manager for MiniTemplator objects.
 * This class is used to cache MiniTemplator objects in memory, so that
 * each template file is only read and parsed once.
 * <p/>
 * <p/>
 * Home page: <a href="http://www.source-code.biz/MiniTemplator" target="_top">www.source-code.biz/MiniTemplator</a><br>
 * License: This module is released under the <a href="http://www.gnu.org/licenses/lgpl.html" target="_top">GNU/LGPL</a> license.<br>
 * Copyright 2003-2006: Christian d'Heureuse, Inventec Informatik AG, Switzerland. All rights reserved.<br>
 * This product is provided "as is" without warranty of any kind.<br>
 * <p/>
 * <p/>
 * Version history:<br>
 * 2004-11-06 chdh: Module created.<br>
 * 2004-11-07 chdh: Method "clear" added.<br>
 * 2006-07-07 chdh: Extended constructor with <code>charset</code> argument added.
 */
public class MiniTemplatorCache {

    private Charset charset;                      // charset used for file i/o
    private HashMap<String, MiniTemplator> cache;               // buffered MiniTemplator objects
    private final boolean disabled;

    /**
     * Creates a new MiniTemplatorCache object.
     */
    public MiniTemplatorCache() {
        this(Charset.defaultCharset());
    }

    /**
     * Creates a new MiniTemplatorCache object.
     *
     * @param charset             the character set to be used for reading template files and writing output files.
     */
    public MiniTemplatorCache(Charset charset) {
        this.charset = charset;
        cache = new HashMap<String, MiniTemplator>();
        this.disabled = !Boolean.getBoolean("cache");
    }

    /**
     * Returns a cloned MiniTemplator object from the cache.
     * If there is not yet a MiniTemplator object with the specified <code>templateFileName</code>
     * in the cache, a new MiniTemplator object is created and stored in the cache.
     * Then the cached MiniTemplator object is cloned and the clone object is returned.
     *
     * @param templateFileName the name of the template file.
     * @return a cloned and reset MiniTemplator object.
     */
    public MiniTemplator get(String templateFileName)
            throws IOException, MiniTemplator.TemplateSyntaxException {
        return getClone(templateFileName);
    }

    private synchronized MiniTemplator getClone(String templateFileName)
            throws IOException, MiniTemplator.TemplateSyntaxException {
        MiniTemplator mt = cache.get(templateFileName);
        if (mt == null) {
            InputStream in = null;
            try {
                in = getClass().getClassLoader().getResourceAsStream("org/twdata/twxbbs/web/"+templateFileName);
                if (in == null) {
                    throw new IllegalArgumentException("Template "+templateFileName+" not found");
                }
                mt = new MiniTemplator(new InputStreamReader(in, charset));
                if (!disabled) {
                    cache.put(templateFileName, mt);
                }
            } finally {
                if (in != null)
                    in.close();
            }
        }


        return mt.cloneReset();
    }

    /**
     * Clears the cache.
     */
    public synchronized void clear() {
        cache.clear();
    }

} // end class MiniTemplatorCache
