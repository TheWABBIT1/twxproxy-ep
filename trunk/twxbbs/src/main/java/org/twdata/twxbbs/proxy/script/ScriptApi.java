package org.twdata.twxbbs.proxy.script;

public interface ScriptApi {

    String getCurrentLine();

    void setTextTrigger(String id, String text);

    void setTextLineTrigger(String id, String text);

    void setCapturingTextTrigger(String id, String text);

    void setCapturingTextLineTrigger(String id, String text);

    void send(String text) throws Exception;

    String pause();

    void killTextTrigger(String id);


    String stripAnsi(String text);

    void setTimeout(long timeout);

    String getMatchedLine();
}
