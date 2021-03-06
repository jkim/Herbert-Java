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

import org.usfirst.frc.team3926.robot.enums.PCMId;
import org.usfirst.frc.team3926.robot.enums.TalonId;
import org.usfirst.frc.team3926.robot.enums.JoystickId;
import org.usfirst.frc.team3926.robot.enums.LimitSwitchId;
import org.usfirst.frc.team3926.robot.enums.JoystickButtonId;
import org.usfirst.frc.team3926.robot.enums.PWMDefinedSpeeds;

public class Robot extends IterativeRobot {
    // Pneumatic Control System Objects
    private Compressor compressor;
    private DoubleSolenoid mainLift; //The main giant cylinder
    private DoubleSolenoid sideLiftR; //The right side cylinder
    private DoubleSolenoid sideLiftL; //The left side cylinder

    // TalonId Motor Controller Objects
    private Talon talonFrontLeft;
    private Talon talonBackLeft;
    private Talon talonFrontRight;
    private Talon talonBackRight;
    private Talon armWheels;
    private Talon mysteryTalon; // TalonId of unknown origin

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
        talonFrontLeft     = new Talon(TalonId.FRONT_LEFT_CAN_ID.getId());
        talonBackLeft      = new Talon(TalonId.BACK_LEFT_CAN_ID.getId());
        talonFrontRight    = new Talon(TalonId.FRONT_RIGHT_CAN_ID.getId());
        talonBackRight     = new Talon(TalonId.BACK_RIGHT_CAN_ID.getId());
        armWheels          = new Talon(TalonId.ARM_WHEELS_CAN_ID.getId());

        // Initialize Drive System
        driveSystem = new RobotDrive(talonFrontLeft, talonBackLeft, talonFrontRight, talonBackRight);

        // Initialize Human Interface Controllers
        leftStick  = new Joystick(JoystickId.LEFT_ID.getId());
        rightStick = new Joystick(JoystickId.RIGHT_ID.getId());

        // Initialize Limit Switches
        topLimit = new DigitalInput(LimitSwitchId.TOP_ID.getId());
        botLimit = new DigitalInput(LimitSwitchId.BOTTOM_ID.getId());

        // Initialize Pneumatic Control System
        compressor = new Compressor(PCMId.CAN_ID.getId());
        compressor.setClosedLoopControl(true);
        mainLift   = new DoubleSolenoid(
                PCMId.CAN_ID.getId(),
                PCMId.LIFT_FORWARD_CHANNEL_ID.getId(),
                PCMId.LIFT_FORWARD_CHANNEL_ID.getId()
        );
        sideLiftR  = new DoubleSolenoid(
                PCMId.CAN_ID.getId(),
                PCMId.RIGHT_SIDE_FORWARD_CHANNEL_ID.getId(),
                PCMId.RIGHT_SIDE_REVERSE_CHANNEL_ID.getId()
        );
        sideLiftL  = new DoubleSolenoid(
                PCMId.CAN_ID.getId(),
                PCMId.LEFT_SIDE_FORWARD_CHANNEL_ID.getId(),
                PCMId.LEFT_SIDE_REVERSE_CHANNEL_ID.getId()
        );
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
        leftInput  = leftStickReturn();

// Is this intentionally duplicated?
//        if (leftStick.getRawButton(JoystickButtonId.BUTTON_1.getId())) {
//            rightInput = leftInput;
//        }

        /*
            Document what is going on here.
        */
        if (leftStick.getRawButton(JoystickButtonId.BUTTON_1.getId())) {
            rightInput = leftInput;
        }

        driveSystem.tankDrive(leftInput, rightInput);

        /*
            Document and encapsulate the logic here
        */
        if (leftStick.getRawButton(JoystickButtonId.BUTTON_2.getId())) {
            armWheels.set(PWMDefinedSpeeds.FULL_SPEED_FORWARD.getId());
        } else if (leftStick.getRawButton(JoystickButtonId.BUTTON_3.getId())) {
            armWheels.set(PWMDefinedSpeeds.FULL_SPEED_REVERSE.getId());
        }
        else {
            armWheels.set(PWMDefinedSpeeds.STOP.getId());
        }

        /*
            Documents what the topLimit and botLimit functionality is doing,
            and if applicable encapsulate.
        */
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

        /*
            Encapsulate (into objects and/or objects and methods) this logic into something concise
        */
        if (rightStick.getRawButton(JoystickButtonId.BUTTON_1.getId())) {
            solenoidControl(DoubleSolenoid.Value.kForward);
        } else if (rightStick.getRawButton(JoystickButtonId.BUTTON_4.getId())) {
            solenoidControl(DoubleSolenoid.Value.kReverse);
        } else if (leftStick.getRawButton(JoystickButtonId.BUTTON_1.getId())) {
            mainLift.set(DoubleSolenoid.Value.kForward);
        } else if (leftStick.getRawButton(JoystickButtonId.BUTTON_4.getId())) {
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
