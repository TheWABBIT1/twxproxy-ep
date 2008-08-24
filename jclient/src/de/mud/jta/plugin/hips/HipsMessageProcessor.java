package de.mud.jta.plugin.hips;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 15/08/2008
 * Time: 21:39:54
 * To change this template use File | Settings | File Templates.
 */
public class HipsMessageProcessor {
    public static final String DEFAULT_NAMESPACE = "_default_";
    private static final Pattern MESSAGE_PATTERN = Pattern.compile("(?:([a-zA-Z][a-zA-Z0-9_]*)\\.)?([a-zA-Z][a-zA-Z0-9_]*)=(\\{.*\\})");
    private final Map<String,List<HipsMessageListener>> handlers = new HashMap<String,List<HipsMessageListener>>();
    private static final HipsMessageProcessor SELF = new HipsMessageProcessor();

    private HipsMessageProcessor() {}

    public static HipsMessageProcessor getInstance() {
        return SELF;
    }

    public int getListenerCount() {
        int count = 0;
        for (List<HipsMessageListener> list : handlers.values()) {
            count += list.size();
        }
        return count;
    }

    public void process(String message) {

        System.out.println("processing message "+message);
        Matcher m = MESSAGE_PATTERN.matcher(message);
        if (m.matches()) {
            String namespace = m.group(1) != null ? m.group(1) : DEFAULT_NAMESPACE;
            String name = m.group(2);
            String json = m.group(3);
            List<HipsMessageListener> listeners = handlers.get(namespace);
            if (listeners != null) {
                for (HipsMessageListener listener : listeners) {
                    listener.handle(name, json);
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid message");
        }
    }

    public void addListener(String namespace, HipsMessageListener listener) {
        List<HipsMessageListener> listeners = handlers.get(namespace);
        if (listeners == null) {
            listeners = new ArrayList<HipsMessageListener>();
            handlers.put(namespace, listeners);
        }
        listeners.add(listener);
    }
}
