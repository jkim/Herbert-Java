package org.usfirst.frc.team3926.robot.enums;

/**
 * Created by jkim on 2/6/16.
 */
public enum JoystickId {
    LEFT_ID(0),
    RIGHT_ID(1);

    private final int id;
    JoystickId(int id) {
        this.id = id;
    }
    public int getId() {
        return this.id;
    }
}
