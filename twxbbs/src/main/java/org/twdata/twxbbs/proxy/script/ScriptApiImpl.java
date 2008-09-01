package org.twdata.twxbbs.proxy.script;

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
        return (lastMatch != null ? lastMatch.getMatchedText() : null);
    }

    public void setTextTrigger(String id, String text) {
        lexer.addTextTrigger(id, text);
    }

    public void setTextLineTrigger(String id, String text) {
        lexer.addTextLineTrigger(id, text);
    }

    public void setCapturingTextTrigger(String id, String text) {
        lexer.addCapturingTextTrigger(id, text);
    }

    public void setCapturingTextLineTrigger(String id, String text) {
        lexer.addCapturingTextLineTrigger(id, text);
    }

    public void send(String text) throws Exception {
        if (text != null) {
            text = text.replace("*", "\r\n");
        }
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

    public ScriptLexer getScriptLexer() {
        return lexer;
    }

    public void killTextTrigger(String id) {
        lexer.removeTextTrigger(id);
    }

    public void setTimeout(long timeout) {
        lexer.setTimeout(timeout);
    }

    public String stripAnsi(String text) {
        return stripAnsi(text.getBytes(), text.length());
    }

    int NORMAL = 0, ESCAPE = 1, ESCAPE2 = 2, ESCAPE_STRING = 3;
    int ansiState = NORMAL;

    //hand made lexer to strip out pesky ansi escape codes
    //we don't want the overhead of another lexer, and we can't do it in the full lex
    private String stripAnsi(byte[] b, int amount) {
        int counter, rCounter, numbytes;

        byte[] c = new byte[amount];
        rCounter = 0;
        for (counter = 0; counter < amount; counter++) {
            char current = (char) b[counter];
            switch (ansiState) {
                case 0: //NORMAL:
                    if (current != 27 && current != 0) //get rid of those pesky nulls
                    {
                        c[rCounter] = b[counter];
                        rCounter++;
                    } else if (current == 27) {
                        ansiState = ESCAPE;
                    }
                    break;
                case 1: //ESCAPE:
                    if (current == '[' || Character.isDigit(current)) {
                        ansiState = ESCAPE2;
                    } else if (current == '\"') {
                        ansiState = ESCAPE_STRING;

                    }
                    break;
                case 2: //ESCAPE2
                    if (Character.isLetter(current)) {
                        ansiState = NORMAL;
                    } else if (current == '[' || Character.isDigit(current)) {
                        ansiState = ESCAPE2;
                    } else {
                        ansiState = ESCAPE;
                    }
                    break;
                case 3: //ESCAPE_STRING:
                    if (current == '\"') {
                        ansiState = ESCAPE;
                    }
                    break;
            }
        }

        return new String(c, 0, rCounter);
    }

    public static interface TextSender {
        void send(String text) throws Exception;
    }
}
