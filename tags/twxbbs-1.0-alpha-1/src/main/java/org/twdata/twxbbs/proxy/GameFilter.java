package org.twdata.twxbbs.proxy;

import org.apache.mina.common.IoFilterAdapter;
import org.apache.mina.common.IoSession;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 10/08/2008
 * Time: 00:41:48
 * To change this template use File | Settings | File Templates.
 */
public class GameFilter extends IoFilterAdapter {

    @Override
    public void messageReceived(final NextFilter nextFilter, IoSession session,
            Object message) throws Exception {

        String line = message.toString();
        if (line.contains("Game"))
            System.out.println("Game: "+line);

        nextFilter.messageReceived(session, message);
    }
}
