package org.usfirst.frc.team3926.robot.enums;

/**
 * Created by jkim on 2/6/16.
 */
public enum LimitSwitchId {
    TOP_ID(9),
    BOTTOM_ID(8);

    private final int id;
    LimitSwitchId(int id) {
        this.id = id;
    }
    public int getId() {
        return this.id;
    }
}
