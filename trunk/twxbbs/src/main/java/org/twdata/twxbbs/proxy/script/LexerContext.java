package org.twdata.twxbbs.proxy.script;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 02/09/2008
 * Time: 8:01:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class LexerContext {
    private boolean waiting;
    private ScriptLexer.Match lastMatch;

    public boolean isWaiting() {
        return waiting;
    }

    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    public ScriptLexer.Match getLastMatch() {
        return lastMatch;
    }

    public void setLastMatch(ScriptLexer.Match lastMatch) {
        this.lastMatch = lastMatch;
    }
}
