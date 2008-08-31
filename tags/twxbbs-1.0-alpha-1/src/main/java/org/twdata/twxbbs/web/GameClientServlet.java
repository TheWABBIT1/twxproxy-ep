package org.twdata.twxbbs.web;

import org.twdata.twxbbs.GameAccessor;
import org.twdata.twxbbs.Game;
import org.twdata.twxbbs.Player;
import org.twdata.twxbbs.GameRegistration;
import org.twdata.twxbbs.config.Configuration;
import org.twdata.twxbbs.proxy.ProxyManager;
import org.twdata.twxbbs.impl.DefaultGameRegistration;
import org.twdata.twxbbs.web.template.MiniTemplatorCache;
import org.twdata.twxbbs.web.template.MiniTemplator;
import org.twdata.twxbbs.web.template.TemplateGenerator;
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
import java.util.regex.Matcher;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 20/08/2008
 * Time: 21:38:47
 * To change this template use File | Settings | File Templates.
 */
public class GameClientServlet extends HttpServlet {
    private final GameAccessor gameAccessor;
    private final TemplateGenerator generator;
    private final ProxyManager proxyManager;
    private final Random random;
    private Logger log = LoggerFactory.getLogger(GameClientServlet.class);
    private final Pattern uriPattern = Pattern.compile("/game/([a-zA-Z])(?:/(play))?", Pattern.CASE_INSENSITIVE);

    private static final Map<String,String> messages = new HashMap<String,String>() {{
        put("missing.fields", "All fields must be filled out");
        put("invalid.password", "The password is incorrect, please try again");
    }};
    private final Configuration configuration;


    public GameClientServlet(GameAccessor gameAccessor, ProxyManager proxy, TemplateGenerator generator, Configuration config) {
        this.gameAccessor = gameAccessor;
        this.generator = generator;
        this.proxyManager = proxy;
        this.configuration = config;
        this.random = new Random();
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Matcher m = uriPattern.matcher(req.getRequestURI());
        if (!m.matches()) {
            res.sendError(400, "Invalid URI: "+req.getRequestURI());
            return;
        }

        final Game game = gameAccessor.getGame(m.group(1).charAt(0));
        String command = "view";
        if (m.group(2) != null)
            command = m.group(2).toLowerCase();

        if ("view".equalsIgnoreCase(command) && "get".equalsIgnoreCase(req.getMethod())) {
            viewGame(game, req, res);
        } else if ("play".equalsIgnoreCase(command) && "post".equalsIgnoreCase(req.getMethod())) {
            playGame(game, req, res);
        } else {
            res.sendError(404, "URI "+req.getRequestURI()+" with method "+req.getMethod()+" not allowed");
        }

    }

    protected void viewGame(final Game game, final HttpServletRequest req, HttpServletResponse res) throws IOException {
        generator.render("game-view.mt", res, new TemplateGenerator.TemplateCallback() {
            public void initTemplate(MiniTemplator template) throws MiniTemplator.VariableNotDefinedException, MiniTemplator.BlockNotDefinedException {
                template.setVariable("id", String.valueOf(game.getId()));
                template.setVariable("name", game.getName());
                template.setVariable("sectors", String.valueOf(game.getSectors()));
                template.setVariable("msg", (req.getParameter("msg") != null) ? messages.get(req.getParameter("msg")) : "");
            }
        });
    }

    protected void playGame(final Game game, final HttpServletRequest req, HttpServletResponse res) throws IOException {
        final String username = req.getParameter("username");
        final String password = req.getParameter("password");
        if (username == null || password == null) {
            res.sendRedirect("/game/"+game.getId()+"?msg=missing.fields");
        }

        final Player player = gameAccessor.getPlayer(username);
        if (player != null && !player.getPassword().equals(password)) {
            res.sendRedirect("/game/"+game.getId()+"?msg=invalid.password");
        }

        GameRegistration reg = new DefaultGameRegistration(player, game);

        final String token = String.valueOf(random.nextInt());
        proxyManager.registerClient(token, reg);

        generator.render("game-play.mt", res, new TemplateGenerator.TemplateCallback() {
            public void initTemplate(MiniTemplator template) throws MiniTemplator.VariableNotDefinedException, MiniTemplator.BlockNotDefinedException {
                template.setVariable("name", game.getName());
                template.setVariable("session", token);
                template.setVariable("proxyHost", configuration.getProxyHost());
                template.setVariable("proxyPort", String.valueOf(configuration.getProxyPort()));
            }
        });
    }
}