package org.twdata.twxbbs.proxy.script;

import org.apache.mina.common.ByteBuffer;
import org.twdata.twxbbs.util.CircularFifoBuffer;

import java.io.IOException;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 26/08/2008
 * Time: 7:28:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScriptLexer {
    private final Map<String,Trigger> activeTriggers;
    private final StringBuffer currentLine;
    private CircularFifoBuffer backBuffer;
    private boolean waiting;
    private Match lastMatch;
    private long timeout = 1000 * 60;

    public ScriptLexer() {
        activeTriggers = new LinkedHashMap<String,Trigger>();
        currentLine = new StringBuffer();
        backBuffer = new CircularFifoBuffer(1024);
    }

    public synchronized void addTextTrigger(String id, String text) {
        if (waiting) throw new IllegalStateException("Cannot accept new triggers while lexing text");
        activeTriggers.put(id, new Trigger(id, text, false));
    }

    public synchronized void addTextLineTrigger(String id, String text) {
        if (waiting) throw new IllegalStateException("Cannot accept new triggers while lexing text");
        activeTriggers.put(id, new Trigger(id, text, true));
    }

    public synchronized void removeTextTrigger(String id) {
        if (waiting) throw new IllegalStateException("Cannot remove triggers while lexing text");
        activeTriggers.remove(id);
    }

    public String getCurrentLine() {
        return currentLine.toString();
    }

    public synchronized Match waitForTriggers() throws IOException, InterruptedException {
        waiting = true;
        lastMatch = null;
        if (backBuffer.hasRemaining()) {
            parse(backBuffer);
        }
        if (lastMatch == null) {
            wait(timeout);
        }
        return lastMatch;
    }

    public synchronized void parse(ByteBuffer buffer) throws IOException {

        while (buffer.hasRemaining()) {
            if (!waiting && buffer == backBuffer) {
                return;
            }
            byte b = buffer.get();
            if (!waiting) {
                backBuffer.put(b);
            } else {
                char c = (char) b;
                for (Trigger trigger : activeTriggers.values()) {
                    if (c != '\n') {
                        if (c != '\r')
                            currentLine.append(c);
                    } else {
                        currentLine.setLength(0);
                    }
                    if (trigger.match(c)) {
                        handleMatch(trigger);
                    }
                }
            }
        }
    }

    private void handleMatch(Trigger trigger) {
        Match match = new Match(trigger.getId(), currentLine.toString());
        waiting = false;
        lastMatch = match;
        notifyAll();
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public static class Match {
        private final String matchedId;
        private final String lastLine;

        public Match(String matchedId, String lastLine) {
            this.matchedId = matchedId;
            this.lastLine = lastLine;
        }

        public String getMatchedId() {
            return matchedId;
        }

        public String getLastLine() {
            return lastLine;
        }
    }

    static class Trigger {
        private final String id;
        private final boolean waitForLine;
        private final char[] text;
        private int pos;
        private boolean triggered;

        public Trigger(String id, String line, boolean waitForLine) {
            this.id = id;
            text = line.toCharArray();
            this.waitForLine = waitForLine;
            pos = 0;
            triggered = false;
        }

        public boolean match(char c) {
            if (!triggered) {
                if (text[pos] == c) {
                    pos++;
                    if (text.length == pos) {
                        triggered = true;
                        if (!waitForLine) {
                            reset();
                            return true;
                        }
                    }
                } else {
                    pos = 0;
                }
            } else {
                if ('\r' == c) {
                    reset();
                    return true;
                }
            }
            return false;
        }

        private void reset() {
            triggered = false;
            pos = 0;
        }

        public String getId() {
            return id;
        }
    }
}
