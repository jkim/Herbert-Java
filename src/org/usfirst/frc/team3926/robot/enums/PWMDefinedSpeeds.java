package org.usfirst.frc.team3926.robot.enums;

/**
 * Created by jkim on 2/7/16.
 */
public enum PWMDefinedSpeeds {
    FULL_SPEED_FORWARD(1),
    STOP(0),
    FULL_SPEED_REVERSE(-1);

    private final int id;
    PWMDefinedSpeeds(int id) {
        this.id = id;
    }
    public int getId() {
        return this.id;
    }
}
