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

    // The current line
    private final StringBuffer currentLine;

    // Contains text that isn't being actively matched, but may
    private final CircularFifoBuffer backBuffer;

    // Contains captured text
    private final StringBuffer captureBuffer;

    // Buffer for read data that may be used to create a modified write buffer if a capturing trigger is used
    private final ByteBuffer readCopyBuffer;

    // Whether we are waiting for triggers to be matched or not
    private boolean waiting;

    // The last match a trigger matched
    private Match lastMatch;

    // The timeout for waiting for a match
    private long timeout = 1000 * 60;

    public ScriptLexer() {
        activeTriggers = new LinkedHashMap<String,Trigger>();
        currentLine = new StringBuffer();
        backBuffer = new CircularFifoBuffer(1024);
        captureBuffer = new StringBuffer();
        readCopyBuffer = ByteBuffer.allocate(1024);
        readCopyBuffer.setAutoExpand(true);

    }

    public synchronized void addTextTrigger(String id, String text) {
        if (waiting) throw new IllegalStateException("Cannot accept new triggers while lexing text");
        activeTriggers.put(id, new DefaultTrigger(id, text, false));
    }

    public synchronized void addTextLineTrigger(String id, String text) {
        if (waiting) throw new IllegalStateException("Cannot accept new triggers while lexing text");
        activeTriggers.put(id, new DefaultTrigger(id, text, true));
    }

    public synchronized void addCapturingTextTrigger(String id, String text) {
        if (waiting) throw new IllegalStateException("Cannot accept new triggers while lexing text");
        activeTriggers.put(id, new CapturingTrigger(id, text, false));
    }

    public synchronized void addCapturingTextLineTrigger(String id, String text) {
        if (waiting) throw new IllegalStateException("Cannot accept new triggers while lexing text");
        activeTriggers.put(id, new CapturingTrigger(id, text, true));
    }

    public synchronized void removeTextTrigger(String id) {
        if (waiting) throw new IllegalStateException("Cannot remove triggers while lexing text");
        activeTriggers.remove(id);
    }

    public String getCurrentLine() {
        return currentLine.toString();
    }

    public synchronized Match waitForTriggers() throws IOException, InterruptedException {
        try {
            waiting = true;
            lastMatch = null;
            if (backBuffer.hasRemaining()) {
                parse(backBuffer);
            }
            if (lastMatch == null) {
                wait(timeout);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lastMatch;
    }

    public synchronized ByteBuffer parse(ByteBuffer buffer) throws IOException {
        readCopyBuffer.clear();

        int bytesRead = 0;
        while (buffer.hasRemaining()) {
            if (!waiting && buffer == backBuffer) {
                return buffer;
            }
            byte b = buffer.get();
            bytesRead++;

            if (!waiting) {
                backBuffer.put(b);
                readCopyBuffer.put(b);
            } else {
                char c = (char) b;
                boolean capturing = false;
                putInCurrentLine(c);
                for (Trigger trigger : activeTriggers.values()) {

                    if (trigger instanceof CapturingTrigger && trigger.potentialMatch(c)) {
                        capturing = true;
                    }
                }

                if (capturing) {
                    captureBuffer.append(c);
                    System.out.println("adding to buffer:"+captureBuffer);
                } else {
                    if (captureBuffer.length() > 0) {
                        System.out.println("Clearing buffer");
                    }
                    for (int x=0; x<captureBuffer.length(); x++) {
                        readCopyBuffer.putChar(captureBuffer.charAt(x));
                    }
                    captureBuffer.setLength(0);
                    readCopyBuffer.put(b);

                }

                for (Trigger trigger : activeTriggers.values()) {
                    if (trigger.match(c)) {
                        handleMatch(trigger);
                    }
                }
            }
        }

        return createResultingBuffer(buffer, bytesRead);
    }

    private ByteBuffer createResultingBuffer(ByteBuffer buffer, int bytesRead) {
        readCopyBuffer.flip();
        if (bytesRead > readCopyBuffer.limit()) {
            // Lazily create a new buffer to store our output that will now be modified
            ByteBuffer writeBuffer = ByteBuffer.allocate(readCopyBuffer.limit());
            while (readCopyBuffer.hasRemaining()) {
                writeBuffer.put(readCopyBuffer.get());
            }
            return writeBuffer;
        } else {
            return buffer;
        }
    }

    private void putInCurrentLine(char c) {
        if (c != '\n') {
            if (c != '\r')
                currentLine.append(c);
        } else {
            currentLine.setLength(0);
        }
    }

    private void handleMatch(Trigger trigger) {
        String matchedText;
        if (trigger instanceof CapturingTrigger) {
            matchedText = captureBuffer.toString();
        } else {
            matchedText = currentLine.toString();
        }
        if (matchedText.length() > 0) {
            if (matchedText.charAt(matchedText.length()-1) == '\r') {
                matchedText = matchedText.substring(0, matchedText.length() -1);
            } else if (matchedText.charAt(matchedText.length()-1) == '\n') {
                matchedText = matchedText.substring(0, matchedText.length() -2);
            }
        }
        Match match = new Match(trigger.getId(), matchedText);
        if (trigger.shouldBeRemovedAfterMatch()) {
            activeTriggers.remove(trigger.getId());
        }
        waiting = false;
        lastMatch = match;
        notifyAll();
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public static class Match {
        private final String matchedId;
        private final String matchedText;

        public Match(String matchedId, String matchedText) {
            this.matchedId = matchedId;
            this.matchedText = matchedText;
        }

        public String getMatchedId() {
            return matchedId;
        }

        public String getMatchedText() {
            return matchedText;
        }
    }

    static interface Trigger {
        boolean match(char c);
        String getId();
        boolean shouldBeRemovedAfterMatch();

        boolean potentialMatch(char c);
    }

    static class DefaultTrigger implements Trigger{
        private final String id;
        private final boolean waitForLine;
        private final char[] text;
        private int pos;
        private boolean triggered;

        public DefaultTrigger(String id, String line, boolean waitForLine) {
            this.id = id;
            text = line.toCharArray();
            this.waitForLine = waitForLine;
            pos = 0;
        }

        public boolean potentialMatch(char c) {
            return triggered || text[pos] == c;
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
                if ('\n' == c) {
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

        public boolean shouldBeRemovedAfterMatch() {
            return false;
        }
    }

    class CapturingTrigger extends DefaultTrigger {

        public CapturingTrigger(String id, String line, boolean waitForLine) {
            super(id, line, waitForLine);
        }

        public boolean shouldBeRemovedAfterMatch() {
            return true;
        }
    }
}
