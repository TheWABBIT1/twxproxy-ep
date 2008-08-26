package org.twdata.twxbbs.proxy.script;

public interface ScriptApi {

    String getCurrentLine();

    String getMatchedLine();

    void setTextTrigger(String id, String text);

    void setTextLineTrigger(String id, String text);

    void send(String text) throws Exception;

    String pause();

    void killTextTrigger(String id);


}
