package org.twdata.twxbbs.telnet;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import static org.twdata.twxbbs.telnet.Telnet.IAC;

import java.nio.charset.Charset;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 10/08/2008
 * Time: 15:36:59
 * To change this template use File | Settings | File Templates.
 */
public class TelnetDecoder implements ProtocolDecoder {

    private static final String CONTEXT = TelnetDecoder.class.getName()
            + ".context";

    private final Charset charset;

    /**
     * Creates a new instance with the current default {@link Charset}
     * and {@link LineDelimiter#AUTO} delimiter.
     */
    public TelnetDecoder() {
        this(Charset.forName("UTF-8"));
    }

    /**
     * Creates a new instance with the spcified <tt>charset</tt>
     * and {@link LineDelimiter#AUTO} delimiter.
     */
    public TelnetDecoder(Charset charset) {
        this.charset = charset;
    }

    public void decode(IoSession session, ByteBuffer buffer, ProtocolDecoderOutput out) throws Exception {
        Context ctx = getContext(session, out);

        ByteBuffer buf = ctx.getBuffer();

        // Silly little parser to strip out all telnet codes
        boolean iac = false;
        boolean iacSomething = false;
        for (int pos = buffer.position(); pos < buffer.limit(); pos++) {
            byte b = buffer.get(pos);
            if (b == IAC) {
                iac = true;
            } else if (iac) {
                iacSomething = true;
                iac = false;
            } else if (iacSomething) {
                // swallow
                iac = false;
                iacSomething = false;
            } else if (b == '\r') {
                buf.put((byte)'\r');
                buf.put((byte)'\n');
            } else {
                buf.put(b);
            }
        }
        buf.flip();
        try {
            out.write(buf.getString(ctx.getDecoder()));
        } finally {
            buf.clear();
        }
    }


    public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
    }

    private Context getContext(IoSession session, ProtocolDecoderOutput out) {
        Context ctx = (Context) session.getAttribute(CONTEXT);
        if (ctx == null) {
            ctx = new Context();
            session.setAttribute(CONTEXT, ctx);
        }
        return ctx;
    }

    public void dispose(IoSession session) throws Exception {
        Context ctx = (Context) session.getAttribute(CONTEXT);
        if (ctx != null) {
            ctx.getBuffer().release();
            session.removeAttribute(CONTEXT);
        }
    }

    private class Context {
        private final java.nio.charset.CharsetDecoder decoder;
        private final ByteBuffer buf;

        private Context() {
            decoder = charset.newDecoder();
            buf = ByteBuffer.allocate(80).setAutoExpand(true);
        }

        public java.nio.charset.CharsetDecoder getDecoder() {
            return decoder;
        }

        public ByteBuffer getBuffer() {
            return buf;
        }

    }
}
