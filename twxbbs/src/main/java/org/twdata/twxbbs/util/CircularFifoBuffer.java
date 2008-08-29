package org.twdata.twxbbs.util;

import org.apache.mina.common.ByteBuffer;

import java.nio.*;


/**
 * Simple circular fifo buffer for the lexing back buffer
 */
public class CircularFifoBuffer extends ByteBuffer {

    private final byte[] buffer;

    private int pos;

    private int start;


    public CircularFifoBuffer(int size) {
        this.buffer = new byte[size];
        pos = 0;
        start = 0;
    }

    public ByteBuffer put(byte b) {
        buffer[pos++] = b;
        if (pos == buffer.length) {
            pos = 0;
        }
        if (start == pos) {
            start++;
            if (start == buffer.length) {
                start = 0;
            }
        }
        return this;
    }

    @Override
    public byte get() {
        if (start != pos) {
            byte b = buffer[start++];
            if (start == buffer.length) {
                start = 0;
            }
            return b;
        } else {
            throw new IllegalStateException("Cannot get from an empty buffer");
        }
    }

    @Override
    public boolean hasRemaining() {
        return start != pos;
    }

    @Override
    public int remaining() {
        if (hasRemaining()) {
            if (pos > start) {
                return pos - start;
            } else {
                return (buffer.length) - (start - pos);
            }
        } else {
            return 0;
        }
    }

    @Override
    public byte[] array() {
        byte[] result = new byte[remaining()];
        int rpos = start;
        for (int x=0; x<result.length; x++) {
            result[x] = buffer[rpos++];
            if (rpos == buffer.length) {
                rpos = 0;
            }
        }
        return result;
    }

    public byte get(int i) {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer put(int i, byte b) {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer get(byte[] bytes, int i, int i1) {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer put(java.nio.ByteBuffer byteBuffer) {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer put(byte[] bytes, int i, int i1) {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer compact() {
        throw new UnsupportedOperationException();
    }

    public ByteOrder order() {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer order(ByteOrder byteOrder) {
        throw new UnsupportedOperationException();
    }

    public char getChar() {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer putChar(char c) {
        throw new UnsupportedOperationException();
    }

    public char getChar(int i) {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer putChar(int i, char c) {
        throw new UnsupportedOperationException();
    }

    public CharBuffer asCharBuffer() {
        throw new UnsupportedOperationException();
    }

    public short getShort() {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer putShort(short i) {
        throw new UnsupportedOperationException();
    }

    public short getShort(int i) {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer putShort(int i, short i1) {
        throw new UnsupportedOperationException();
    }

    public ShortBuffer asShortBuffer() {
        throw new UnsupportedOperationException();
    }

    public int getInt() {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer putInt(int i) {
        throw new UnsupportedOperationException();
    }

    public int getInt(int i) {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer putInt(int i, int i1) {
        throw new UnsupportedOperationException();
    }

    public IntBuffer asIntBuffer() {
        throw new UnsupportedOperationException();
    }

    public long getLong() {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer putLong(long l) {
        throw new UnsupportedOperationException();
    }

    public long getLong(int i) {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer putLong(int i, long l) {
        throw new UnsupportedOperationException();
    }

    public LongBuffer asLongBuffer() {
        throw new UnsupportedOperationException();
    }

    public float getFloat() {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer putFloat(float v) {
        throw new UnsupportedOperationException();
    }

    public float getFloat(int i) {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer putFloat(int i, float v) {
        throw new UnsupportedOperationException();
    }

    public FloatBuffer asFloatBuffer() {
        throw new UnsupportedOperationException();
    }

    public double getDouble() {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer putDouble(double v) {
        throw new UnsupportedOperationException();
    }

    public double getDouble(int i) {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer putDouble(int i, double v) {
        throw new UnsupportedOperationException();
    }

    public DoubleBuffer asDoubleBuffer() {
        throw new UnsupportedOperationException();
    }

    public void acquire() {
        throw new UnsupportedOperationException();
    }

    public void release() {
        throw new UnsupportedOperationException();
    }

    public java.nio.ByteBuffer buf() {
        throw new UnsupportedOperationException();
    }

    public boolean isDirect() {
        throw new UnsupportedOperationException();
    }

    public boolean isReadOnly() {
        throw new UnsupportedOperationException();
    }

    public int capacity() {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer capacity(int i) {
        throw new UnsupportedOperationException();
    }

    public boolean isAutoExpand() {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer setAutoExpand(boolean b) {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer expand(int i, int i1) {
        throw new UnsupportedOperationException();
    }

    public boolean isPooled() {
        throw new UnsupportedOperationException();
    }

    public void setPooled(boolean b) {
        throw new UnsupportedOperationException();
    }

    public int position() {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer position(int i) {
        throw new UnsupportedOperationException();
    }

    public int limit() {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer limit(int i) {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer mark() {
        throw new UnsupportedOperationException();
    }

    public int markValue() {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer reset() {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer clear() {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer flip() {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer rewind() {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer duplicate() {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer slice() {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer asReadOnlyBuffer() {
        throw new UnsupportedOperationException();
    }

    public int arrayOffset() {
        throw new UnsupportedOperationException();
    }
}
