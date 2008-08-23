package org.twdata.twxbbs.web;

import org.twdata.twxbbs.GameAccessor;
import org.twdata.twxbbs.Game;
import org.twdata.twxbbs.web.template.MiniTemplator;
import org.twdata.twxbbs.web.template.TemplateGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 20/08/2008
 * Time: 21:38:47
 * To change this template use File | Settings | File Templates.
 */
public class GameListServlet extends HttpServlet {
    private final GameAccessor gameAccessor;
    private final TemplateGenerator generator;

    public GameListServlet(GameAccessor gameAccessor, TemplateGenerator templateGenerator) {
        this.gameAccessor = gameAccessor;
        this.generator = templateGenerator;
    }

    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

        generator.render("games.mt", httpServletResponse, new TemplateGenerator.TemplateCallback() {
            public void initTemplate(MiniTemplator template) throws MiniTemplator.VariableNotDefinedException, MiniTemplator.BlockNotDefinedException {
                for (Game game : gameAccessor.getGames()) {
                    template.setVariable("id", String.valueOf(game.getId()));
                    template.setVariable("name", game.getName());
                    template.setVariable("sectors", String.valueOf(game.getSectors()));
                    template.addBlock("game");
                }
            }
        });
    }
}
