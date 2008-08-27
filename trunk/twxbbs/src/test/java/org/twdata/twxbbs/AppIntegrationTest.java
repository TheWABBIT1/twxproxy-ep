package org.twdata.twxbbs;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

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

    public void testPushThroughData() throws Exception {
        /* Work in progress...need to find a way to shut down the app
        File tmpDir = getTempDirectory();
        final File baseDir = new File(tmpDir, "it-base");
        baseDir.mkdir();

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

        App.main(new String[]{baseDir.getAbsolutePath()});
        InputStream in = getClass().getResourceAsStream("/bible12.txt");
        ServerSocket server = new ServerSocket(2222);
        Socket socket = server.accept();
        IOUtils.copy(in, socket.getOutputStream());
        in.close();
        socket.close();
        server.close();
        */

    }
}
