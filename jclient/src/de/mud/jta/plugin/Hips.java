package de.mud.jta.plugin;

import de.mud.jta.FilterPlugin;
import de.mud.jta.Plugin;
import de.mud.jta.PluginBus;
import de.mud.jta.VisualPlugin;
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
                    source.write(new byte[]{(byte) 255, (byte) 246});
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

            public void offline() {
            }
        });
    }

    public void setFilterSource(FilterPlugin source) throws IllegalArgumentException {
        this.source = source;
    }

    public FilterPlugin getFilterSource() {
        return source;
    }

    public int read(byte[] barr) throws IOException {
        int len = source.read(barr);
        for (int x = 0; x < len; x++) {
            byte b = barr[x];

            if (TELNET_WILL_HIPS[willHipsPos] == b) {
                System.out.println("will hips pos " + willHipsPos);
                willHipsPos++;
                if (TELNET_WILL_HIPS.length == willHipsPos) {
                    source.write(TELNET_DO_HIPS);
                    System.out.println("DO HIPS");
                    willHipsPos = 0;
                }
            } else {
                willHipsPos = 0;
            }

            System.out.println("In hips: "+inHips+" concealPos:"+concealPos+" revealPos:"+revealPos);
            if (!inHips) {
                if (ANSI_CONCEAL[concealPos] == b) {
                    concealPos++;
                } else {
                    concealPos = 0;
                }
                if (ANSI_CONCEAL.length == concealPos) {
                    inHips = true;
                    concealPos = 0;
                    hipsBuffer.reset();
                }
            } else {
                if (ANSI_REVEAL[revealPos] == b) {
                    revealPos++;
                } else {
                    revealPos = 0;
                }
                if (ANSI_REVEAL.length == revealPos) {
                    System.out.println("Collected " + new String(hipsBuffer.toByteArray(), 0, hipsBuffer.size() - 2));
                    revealPos = 0;
                    inHips = false;
                } else {
                    hipsBuffer.write(b);
                }
            }
        }
        return len;
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
}
