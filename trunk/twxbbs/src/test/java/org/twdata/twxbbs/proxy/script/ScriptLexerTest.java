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
                    assertEquals("This guy \nyeah", bufferAsStr);
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
