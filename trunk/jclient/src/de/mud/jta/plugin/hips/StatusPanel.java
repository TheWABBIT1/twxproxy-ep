package de.mud.jta.plugin.hips;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 16/08/2008
 * Time: 01:06:48
 * To change this template use File | Settings | File Templates.
 */
public class StatusPanel extends JPanel {
    private TraderInfoPanel tInfoPanel;

    public StatusPanel() {
        super();
        setPreferredSize(new Dimension(150, 200));
        setLayout(new FlowLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED, Color.BLUE, Color.GRAY));
        Box box = new Box(BoxLayout.Y_AXIS);

        tInfoPanel = new TraderInfoPanel();
        box.add(tInfoPanel);
        add(box);
    }

    public void updateCurrentSector(int curSector) {
        tInfoPanel.updateCurrentSector(curSector);
    }
}
