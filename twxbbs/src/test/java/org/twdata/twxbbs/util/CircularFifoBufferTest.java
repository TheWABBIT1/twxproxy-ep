package org.twdata.twxbbs.util;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 27/08/2008
 * Time: 9:28:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class CircularFifoBufferTest extends TestCase {

    public void testPut() {
        CircularFifoBuffer buffer = new CircularFifoBuffer(5);
        buffer.put((byte) 1);
        buffer.put((byte) 3);
        assertTrue(buffer.hasRemaining());
        assertEquals((byte) 1, buffer.get());
        assertTrue(buffer.hasRemaining());
        assertEquals((byte) 3, buffer.get());
        assertFalse(buffer.hasRemaining());
    }

    public void testPutPastEnd() {
        CircularFifoBuffer buffer = new CircularFifoBuffer(3);
        buffer.put((byte) 1);
        buffer.put((byte) 3);
        buffer.put((byte) 5);
        buffer.put((byte) 7);
        assertTrue(buffer.hasRemaining());
        assertEquals((byte) 5, buffer.get());
        assertTrue(buffer.hasRemaining());
        assertEquals((byte) 7, buffer.get());
        assertFalse(buffer.hasRemaining());
    }
}
