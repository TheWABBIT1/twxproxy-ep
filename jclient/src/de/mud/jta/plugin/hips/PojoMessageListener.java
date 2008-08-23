package de.mud.jta.plugin.hips;

import com.twolattes.json.Marshaller;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import org.json.JSONObject;
import org.json.JSONException;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 23/08/2008
 * Time: 3:22:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class PojoMessageListener implements HipsMessageListener {
    private final Object handler;
    private final String namespace;

    public PojoMessageListener(String namespace, Object handler) {
        this.handler = handler;
        this.namespace = namespace;
    }


    public void handle(String name, String json) {


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
