package org.twdata.twxbbs.web;

import org.twdata.twxbbs.GameAccessor;
import org.twdata.twxbbs.Game;
import org.twdata.twxbbs.web.template.MiniTemplatorCache;
import org.twdata.twxbbs.web.template.MiniTemplator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.regex.Pattern;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 20/08/2008
 * Time: 21:38:47
 * To change this template use File | Settings | File Templates.
 */
public class GameListServlet extends HttpServlet {
    private final GameAccessor gameAccessor;
    private final MiniTemplatorCache templateCache;
    private Logger log = LoggerFactory.getLogger(GameListServlet.class);

    public GameListServlet(GameAccessor gameAccessor, MiniTemplatorCache templateCache) {
        this.gameAccessor = gameAccessor;
        this.templateCache = templateCache;
    }

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

        MiniTemplator template;
        try {
            template = templateCache.get("games.mt");

            for (Game game : gameAccessor.getGames()) {
                template.setVariable("id", String.valueOf(game.getId()));
                template.setVariable("name", game.getName());
                template.setVariable("sectors", String.valueOf(game.getSectors()));
                template.addBlock("game");
            }
            httpServletResponse.setContentType("text/html");
            StringWriter writer = new StringWriter();
            template.generateOutput(writer);
            httpServletResponse.getWriter().write(writer.toString());
        } catch (MiniTemplator.TemplateSyntaxException e) {
            log.error("Invalid template", e);
            httpServletResponse.sendError(500, "Internal template parsing error");
            return;
        } catch (MiniTemplator.VariableNotDefinedException e) {
            log.error("Invalid variable", e);
            httpServletResponse.sendError(500, "Internal template parsing error");
            return;
        } catch (MiniTemplator.BlockNotDefinedException e) {
            log.error("Invalid block", e);
            httpServletResponse.sendError(500, "Internal template parsing error");
            return;
        }
    }
}
