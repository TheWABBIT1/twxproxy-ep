package de.mud.jta.plugin.hips.tw;

import de.mud.jta.plugin.hips.tw.Status;
import de.mud.jta.plugin.hips.StatusPanel;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 15/08/2008
 * Time: 21:39:15
 * To change this template use File | Settings | File Templates.
 */
public class TwMessageHandler {
    private StatusPanel statusPanel;

    public TwMessageHandler(StatusPanel statusPanel) {
        this.statusPanel = statusPanel;
    }

    public void handle(Status status) {
        System.out.println("Current sector: "+status.getSector());
        statusPanel.updateCurrentSector(status.getSector());
    }
}
