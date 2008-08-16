package de.mud.jta.plugin.hips;

import com.twolattes.json.Marshaller;

import java.util.regex.Pattern;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;

import de.mud.jta.plugin.hips.tw.TwMessageHandler;
import de.mud.jta.plugin.Hips;
import org.json.JSONObject;
import org.json.JSONException;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 15/08/2008
 * Time: 21:39:54
 * To change this template use File | Settings | File Templates.
 */
public class HipsMessageProcessor {
    private static final Pattern MESSAGE_PATTERN = Pattern.compile("[a-zA-Z][a-zA-Z0-9_]*\\.?[a-zA-Z][a-zA-Z0-9_]*=\\{.*\\}");
    private final Map<String,Object> handlers;

    public HipsMessageProcessor(final StatusPanel statusPanel) {
        handlers = Collections.unmodifiableMap(new HashMap<String,Object>() {{
            put("tw", new TwMessageHandler(statusPanel));
        }});
    }

    public void process(String message) {

        System.out.println("processing message "+message);
        int equalsPos = message.indexOf('=');

        String namespace = null;
        String name = message.substring(0, equalsPos);
        String json = message.substring(equalsPos+1);

        int nsPos = name.indexOf('.');
        if (nsPos > 0) {
            namespace = name.substring(0, nsPos);
            name = name.substring(nsPos+1);
        }

        Object handler = handlers.get(namespace);
        if (handler == null) {
            System.err.println("Invalid namespace: "+namespace+" in message "+message);
            return;
        }

        String className = getClass().getPackage().getName()+"."+namespace+"."+Character.toUpperCase(name.charAt(0)) + name.substring(1);
        Class messageCls;
        try {

            messageCls = getClass().getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            System.err.println("Invalid message: "+className);
            return;
        }
        Method method = null;
        try {
            method = getHandlerMethod(handler, messageCls);
        } catch (NoSuchMethodException e) {
            System.err.println("No such handler method for message class "+className+" on namespace "+namespace);
            return;
        }

        Marshaller m = Marshaller.create(messageCls);

        JSONObject obj;
        try {

            obj = new JSONObject(json);
        } catch (JSONException e) {
            System.err.println("Invalid json message: "+json);
            return;
        }
        Object messageObj;
        try {
            messageObj = m.unmarshall(obj);
        } catch (Exception ex) {
            System.err.println("Unable to unmarshal: "+json);
            return;
        }
        try {
            method.invoke(handler, messageObj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("We're all fucked", e);
        } catch (InvocationTargetException e) {
            System.err.println("Unable to execute handler");
            e.printStackTrace(System.err);
        }
    }

    private Method getHandlerMethod(Object handler, Class messageCls) throws NoSuchMethodException {

        Method m = handler.getClass().getMethod("handle", messageCls);
        return m;
    }
}
