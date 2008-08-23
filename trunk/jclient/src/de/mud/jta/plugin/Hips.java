package de.mud.jta.plugin;

import de.mud.jta.FilterPlugin;
import de.mud.jta.Plugin;
import de.mud.jta.PluginBus;
import de.mud.jta.VisualPlugin;
import de.mud.jta.plugin.hips.HipsMessageProcessor;
import de.mud.jta.plugin.hips.StatusPanel;
import de.mud.jta.event.OnlineStatusListener;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 14/08/2008
 * Time: 23:24:00
 * To change this template use File | Settings | File Templates.
 */
public class Hips extends Plugin
        implements FilterPlugin, VisualPlugin {

    private static final byte[] ANSI_CONCEAL = new byte[]{27, 91, 56, 109};
    private static final byte[] ANSI_REVEAL = new byte[]{27, 91, 50, 56, 109};
    private int concealPos = 0;
    private int revealPos = 0;

    private static final byte[] TELNET_WILL_HIPS = new byte[]{(byte) 255, (byte) 251, 76};
    private int willHipsPos = 0;
    private static final byte[] TELNET_DO_HIPS = new byte[]{(byte) 255, (byte) 253, 76};

    private final ByteArrayOutputStream hipsBuffer = new ByteArrayOutputStream();
    private final ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
    private final HipsMessageProcessor hipsMessageProcessor;

    private FilterPlugin source;
    private boolean inHips;

    /**
     * Create a new plugin and set the plugin bus used by this plugin and
     * the unique id. The unique id may be null if there is only one plugin
     * used by the system.
     *
     * @param bus the plugin bus
     * @param id  the unique plugin id
     */
    public Hips(PluginBus bus, String id) {
        super(bus, id);
        bus.registerPluginListener(new OnlineStatusListener() {
            public void online() {
                // AYT
                try {
                    System.out.println("Sending AYT");
                    source.write(new byte[]{(byte) 255, (byte) 253, (byte) 246});
                    source.write(new byte[]{(byte) 255, (byte) 246});
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            public void offline() {
            }
        });

        hipsMessageProcessor = HipsMessageProcessor.getInstance();

    }

    public void setFilterSource(FilterPlugin source) throws IllegalArgumentException {
        this.source = source;
    }

    public FilterPlugin getFilterSource() {
        return source;
    }

    public int read(byte[] barr) throws IOException {
        int len = source.read(barr);
        return scanBytesForHips(barr, len);
    }

    public void write(byte[] b) throws IOException {
        source.write(b);
    }

    public JComponent getPluginVisual() {
        return null;
    }

    public JMenu getPluginMenu() {
        return null;
    }

    public int scanBytesForHips(byte[] barr, int len) throws IOException {
        for (int x = 0; x < len; x++) {
            byte b = barr[x];

            if (Hips.TELNET_WILL_HIPS[willHipsPos] == b) {
                System.out.println("will hips pos " + willHipsPos);
                willHipsPos++;
                if (Hips.TELNET_WILL_HIPS.length == willHipsPos) {
                    source.write(Hips.TELNET_DO_HIPS);
                    System.out.println("DO HIPS");
                    willHipsPos = 0;
                }
            } else {
                willHipsPos = 0;
            }

            if (!inHips) {
                if (Hips.ANSI_CONCEAL[concealPos] == b) {
                    concealPos++;
                } else {
                    concealPos = 0;
                }
                if (Hips.ANSI_CONCEAL.length == concealPos) {
                    inHips = true;
                    concealPos = 0;
                    hipsBuffer.reset();
                }
                outputBuffer.write(b);
            } else {
                if (Hips.ANSI_REVEAL[revealPos] == b) {
                    revealPos++;
                } else {
                    revealPos = 0;
                }
                if (Hips.ANSI_REVEAL.length == revealPos) {
                    hipsMessageProcessor.process(new String(hipsBuffer.toByteArray(), 0, hipsBuffer.size() - 4));
                    revealPos = 0;
                    inHips = false;
                    outputBuffer.write(Hips.ANSI_REVEAL);
                } else {
                    hipsBuffer.write(b);
                }
            }
        }
        byte[] returnBytes = outputBuffer.toByteArray();
        System.arraycopy(returnBytes, 0, barr, 0, returnBytes.length);
        outputBuffer.reset();
        return returnBytes.length;
    }
}
