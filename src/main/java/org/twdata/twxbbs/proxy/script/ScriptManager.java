package org.twdata.twxbbs.proxy.script;

import org.twdata.twxbbs.StartableManager;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 31/08/2008
 * Time: 12:11:21 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ScriptManager extends StartableManager {
    void stop();

    void start();

    List<Thread> startScripts(ScriptType type, ScriptVariablesFactory varFactory);
}
