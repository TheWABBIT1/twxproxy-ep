package org.twdata.twxbbs;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 27/08/2008
 * Time: 10:32:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppIntegrationTest extends TestCase {

    private File baseDir;

    @Override
    public void setUp() {
        File tmpDir = getTempDirectory();
        baseDir = new File(tmpDir, "it-base");
        baseDir.mkdir();
    }

    @Override
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(baseDir);
    }
    private File getTempDirectory() {
        File tmpDir;
        File targetDir = new File("target");
        if (targetDir.exists()) {
            tmpDir = new File(targetDir, "tmp");
        } else {
            tmpDir = new File(System.getProperty("java.io.tmpdir"));
        }
        return tmpDir;
    }

    public void testNothing() {}

    /* Remove the underscore to run the tests via 'mvn clean test' */
    public void _testRunAllTests() throws Exception {
        new Container(new File(".")).stop();
        runTests(false, false);
        runTests(false, true);
        runTests(true, true);
    }

    public void runTests(boolean includeScript, boolean runThroughProxy) throws Exception {
        long avgStart = System.currentTimeMillis();
        for (int x=0; x<5; x++) {
            pushThroughData(includeScript, runThroughProxy);
            Thread.sleep(1000);
        }
        long avgEnd = System.currentTimeMillis();
        System.out.println("--------- Push in "+(((avgEnd-avgStart)-10000)/5)+" ms");

    }

    public void pushThroughData(boolean includeScript, final boolean runThroughProxy) throws Exception {
        FileUtils.writeStringToFile(new File(baseDir, "twxbbs.ini"),
                "[Proxy]\n" +
                "Port = 8023\n" +
                "TWGSPort = 2222" +
                "\n" +
                "[Web]\n" +
                "Port = 8084\n" +
                "\n" +
                "[Global]\n" +
                "Setup = 1");
        if (includeScript) {
            File scripts = new File(baseDir, "scripts");
            scripts.mkdir();
            FileUtils.writeStringToFile(new File(scripts, "foo.js"),
                    "println('script start');\n" +
                            "player.setTextTrigger('foo','God');\n" +
                            "while (true) {\n" +
                            "player.pause();\n" +
                            //"println('god found'+player.matchedLine);\n" +
                            "}");
        }

        Thread t = new Thread(new Runnable() {

            public void run() {
                ServerSocket server = null;
                try {
                    server = new ServerSocket(2222);
                    Socket incomingSocket = server.accept();
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    int lastLen = 0;
                    while ((len = incomingSocket.getInputStream().read(buffer)) > 0) {
                        lastLen = len;
                        if (buffer[len-1] == -1) {
                            break;
                        }
                    }
                    //System.out.println("received data: "+new String(buffer, 0, lastLen-1));
                    incomingSocket.close();
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
        t.start();
        Container container = new Container(baseDir);
        final int port = (runThroughProxy ? 8023 : 2222);
        System.out.println("Sending data to port "+port);
        Thread sendingThread = new Thread(new Runnable() {
            public void run() {
                Socket socket = null;
                try {
                    InputStream in = getClass().getResourceAsStream("/bible12.txt");
                    socket = new Socket("localhost", port);
                    Thread.sleep(1000);
                    IOUtils.copy(in, socket.getOutputStream());
                    socket.getOutputStream().write((byte)255);
                    socket.getOutputStream().flush();
                    //System.out.println("data sent");
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
        sendingThread.start();
        t.join();
        container.stop();
    }
}
