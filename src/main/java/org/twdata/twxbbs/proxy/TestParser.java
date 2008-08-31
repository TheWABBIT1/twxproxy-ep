package org.twdata.twxbbs.proxy;

import java.io.Reader;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileNotFoundException;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 25/08/2008
 * Time: 10:36:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestParser {

    public static void main(String[] args) throws IOException {
        for (int x = 0; x<20; x++) {
            String[] lines = new String[x];
            for (int y=0; y<x;y++) {
                lines[y] = "and to his";
            }
            run(5, lines);
        }
    }

    public static void run(int times, String... lines) throws IOException {
        long time = 0;
        for (int x=0; x<times; x++) {
            long start = System.currentTimeMillis();
            Parser parser = new Parser(lines);
            parser.parse(new FileReader("/home/mrdon/incoming/bible12.txt"));
            long end = System.currentTimeMillis();
            time+= end - start;
        }
        //System.out.println("Average times for "+lines.length+" lines: "+(time/times)+"ms");
        System.out.print((time/times)+",");
    }
}

class Parser {
    String[] lines;
    int[] linePos;

    public Parser(String... lines) {
        this.lines = lines;
        this.linePos = new int[lines.length];
    }

    public void parse(Reader reader) throws IOException {

        char[] buffer = new char[1024];
        int len = 0;
        while ((len = reader.read(buffer)) > 0) {
            for (int pos = 0; pos < len; pos++) {
                char c = buffer[pos];
                for (int lineNum = 0; lineNum < lines.length; lineNum++) {
                    if (lines[lineNum].charAt(linePos[lineNum]) == c) {
                        linePos[lineNum]++;
                    } else {
                        linePos[lineNum] = 0;
                    }
                    if (linePos[lineNum] == lines[lineNum].length()) {
                        //System.out.println("Found "+lines[lineNum]);
                        linePos[lineNum] = 0;
                    }
                }
            }
        }
    }
}
