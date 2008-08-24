package org.twdata.twxbbs.web;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;
import org.mortbay.jetty.handler.HandlerWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 24/08/2008
 * Time: 10:15:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class RedirectHandler extends HandlerWrapper {
    private final String path;

    public RedirectHandler(String path) {
        this.path = path;
    }

    public void handle(String target, HttpServletRequest request, HttpServletResponse httpServletResponse, int dispatchers) throws IOException, ServletException {
        if (("".equals(target) || "/".equals(target)) && dispatchers == Handler.REQUEST) {
            ((Request)request).setHandled(true);
            httpServletResponse.sendRedirect(path);
        }
    }
}
