package org.twdata.twxbbs.proxy.script;

import org.apache.mina.common.ByteBuffer;
import org.twdata.twxbbs.util.CircularFifoBuffer;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 26/08/2008
 * Time: 7:28:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScriptLexer {
    private final Set<Trigger> activeTriggers;

    // The current line
    private final StringBuffer currentLine;
    private boolean resetCurrentLineNextCharacter;

    // Contains text that isn't being actively matched, but may
    private final CircularFifoBuffer backBuffer;

    // Contains captured text
    private final StringBuffer captureBuffer;

    // Buffer for read data that may be used to create a modified write buffer if a capturing trigger is used
    private final ByteBuffer readCopyBuffer;

    // The timeout for waiting for a match
    private long timeout = 0;

    // Whether we are waiting for triggers to be matched or not, also used as the synchronization object
    private final LexerContext lexerContext;

    public ScriptLexer(LexerContext lexerContext) {
        activeTriggers = new CopyOnWriteArraySet<Trigger>();
        currentLine = new StringBuffer();
        backBuffer = new CircularFifoBuffer(1024);
        captureBuffer = new StringBuffer();
        readCopyBuffer = ByteBuffer.allocate(1024);
        readCopyBuffer.setAutoExpand(true);
        this.lexerContext = lexerContext;
    }

    public void addTextTrigger(String id, String text) {
        if (lexerContext.isWaiting()) throw new IllegalStateException("Cannot accept new triggers while lexing text");
        activeTriggers.add(new DefaultTrigger(id, text, false));
    }

    public void addTextLineTrigger(String id, String text) {
        if (lexerContext.isWaiting()) throw new IllegalStateException("Cannot accept new triggers while lexing text");
        activeTriggers.add(new DefaultTrigger(id, text, true));
    }

    public void addCapturingTextTrigger(String id, String text) {
        if (lexerContext.isWaiting()) throw new IllegalStateException("Cannot accept new triggers while lexing text");
        if (text == null || text.length() == 0) {
            backBuffer.clear();
        }
        activeTriggers.add(new CapturingTrigger(id, text, false));
    }

    public void addCapturingTextLineTrigger(String id, String text) {
        if (lexerContext.isWaiting()) throw new IllegalStateException("Cannot accept new triggers while lexing text");
        if (text == null || text.length() == 0) {
            backBuffer.clear();
        }
        activeTriggers.add(new CapturingTrigger(id, text, true));
    }

    public void removeTextTrigger(String id) {
        if (lexerContext.isWaiting()) throw new IllegalStateException("Cannot remove triggers while lexing text");
        activeTriggers.remove(new DefaultTrigger(id, "", false));
    }

    public String getCurrentLine() {
        return currentLine.toString();
    }

    public Match waitForTriggers() throws IOException, InterruptedException {
        synchronized (lexerContext) {
            try {
                lexerContext.setWaiting(true);
                lexerContext.setLastMatch(null);
                if (backBuffer.hasRemaining()) {
                    parse(backBuffer);
                }
                if (lexerContext.getLastMatch() == null) {
                    if (timeout > 0) {
                        lexerContext.wait(timeout);
                    } else {
                        lexerContext.wait();
                    }
                }
            } catch (InterruptedException ex) {
                System.out.println("Stopping script");
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                lexerContext.setWaiting(false);
            }
            return lexerContext.getLastMatch();
        }
    }

    public ByteBuffer parse(ByteBuffer buffer) throws IOException {
        synchronized (lexerContext) {
            readCopyBuffer.clear();

            int bytesRead = 0;
            while (buffer.hasRemaining()) {
                if (!lexerContext.isWaiting() && buffer == backBuffer) {
                    return buffer;
                }
                byte b = buffer.get();
                bytesRead++;

                if (!lexerContext.isWaiting()) {
                    backBuffer.put(b);
                    readCopyBuffer.put(b);
                    captureBuffer.setLength(0);
                } else {
                    char c = (char) b;
                    boolean capturing = false;
                    putInCurrentLine(c);
                    for (Trigger trigger : activeTriggers) {

                        if (trigger instanceof CapturingTrigger && trigger.potentialMatch(c)) {
                            capturing = true;
                        }
                    }

                    if (capturing) {
                        captureBuffer.append(c);
                    } else {
                        for (int x=0; x<captureBuffer.length(); x++) {
                            readCopyBuffer.putChar(captureBuffer.charAt(x));
                        }
                        captureBuffer.setLength(0);
                        readCopyBuffer.put(b);

                    }

                    for (Trigger trigger : activeTriggers) {
                        if (trigger.match(c)) {
                            handleMatch(trigger);
                        }
                    }
                }
            }

            return createResultingBuffer(buffer, bytesRead);
        }
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
        if (resetCurrentLineNextCharacter) {
            currentLine.setLength(0);
            resetCurrentLineNextCharacter = false;
        }
        if (c != '\n') {
            if (c != '\r')
                currentLine.append(c);
        } else {
            resetCurrentLineNextCharacter = true;
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
            activeTriggers.remove(trigger);
        }
        lexerContext.setWaiting(false);
        lexerContext.setLastMatch(match);
        lexerContext.notifyAll();
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
            return triggered || text.length == 0 || text[pos] == c;
        }

        public boolean match(char c) {
            if (!triggered) {
                if (text.length == 0 || text[pos] == c) {
                    pos++;
                    if (text.length == 0 || text.length == pos) {
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

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DefaultTrigger)) return false;

            DefaultTrigger that = (DefaultTrigger) o;

            return id.equals(that.id);
        }

        public int hashCode() {
            return id.hashCode();
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
