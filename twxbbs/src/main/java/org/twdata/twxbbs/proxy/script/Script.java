package org.twdata.twxbbs.proxy.script;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 26/08/2008
 * Time: 10:53:37 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Script {

    void run(Map<String,Object> args);
}
