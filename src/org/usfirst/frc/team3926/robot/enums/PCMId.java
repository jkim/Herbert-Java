package org.usfirst.frc.team3926.robot.enums;

/**
 * Created by jkim on 2/6/16.
 */
public enum PCMId {
    CAN_ID(1),
    LEFT_SIDE_REVERSE_CHANNEL_ID(1),
    LIFT_REVERSE_CHANNEL_ID(2),
    RIGHT_SIDE_REVERSE_CHANNEL_ID(3),
    RIGHT_SIDE_FORWARD_CHANNEL_ID(4),
    LIFT_FORWARD_CHANNEL_ID(5),
    LEFT_SIDE_FORWARD_CHANNEL_ID(6);

    private final int id;
    PCMId(int id) {
        this.id = id;
    }
    public int getId() {
        return this.id;
    }
}
