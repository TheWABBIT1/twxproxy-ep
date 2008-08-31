package org.twdata.twxbbs.proxy.script;

import junit.framework.TestCase;

import java.io.IOException;

import org.apache.mina.common.ByteBuffer;
import org.apache.commons.io.output.ByteArrayOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 26/08/2008
 * Time: 8:18:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScriptLexerTest extends TestCase {

    public void testSimple() throws IOException, InterruptedException {
        ScriptLexer lexer = new ScriptLexer();
        lexer.addTextTrigger("foo", "bob");
        lexer.parse(ByteBuffer.wrap("This guy bob is great".getBytes()));
        ScriptLexer.Match match = lexer.waitForTriggers();
        assertNotNull(match);
        assertEquals("foo", match.getMatchedId());
        assertEquals("This guy bob", match.getMatchedText());
    }

    public void testSimpleLine() throws IOException, InterruptedException {
        ScriptLexer lexer = new ScriptLexer();
        lexer.addTextLineTrigger("foo", "bob");
        lexer.parse(ByteBuffer.wrap("This guy bob is great\r\n".getBytes()));
        ScriptLexer.Match match = lexer.waitForTriggers();
        assertNotNull(match);
        assertEquals("foo", match.getMatchedId());
        assertEquals("This guy bob is great", match.getMatchedText());
    }

    public void testSimpleMultiThread() throws IOException, InterruptedException {
        final ScriptLexer lexer = new ScriptLexer();
        lexer.addTextTrigger("foo", "bob");
        lexer.setTimeout(2000);
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    for (int x=0; x<4; x++) {
                        lexer.parse(ByteBuffer.wrap("This guy bob is great\r\n".getBytes()));
                        Thread.sleep(500);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("bad exception: "+ e);
                }
            }
        });
        t.start();
        for (int x=0; x<4; x++) {
            ScriptLexer.Match match = lexer.waitForTriggers();
            assertNotNull(match);
            assertEquals("foo", match.getMatchedId());
            assertEquals("This guy bob", match.getMatchedText());
        }
        assertNull(lexer.waitForTriggers());
    }

    public void testCapturingTrigger() throws IOException, InterruptedException {
        final ScriptLexer lexer = new ScriptLexer();
        lexer.addCapturingTextTrigger("foo", "bob");
        lexer.setTimeout(5000);
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500);
                    ByteBuffer outBuffer = lexer.parse(ByteBuffer.wrap("This guy bob is great\r\n".getBytes()));
                    String bufferAsStr = readBufferIntoString(outBuffer);
                    assertEquals("This guy  is great\r\n", bufferAsStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("bad exception: "+ e);
                }
            }
        });
        t.start();
        ScriptLexer.Match match = lexer.waitForTriggers();
        assertNotNull(match);
        assertEquals("foo", match.getMatchedId());
        assertEquals("bob", match.getMatchedText());
    }

    public void testCapturingTriggerTwice() throws IOException, InterruptedException {
        final ScriptLexer lexer = new ScriptLexer();
        lexer.addCapturingTextTrigger("foo", "bob");
        lexer.setTimeout(5000);
        sendTextOnOtherThread(lexer, "This guy bob is great\r\n", "This guy  is great\r\n");
        ScriptLexer.Match match = lexer.waitForTriggers();
        assertNotNull(match);
        assertEquals("foo", match.getMatchedId());
        assertEquals("bob", match.getMatchedText());
        lexer.addCapturingTextTrigger("foo", "bob");
        sendTextOnOtherThread(lexer, "This guy bob is great\r\n", "This guy  is great\r\n");
        match = lexer.waitForTriggers();
        assertNotNull(match);
        assertEquals("foo", match.getMatchedId());
        assertEquals("bob", match.getMatchedText());
    }

    private void sendTextOnOtherThread(final ScriptLexer lexer, final String txt, final String expected) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500);
                    ByteBuffer outBuffer = lexer.parse(ByteBuffer.wrap(txt.getBytes()));
                    String bufferAsStr = readBufferIntoString(outBuffer);
                    assertEquals(expected, bufferAsStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("bad exception: "+ e);
                }
            }
        });
        t.start();
    }

    public void testCapturingTriggerThatMatchesAnything() throws IOException, InterruptedException {
        final ScriptLexer lexer = new ScriptLexer();
        lexer.addCapturingTextTrigger("foo", "");
        lexer.setTimeout(5000);
        sendTextOnOtherThread(lexer, "This guy bob is great\r\n", "his guy bob is great\r\n");
        ScriptLexer.Match match = lexer.waitForTriggers();
        assertNotNull(match);
        assertEquals("foo", match.getMatchedId());
        assertEquals("T", match.getMatchedText());
    }

    public void testCapturingTriggerThatMatchesAnythingTwice() throws IOException, InterruptedException {
        final ScriptLexer lexer = new ScriptLexer();
        lexer.addCapturingTextTrigger("foo", "");
        lexer.setTimeout(5000);
        sendTextOnOtherThread(lexer, "This guy bob is great\r\n", "his guy bob is great\r\n");
        ScriptLexer.Match match = lexer.waitForTriggers();
        assertNotNull(match);
        assertEquals("foo", match.getMatchedId());
        assertEquals("T", match.getMatchedText());
        lexer.addCapturingTextTrigger("foo", "");
        sendTextOnOtherThread(lexer, "And jan is cool too\r\n", "nd jan is cool too\r\n");
        match = lexer.waitForTriggers();
        assertNotNull(match);
        assertEquals("foo", match.getMatchedId());
        assertEquals("A", match.getMatchedText());
    }

    public void testCapturingTriggerThatMatchesAnythingButNotBackBuffer() throws IOException, InterruptedException {
        final ScriptLexer lexer = new ScriptLexer();
        lexer.parse(ByteBuffer.wrap("some precursor text".getBytes()));
        lexer.addCapturingTextTrigger("foo", "");
        lexer.setTimeout(5000);
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500);
                    ByteBuffer outBuffer = lexer.parse(ByteBuffer.wrap("This guy bob is great\r\n".getBytes()));
                    String bufferAsStr = readBufferIntoString(outBuffer);
                    assertEquals("his guy bob is great\r\n", bufferAsStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("bad exception: "+ e);
                }
            }
        });
        t.start();
        ScriptLexer.Match match = lexer.waitForTriggers();
        assertNotNull(match);
        assertEquals("foo", match.getMatchedId());
        assertEquals("T", match.getMatchedText());
    }

    public void testCapturingTriggerForLine() throws IOException, InterruptedException {
        final ScriptLexer lexer = new ScriptLexer();
        lexer.addCapturingTextLineTrigger("foo", "bob");
        lexer.setTimeout(5000);
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500);
                    ByteBuffer outBuffer = lexer.parse(ByteBuffer.wrap("This guy bob is great\r\nyeah".getBytes()));
                    String bufferAsStr = readBufferIntoString(outBuffer);
                    assertEquals("This guy yeah", bufferAsStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("bad exception: "+ e);
                }
            }
        });
        t.start();
        ScriptLexer.Match match = lexer.waitForTriggers();
        assertNotNull(match);
        assertEquals("foo", match.getMatchedId());
        assertEquals("bob is great", match.getMatchedText());
    }

    public void testCapturingTriggerForLineThatMatchesAnything() throws IOException, InterruptedException {
        final ScriptLexer lexer = new ScriptLexer();
        lexer.addCapturingTextLineTrigger("foo", "");
        lexer.setTimeout(5000);
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500);
                    ByteBuffer outBuffer = lexer.parse(ByteBuffer.wrap("This guy bob is great\r\nyeah".getBytes()));
                    String bufferAsStr = readBufferIntoString(outBuffer);
                    assertEquals("yeah", bufferAsStr);
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("bad exception: "+ e);
                }
            }
        });
        t.start();
        ScriptLexer.Match match = lexer.waitForTriggers();
        assertNotNull(match);
        assertEquals("foo", match.getMatchedId());
        assertEquals("This guy bob is great", match.getMatchedText());
    }

    public void testCapturingTriggerSpreadOverMultipleCalls() throws IOException, InterruptedException {
        final ScriptLexer lexer = new ScriptLexer();
        lexer.addCapturingTextLineTrigger("foo", "bob");
        lexer.setTimeout(5000);
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500);
                    ByteBuffer outBuffer = lexer.parse(ByteBuffer.wrap("This guy bo".getBytes()));
                    ByteBuffer outBuffer2 = lexer.parse(ByteBuffer.wrap("b is great\r\nyeah".getBytes()));
                    assertEquals("This guy ", readBufferIntoString(outBuffer));
                    assertEquals("yeah", readBufferIntoString(outBuffer2));
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("bad exception: "+ e);
                }
            }
        });
        t.start();
        ScriptLexer.Match match = lexer.waitForTriggers();
        assertNotNull(match);
        assertEquals("foo", match.getMatchedId());
        assertEquals("bob is great", match.getMatchedText());
    }

    private String readBufferIntoString(ByteBuffer buffer) {
        buffer.flip();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        while (buffer.hasRemaining()) {
            bout.write(buffer.get());
        }
        return new String(bout.toByteArray());
    }
}
