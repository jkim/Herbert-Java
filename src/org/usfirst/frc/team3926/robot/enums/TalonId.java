package org.usfirst.frc.team3926.robot.enums;

/**
 * Created by jkim on 2/6/16.
 */
public enum TalonId {
    FRONT_LEFT_CAN_ID(0),
    BACK_LEFT_CAN_ID(1),
    FRONT_RIGHT_CAN_ID(2),
    BACK_RIGHT_CAN_ID(3),
    ARM_WHEELS_CAN_ID(8);

    private final int id;
    TalonId(int id) {
        this.id = id;
    }
    public int getId() {
        return this.id;
    }
}
