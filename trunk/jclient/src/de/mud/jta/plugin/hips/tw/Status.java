package de.mud.jta.plugin.hips.tw;

import com.twolattes.json.Entity;
import com.twolattes.json.Value;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 15/08/2008
 * Time: 21:26:44
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Status {
    @Value
    private int sector;

    public int getSector() {
        return sector;
    }
}
