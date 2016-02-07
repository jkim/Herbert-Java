package org.usfirst.frc.team3926.robot.enums;

/**
 * Created by jkim on 2/7/16.
 */
public enum JoystickButtonId {
    /*
       Name these! Call them something that matches
       physical identity like BUTTON_X(1)
    */
    BUTTON_1(1),
    BUTTON_2(2),
    BUTTON_3(3),
    BUTTON_4(4);

    private final int id;
    JoystickButtonId(int id) {
        this.id = id;
    }
    public int getId() {
        return this.id;
    }
}
