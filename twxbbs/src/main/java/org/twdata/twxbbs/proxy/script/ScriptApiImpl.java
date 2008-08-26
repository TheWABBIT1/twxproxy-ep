package org.twdata.twxbbs.proxy.script;

import org.apache.mina.common.IoSession;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoFilter;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 26/08/2008
 * Time: 11:07:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScriptApiImpl implements ScriptApi {
    private final ScriptLexer lexer;
    private ScriptLexer.Match lastMatch;
    private final TextSender sender;

    public ScriptApiImpl(ScriptLexer lexer, TextSender sender) {
        this.lexer = lexer;
        this.sender = sender;
    }

    public String getCurrentLine() {
        return lexer.getCurrentLine();
    }

    public String getMatchedLine() {
        return (lastMatch != null ? lastMatch.getLastLine() : null);
    }

    public void setTextTrigger(String id, String text) {
        lexer.addTextTrigger(id, text);
    }

    public void setTextLineTrigger(String id, String text) {
        lexer.addTextLineTrigger(id, text);
    }

    public void send(String text) throws Exception {
        sender.send(text);
    }

    public String pause() {
        ScriptLexer.Match match;
        try {
            match = lexer.waitForTriggers();
        } catch (IOException e) {
            throw new RuntimeException("Problem lexing the stream", e);
        } catch (InterruptedException e) {
            throw new RuntimeException("Script stopped", e);
        }
        if (match != null) {
            lastMatch = match;
            return match.getMatchedId();
        } else {
            return null;
        }
    }

    public void killTextTrigger(String id) {
        lexer.removeTextTrigger(id);
    }

    public static interface TextSender {
        void send(String text) throws Exception;
    }
}
