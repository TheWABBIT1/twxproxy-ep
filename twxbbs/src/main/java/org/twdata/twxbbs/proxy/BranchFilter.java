package org.twdata.twxbbs.proxy;

import org.apache.mina.common.*;
import org.apache.mina.common.support.AbstractIoFilterChain;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.twdata.twxbbs.proxy.GameFilter;

import java.nio.charset.Charset;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 9/08/2008
 * Time: 22:13:19
 * To change this template use File | Settings | File Templates.
 */
public class BranchFilter extends IoFilterAdapter {

    Charset charset;
    private static final String CONTEXT = BranchFilter.class.getName()
            + ".context";
    private ProtocolCodecFilter protocolFilter;
    private GameFilter gameFilter;

    public BranchFilter() {
        protocolFilter = new ProtocolCodecFilter(new TextLineCodecFactory( Charset.forName( "CP437" )));
        gameFilter = new GameFilter();
    }

    public void sessionCreated(final NextFilter nextFilter, IoSession session)
            throws Exception {
        IoFilterChain newChain = new AbstractIoFilterChain(session) {
            protected void doWrite(IoSession session, WriteRequest writeRequest) throws Exception {
                nextFilter.filterWrite(session, writeRequest);
            }

            protected void doClose(IoSession session) throws Exception {
                nextFilter.filterClose(session);
            }
        };
        newChain.addFirst("line", protocolFilter);
        newChain.addAfter("line", "game", gameFilter);

        Context ctx = new Context(newChain);
        session.setAttribute(CONTEXT, ctx);

        nextFilter.sessionCreated(session);
    }

    public void messageSent(final NextFilter nextFilter, final IoSession session,
            Object message) throws Exception {

        // Forking request into new chain that won't modify original
        ByteBuffer buffer = (ByteBuffer) message;
        Context ctx = getContext(session);
        ctx.getBuffer().clear();
        ctx.getBuffer().put(buffer);
        ctx.getBuffer().acquire();

        IoSession newSession = (IoSession) Proxy.newProxyInstance(
	        IoSession.class.getClassLoader(),
	        new Class[]{IoSession.class},
	        new InvocationHandler() {
                public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                    if (method.getName().equals("getHandler"))
                        return new IoHandlerAdapter() {};
                    else
                        return method.invoke(session, objects);
                }
            }
        );

        ctx.getFilterChain().fireMessageReceived(newSession, ctx.getBuffer());
        nextFilter.messageSent(session, message);
    }

    private Context getContext(IoSession session) {
        return (Context) session.getAttribute(CONTEXT);
    }

    private class Context {
        private final ByteBuffer buf;
        private IoFilterChain filterChain;

        private Context(IoFilterChain newChain) {
            buf = ByteBuffer.allocate(80).setAutoExpand(true);
            this.filterChain = newChain;
        }

        public ByteBuffer getBuffer() {
            return buf;
        }

        public IoFilterChain getFilterChain() {
            return filterChain;
        }
    }
}
