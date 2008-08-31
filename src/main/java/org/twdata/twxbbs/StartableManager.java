package org.twdata.twxbbs;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 20/08/2008
 * Time: 22:57:05
 * To change this template use File | Settings | File Templates.
 */
public interface StartableManager {
    void start() throws IOException;
    void stop();
    boolean hasStarted();
}
