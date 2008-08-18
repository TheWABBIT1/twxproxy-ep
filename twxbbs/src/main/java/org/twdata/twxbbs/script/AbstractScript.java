/**
 * 
 */
package org.twdata.twxbbs.script;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of StreamConnection connection and parsing methods
 */
public abstract class AbstractScript  {
    
    private Map<String, Response> respondWith = new HashMap<String, Response>();
    private List<Prompt> waitFor = new ArrayList<Prompt>();

    private String endOfLine = "\r\n";
    private char lastChar;
    private boolean alive = true;
    private StringBuilder lastLine = new StringBuilder();
    private final InputStream in;

    protected abstract void writeActual(String text);

    public AbstractScript(InputStream in) {
        this.in = in;
    }

    public void setEndOfLine(String eol) {
    	this.endOfLine = eol;
    }
    
    public void send(String text) throws IOException {
        print(text, false);

    }

    public void sendLine(String text) throws IOException {
        print(text, true);
    }

    public void sleep(int millis) throws InterruptedException {
        Thread.sleep(millis);
    }
    
    private void print(String text, boolean eol) throws IOException {
        text = text.replace("^C", String.valueOf((char) 3));
        text = text.replace("^M", endOfLine);
        if (eol) {
            writeActual(text+endOfLine);
            getLine();
        } else {
            writeActual(text);
        }
    }

    public void respond(String prompt, String response) {
        if (response == null) {
            respondWith.remove(prompt);
        } else {
            respondWith.put(prompt, new Response(prompt, response));
        }
    }
    
    public boolean waitFor(String waitFor) throws IOException {
        return waitFor(waitFor, false);
    }

    public boolean waitFor(String waitFor,
            boolean readLineOnMatch) throws IOException {
        prepare(new String[] { waitFor });
        return (readFromStream(readLineOnMatch) == 0);
    }

    public int waitForMux(String... waitFor) throws IOException {
        return waitForMux(waitFor, false);
    }
    
    public int waitForMux(String[] waitFor,
            boolean readLineOnMatch) throws IOException {
        prepare(waitFor);
        return readFromStream(readLineOnMatch);
    }
    
    protected void prepare(String[] text) {
        this.alive = true;
        for (String val : text) {
            waitFor.add(new Prompt(val));
        }
        this.lastLine.setLength(0);
    }

    public String lastLine() {
        return this.lastLine.toString();
    }
    
    public String getLine() throws IOException {
        if (waitFor(endOfLine, false)) {
            return lastLine();
        }
        return null;
    }

    public void process(byte data) {

    }

    public int readFromStream(boolean readLineOnMatch) throws IOException {
        int result = -1;
        byte[] data = new byte[1];
        int length = 0;
        boolean readTillEndOfLine = false;

        outer:
        while (alive && (length = in.read(data)) >= 0) {
            
            for (int x=0; x<length; x++) {
                char c = (char) data[x];
                if (readTillEndOfLine && (c == '\r' || c == '\n'))
                    break outer;
    
                int match = lookForMatch(c);
                if (match != -1) {
                    result = match;
                    if (readLineOnMatch && (c != '\r' && c != '\n')) {
                        readTillEndOfLine = true;
                    } else {
                        break outer;
                    }
                } else {
                    lookForResponse((char) data[x]);
                    lastChar = (char) data[x];
                }
            }
        }
        reset();
        return result;
    }

    int lookForMatch(char s) {
        if (s != '\r' && s != '\n')
            lastLine.append(s);
        for (int m = 0; alive && m < waitFor.size(); m++) {
            Prompt prompt = (Prompt) waitFor.get(m);
            if (prompt.matchChar(s)) {
                // the whole thing matched so, return the match answer
                if (prompt.match()) {
                    return m;
                } else {
                    prompt.nextPos();
                }

            } else {
                // if the current character did not match reset
                prompt.resetPos();
                if (s == '\n' || s == '\r') {
                    lastLine.setLength(0);
                }
            }
        }
        return -1;
    }

    void lookForResponse(char s) throws IOException {
        for (Response response : respondWith.values()) {
            if (response.matchChar(s)) {
                if (response.match()) {
                    print(response.getResponse(), false);
                    response.resetPos();
                } else {
                    response.nextPos();
                }
            } else {
                response.resetPos();
            }
        }
    }

    void reset() {
        waitFor.clear();
    }
    
    static class Prompt {
        private String prompt;

        private int pos;

        public Prompt(String prompt) {
            this.prompt = prompt;
            this.pos = 0;
        }

        public boolean matchChar(char c) {
            return (prompt.charAt(pos) == c);
        }

        public boolean match() {
            return pos + 1 == prompt.length();
        }

        public String getPrompt() {
            return prompt;
        }

        public void nextPos() {
            this.pos++;
        }

        public void resetPos() {
            this.pos = 0;
        }

    }

    static class Response extends Prompt {
        private String response;

        public Response(String prompt, String response) {
            super(prompt);
            this.response = response;
        }

        public String getResponse() {
            return response;
        }
    }

}