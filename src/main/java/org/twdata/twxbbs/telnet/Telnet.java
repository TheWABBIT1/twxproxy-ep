package org.twdata.twxbbs.telnet;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 10/08/2008
 * Time: 19:13:24
 * To change this template use File | Settings | File Templates.
 */
public class Telnet {
    /**  IAC - init sequence for telnet negotiation. */
    public final static byte IAC = (byte) 255;
    /**  [IAC] End Of Record */
    public final static byte EOR = (byte) 239;
    /**  [IAC] WILL */
    public final static byte WILL = (byte) 251;
    /**  [IAC] WONT */
    public final static byte WONT = (byte) 252;
    /**  [IAC] DO */
    public final static byte DO = (byte) 253;
    /**  [IAC] DONT */
    public final static byte DONT = (byte) 254;
    /**  [IAC] Sub Begin */
    public final static byte SB = (byte) 250;
    /**  [IAC] Sub End */
    public final static byte SE = (byte) 240;
    /**  Telnet option: binary mode */
    public final static byte TELOPT_BINARY = (byte) 0;
    /* binary mode */
    /**  Telnet option: echo text */
    public final static byte TELOPT_ECHO = (byte) 1;
    /* echo on/off */
    /**  Telnet option: sga */
    public final static byte TELOPT_SGA = (byte) 3;
    /* supress go ahead */
    /**  Telnet option: End Of Record */
    public final static byte TELOPT_EOR = (byte) 25;
    /* end of record */
    /**  Telnet option: Negotiate About Window Size */
    public final static byte TELOPT_NAWS = (byte) 31;
    /* NA-WindowSize*/
    /**  Telnet option: Terminal Type */
    public final static byte TELOPT_TTYPE = (byte) 24;

    public final static byte TELOPT_STATUS = (byte) 5;
    public final static byte TELOPT_TSPEED = (byte) 32;
    public final static byte TELOPT_LFLOW = (byte) 33;
    public final static byte TELOPT_LINEMODE = (byte) 34;
    public final static byte TELOPT_NEW_ENVIRON = (byte) 39;
    public final static byte TELOPT_OLD_ENVIRON = (byte) 36;
    
    /* terminal type */
    public final static byte[] IACWILL = {IAC, WILL};
    public final static byte[] IACWONT = {IAC, WONT};
    public final static byte[] IACDO = {IAC, DO};
    public final static byte[] IACDONT = {IAC, DONT};
    public final static byte[] IACSB = {IAC, SB};
    public final static byte[] IACSE = {IAC, SE};

    /**  Telnet option qualifier 'IS' */
    public final static byte TELQUAL_IS = (byte) 0;
    /**  Telnet option qualifier 'SEND' */
    public final static byte TELQUAL_SEND = (byte) 1;

    public final static byte[] START_INSTRUCTIONS = new byte[] {
               IAC, DO, TELOPT_BINARY,
               IAC, WILL, TELOPT_BINARY,
               IAC, WILL, TELOPT_SGA,
               IAC, WONT, TELOPT_STATUS,
               IAC, DONT, TELOPT_TTYPE,
               IAC, DONT, TELOPT_NAWS,
               IAC, DONT, TELOPT_TSPEED,
               IAC, DONT, TELOPT_LFLOW,
               IAC, DONT, TELOPT_LINEMODE,
               IAC, WONT, TELOPT_NEW_ENVIRON,
               IAC, DONT, TELOPT_NEW_ENVIRON,
               IAC, WONT, TELOPT_OLD_ENVIRON,
               IAC, DONT, TELOPT_OLD_ENVIRON
        };
}
