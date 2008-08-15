package de.mud.jta.plugin.hips;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 16/08/2008
 * Time: 01:08:53
 * To change this template use File | Settings | File Templates.
 */
public class TraderInfoPanel extends Box {

    private JTextField curSector;
    private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font VALUE_FONT = new Font("Monospaced", Font.BOLD, 12);

    public TraderInfoPanel() {
        super(BoxLayout.Y_AXIS);
        setBorder(new TitledBorder("Trader Info"));

        curSector = new JTextField();
        add(createField("Sector", curSector));
        add(createField("Turns", new JTextField("434")));
        add(createField("Credits", new JTextField("20k")));
        add(createField("Ship", new JTextField("Mule")));
    }

    public void updateCurrentSector(final int sector) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                curSector.setText(String.valueOf(sector));
            }
        });
    }

    private JPanel createField(String text, JTextField field) {
        JPanel panel = new JPanel(new GridLayout(1, 0, 3, 0));
        panel.setBorder(new EmptyBorder(1, 1, 1, 1));
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        panel.add(label);

        field.setColumns(7);
        field.setEditable(false);
        field.setEnabled(false);
        field.setOpaque(true);
        field.setHorizontalAlignment(JTextField.RIGHT);
        field.setFont(VALUE_FONT);
        field.setDisabledTextColor(Color.GREEN);
        panel.add(field);
        return panel;
    }
}
