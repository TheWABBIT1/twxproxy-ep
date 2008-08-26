package org.twdata.twxbbs.proxy.script;

import junit.framework.TestCase;

import java.io.IOException;

import org.apache.mina.common.ByteBuffer;

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
        lexer.addTextTrigger("foo", "don");
        lexer.parse(ByteBuffer.wrap("This guy don is great".getBytes()));
        ScriptLexer.Match match = lexer.waitForTriggers();
        assertNotNull(match);
        assertEquals("foo", match.getMatchedId());
        assertEquals("This guy don", match.getLastLine());
    }

    public void testSimpleLine() throws IOException, InterruptedException {
        ScriptLexer lexer = new ScriptLexer();
        lexer.addTextLineTrigger("foo", "don");
        lexer.parse(ByteBuffer.wrap("This guy don is great\r\n".getBytes()));
        ScriptLexer.Match match = lexer.waitForTriggers();
        assertNotNull(match);
        assertEquals("foo", match.getMatchedId());
        assertEquals("This guy don is great", match.getLastLine());
    }

    public void testSimpleMultiThread() throws IOException, InterruptedException {
        final ScriptLexer lexer = new ScriptLexer();
        lexer.addTextTrigger("foo", "don");
        lexer.setTimeout(2000);
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    for (int x=0; x<4; x++) {
                        lexer.parse(ByteBuffer.wrap("This guy don is great\r\n".getBytes()));
                        Thread.sleep(500);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("bad exception: "+ e);
                }
            }
        });
        t.run();
        for (int x=0; x<4; x++) {
            ScriptLexer.Match match = lexer.waitForTriggers();
            assertNotNull(match);
            assertEquals("foo", match.getMatchedId());
            assertEquals("This guy don", match.getLastLine());
        }
        assertNull(lexer.waitForTriggers());
    }
}
