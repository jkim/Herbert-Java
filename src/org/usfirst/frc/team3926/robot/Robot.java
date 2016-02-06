/* Created Fri Feb 05 07:22:58 CST 2016 */
package org.usfirst.frc.team3926.robot;

import org.strongback.Strongback;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
    // PCM Constants
    public static final int PCM_CAN_ID                        = 1;
    public static final int PCM_LEFT_SIDE_REVERSE_CHANNEL_ID  = 1;
    public static final int PCM_LIFT_REVERSE_CHANNEL_ID       = 2;
    public static final int PCM_RIGHT_SIDE_REVERSE_CHANNEL_ID = 3;
    public static final int PCM_RIGHT_SIDE_FORWARD_CHANNEL_ID = 4;
    public static final int PCM_LIFT_FORWARD_CHANNEL_ID       = 5;
    public static final int PCM_LEFT_SIDE_FORWARD_CHANNEL_ID  = 6;

    // Talon Constants
    public static final int TALON_FRONT_LEFT_CAN_ID  = 0;
    public static final int TALON_BACK_LEFT_CAN_ID   = 1;
    public static final int TALON_FRONT_RIGHT_CAN_ID = 2;
    public static final int TALON_BACK_RIGHT_CAN_ID  = 3;
    public static final int TALON_ARM_WHEELS_CAN_ID  = 8;

    // Human Interface Controller Constants
    public static final int LEFT_JOYSTICK_ID  = 0;
    public static final int RIGHT_JOYSTICK_ID = 1;

    // Limit Switch Constants
    public static final int TOP_LIMIT_SWITCH_ID    = 9;
    public static final int BOTTOM_LIMIT_SWITCH_ID = 8;

    // Pneumatic Control System Objects
    private Compressor compressor;
    private DoubleSolenoid mainLift; //The main giant cylinder
    private DoubleSolenoid sideLiftR; //The right side cylinder
    private DoubleSolenoid sideLiftL; //The left side cylinder

    // Talon Motor Controller Objects
    private Talon talonFrontLeft;
    private Talon talonBackLeft;
    private Talon talonFrontRight;
    private Talon talonBackRight;
    private Talon armWheels;
    private Talon mysteryTalon; // Talon of unknown origin

    // Drive System Object
    private RobotDrive driveSystem;

    // Human Interface Controller Objects
    private Joystick leftStick;
    private Joystick rightStick;

    // Limit Switch Objects
    private DigitalInput topLimit;
    private DigitalInput botLimit;

    // Misc object members
    double leftInput;
    double rightInput;
    int debounce = 0;
    
    /**
     * Main Robot initialization
     */
    @Override
    public void robotInit() {
        // Initialize Motor Controllers
        talonFrontLeft     = new Talon(TALON_FRONT_LEFT_CAN_ID);
        talonBackLeft      = new Talon(TALON_BACK_LEFT_CAN_ID);
        talonFrontRight    = new Talon(TALON_FRONT_RIGHT_CAN_ID);
        talonBackRight     = new Talon(TALON_BACK_RIGHT_CAN_ID);
        armWheels          = new Talon(TALON_ARM_WHEELS_CAN_ID);

        // Initialize Drive System
        driveSystem = new RobotDrive(talonFrontLeft, talonBackLeft, talonFrontRight, talonBackRight);

        // Initialize Human Interface Controllers
        leftStick  = new Joystick(LEFT_JOYSTICK_ID);
        rightStick = new Joystick(RIGHT_JOYSTICK_ID);

        // Initialize Limit Switches
        topLimit = new DigitalInput(TOP_LIMIT_SWITCH_ID);
        botLimit = new DigitalInput(BOTTOM_LIMIT_SWITCH_ID);

        // Initialize Pneumatic Control System
        compressor = new Compressor(PCM_CAN_ID);
        compressor.setClosedLoopControl(true);
        mainLift   = new DoubleSolenoid(PCM_CAN_ID, PCM_LIFT_FORWARD_CHANNEL_ID, PCM_LIFT_REVERSE_CHANNEL_ID);
        sideLiftR  = new DoubleSolenoid(PCM_CAN_ID, PCM_RIGHT_SIDE_FORWARD_CHANNEL_ID, PCM_RIGHT_SIDE_REVERSE_CHANNEL_ID);
        sideLiftL  = new DoubleSolenoid(PCM_CAN_ID, PCM_LEFT_SIDE_FORWARD_CHANNEL_ID, PCM_LEFT_SIDE_REVERSE_CHANNEL_ID);
    }

    /**
     * Pre-Driver initialization
     */
    @Override
    public void teleopInit() {
        // Start Strongback functions ...
        Strongback.start();
    }

    /**
     * Driver control loop
     */
    @Override
    public void teleopPeriodic() {
        rightInput = rightStickReturn();
        leftInput = leftStickReturn();

        if (leftStick.getRawButton(1)) {
            rightInput = leftInput;
        }

        if (leftStick.getRawButton(1)) {
            rightInput = leftInput;
        }

        driveSystem.tankDrive(leftInput, rightInput);

        if (leftStick.getRawButton(2)) {
            armWheels.set(1);
        } else if (leftStick.getRawButton(3)) {
            armWheels.set(-1);
        }
        else {
            armWheels.set(0);
        }

        while(topLimit.get()) {
            ++debounce;
        }

        if (!topLimit.get()) {
            debounce = 0;
        }

        while(botLimit.get()) {
            ++debounce;
        }

        if (!botLimit.get()) {
            debounce = 0;
        }

        if (rightStick.getRawButton(1)) {
            solenoidControl(DoubleSolenoid.Value.kForward);
        } else if (rightStick.getRawButton(4)) {
            solenoidControl(DoubleSolenoid.Value.kReverse);
        } else if (leftStick.getRawButton(1)) {
            mainLift.set(DoubleSolenoid.Value.kForward);
        } else if (leftStick.getRawButton(4)) {
            mainLift.set(DoubleSolenoid.Value.kReverse);
        } else  {
            solenoidControl(DoubleSolenoid.Value.kOff);
            mainLift.set(DoubleSolenoid.Value.kOff);
        }

        if (debounce >= 20) {
            solenoidControl(DoubleSolenoid.Value.kOff);
            mainLift.set(DoubleSolenoid.Value.kOff);
        }

        SmartDashboard.putInt("Debounce", debounce);
    }

    /**
     * @param value The value to set all solenoids to (forward, reverse, or off);
     */
    public void solenoidControl(Value value) {
        sideLiftR.set(value);
        sideLiftL.set(value);
    }

    int debounceCounter = 0;
    /**
    * @param limitSwitch: The name of the limit switch which we are looking at
    * @param joystick: The name of the joystick who's button we will check
    * @param button: the button on the joystick which we will check
    * @return If true, the limit switch is actually pressed and the joystick is actually pressed
    */
    public boolean debounceLimit(DigitalInput limitSwitch, Joystick joystick, int button) {
        boolean check = false;

        if (joystick.getRawButton(button)) {
            if (limitSwitch.get()) {
                ++debounceCounter;
            }
            else {
                debounceCounter = 0;
            }

            if (debounceCounter > 20) {
                check = true;
            }
            else {
                check = false;
            }
        }
        else {
            check = false;
            debounceCounter = 0;
        }

        return check;
    }

    /**
     * After the robot is disabled, flush and unload things from the Strongback framework
     */
    @Override
    public void disabledInit() {
        // Tell Strongback that the robot is disabled so it can flush and kill commands.
        Strongback.disable();
    }

    /**
     * Get the value from the left joystick Y axis, then invert and return it.
     *
     * @return double Inverted Y axis coordinate for the left joystick
     */
    public double leftStickReturn() {
        return leftStick.getY() * -1;
    }

    /**
     * Get the value from the right joystick Y axis, then invert and return it.
     *
     * @return double Inverted Y axis coordinate for the right joystick
     */
    public double rightStickReturn() {
        return rightStick.getY() * -1;
    }
}
