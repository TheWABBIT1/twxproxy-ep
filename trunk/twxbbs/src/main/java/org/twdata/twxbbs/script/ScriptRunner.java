package org.twdata.twxbbs.script;

import java.io.*;
import java.util.Vector;


public abstract class ScriptRunner implements Runnable {

    private PipedReader reader;
    private PipedWriter writer;

    public void run() {
        char[] b = new char[256];
        int n = 0;
        while (n >= 0)
            n = read(b);
    }

    private final Object waitForSynch = new Object();
    private boolean waitingForLine = false;
    private boolean waitingFor = false;
    private char watchString[][] = null;
    private int currentIndex[];
    private int matchNumber;
    private String lastLine;
    private StringBuffer prevLine, currentLine = new StringBuffer();

    private int timeOut = 500;

    public void setTimeout(int t) {
        synchronized (waitForSynch) {
            timeOut = t;
        }
    }


    public int waitFor(String toWait[]) {
        int result = -1;
        synchronized (waitForSynch) {
            waitingFor = true;
            watchString = new char[toWait.length][];
            currentIndex = new int[toWait.length];
            matchNumber = 640;
            currentLine = new StringBuffer();

            clearResponseState();

            //new char[toWait.length()];
            for (int counter = 0; counter < toWait.length; counter++) {
                watchString[counter] = new char[toWait[counter].length()];
                toWait[counter].getChars(0, toWait[counter].length(), watchString[counter], 0);
                currentIndex[counter] = -1;
            }

            handleIncomingData(backBuffer.toString());


            if (waitingFor) {

                clearBackBuffer();
                try {

                    waitForSynch.wait(timeOut);
                    synchronized (waitForSynch) {

                        if (matchNumber == 640) {
                            waitingFor = false;
                        }
                        result = matchNumber;
                    }
                }
                catch (InterruptedException e) {
                    return 640;
                }
            } else {
                result = matchNumber;
            }


        }

        return result;
    }

    public String waitForLine() {
        synchronized (waitForSynch) {
            waitingForLine = true;

            clearResponseState();
            handleIncomingData(backBuffer.toString());

            if (waitingForLine) {

                clearBackBuffer();
                try {
                    waitForSynch.wait();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                    return prevLine.toString();
                }
            }

        }

        return prevLine.toString();
    }


    public String getLastLine() {
        return lastLine;
    }

    StringBuffer buffer = new StringBuffer();
    boolean doBuffer;
    int bufferMax;

    public String setBuffer(int size) {
        String result;

        synchronized (waitForSynch) {
            result = buffer.toString();
            buffer = new StringBuffer();
            bufferMax = size;
            if (size > 0)
                doBuffer = true;
            else
                doBuffer = false;
        }

        return result;
    }

    Vector responseKeys = new Vector();
    Vector responses = new Vector();
    Vector responseIndex = new Vector();

    public void addResponse(String key, String response) {
        synchronized (waitForSynch) {
            char keyChars[] = new char[key.length()];
            key.getChars(0, key.length(), keyChars, 0);
            responseKeys.addElement(keyChars);
            responses.addElement(response);
            responseIndex.addElement(new Integer(-1));
        }
    }

    public void clearResponseState() {
        synchronized (waitForSynch) {
            int size = responseIndex.size();
            responseIndex = new Vector();
            for (int counter = 0; counter < size; counter++)
                responseIndex.addElement(new Integer(-1));
        }
    }

    public void removeAllResponses() {
        synchronized (waitForSynch) {
            responseKeys.removeAllElements();
            responseIndex.removeAllElements();
            responses.removeAllElements();
        }
    }

    public void removeResponse(String key) {
        synchronized (waitForSynch) {
            for (int counter = 0; counter < responseKeys.size(); counter++) {
                String k = new String((char[]) responseKeys.elementAt(counter));
                if (k.equals(key)) {

                    responseKeys.removeElementAt(counter);
                    responses.removeElementAt(counter);
                    responseIndex.removeElementAt(counter);
                }
            }
        }
    }


    final StringBuffer backBuffer = new StringBuffer();

    public void clearBackBuffer() {
        synchronized (backBuffer) {
            backBuffer.delete(0, backBuffer.length());
        }

    }


    public void handleIncomingData(String checkString) {
        synchronized (waitForSynch) {


            synchronized (backBuffer) {
                if (!waitingFor && !waitingForLine) {
                    backBuffer.append(checkString);

                }
            }

            for (int counter = 0; (waitingFor || waitingForLine) && counter < checkString.length(); counter++) {
                char currentChar = checkString.charAt(counter);

                if (doBuffer && buffer.length() < bufferMax)
                    buffer.append(currentChar);

                if (currentChar != '\n' && currentChar != '\r')
                    currentLine.append(currentChar);
                else if (currentChar == '\n') {

                    prevLine = currentLine;
                    currentLine = new StringBuffer();

//                        if (counter < checkString.length() - 1) //make sure we're not at the end
//                        {
//                            int nextNewLine = checkString.indexOf('\n', counter + 1);
//                            if (nextNewLine > -1)
//                            {
//                                currentLine.append(checkString.substring(counter + 1, nextNewLine));
//                            }
//                            else
//                            {
//                                currentLine.append(checkString.substring(counter + 1));
//                            }
//                        }


                    if (waitingForLine) {
                        if (prevLine.length() > 0) {
                            waitForSynch.notifyAll();
                            waitingForLine = false;
                        }
                    }
                }

                for (int wcounter = 0; waitingFor && wcounter < watchString.length; wcounter++) {
                    if (watchString[wcounter][currentIndex[wcounter] + 1] == currentChar) {
                        currentIndex[wcounter]++;
                        if (currentIndex[wcounter] + 1 == watchString[wcounter].length) {
                            waitForSynch.notifyAll();
                            waitingFor = false;
                            if (currentChar != '\n')
                                lastLine = currentLine.toString();
                            else
                                lastLine = prevLine.toString();


                            matchNumber = wcounter;
                            handleResponses(currentChar);
                            if (counter < checkString.length() - 1) {

                                clearBackBuffer();
                                backBuffer.append(checkString.substring(counter + 1));
                            }
                        }
                    } else {
                        currentIndex[wcounter] = -1;
                    }
                }

                if (waitingFor || waitingForLine) {
                    handleResponses(currentChar);
                }
            }

        }
    }


    public void handleResponses(char currentChar) {
        for (int wcounter = 0; wcounter < responseKeys.size(); wcounter++) {
            char keyString[] = (char[]) responseKeys.elementAt(wcounter);
            int cindex = ((Integer) responseIndex.elementAt(wcounter)).intValue();

            if (keyString[cindex + 1] == currentChar) {
                cindex++;
                if (cindex + 1 == keyString.length) {

                    cindex = -1;
                    write(((String) responses.elementAt(wcounter)));
                }
            } else {
                cindex = -1;
            }

            responseIndex.removeElementAt(wcounter);
            responseIndex.insertElementAt(new Integer(cindex), wcounter);
        }


    }


    public synchronized int read(char[] b) {
        try {
            int len = getReader().read(b);
            if (len > -1)
                handleIncomingData(new String(b, 0, len));

            return len;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Writer getWriter() {

        if (writer == null) {
            try {
                reader = new PipedReader();
                writer = new PipedWriter(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return writer;
    }

    public Reader getReader() {

        if (reader == null) {
            try {
                reader = new PipedReader();
                writer = new PipedWriter(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return reader;
    }


    public void closeOutputStream() {
        try {
            if (writer != null) {
                reader.close();
                reader = null;
                writer.close();
                writer = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public abstract void write(String text);
}
