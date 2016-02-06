/* Created Fri Feb 05 07:22:58 CST 2016 */
package org.usfirst.frc.team3926.robot;

import org.strongback.Strongback;
import org.strongback.components.Motor;
import org.strongback.components.PneumaticsModule;
import org.strongback.hardware.Hardware;

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
	Motor strongBackTalon_FL;
	Talon talon_FL;
	Talon talon_BL;
	Talon talon_FR;
    Talon talon_BR;
    
    RobotDrive driveSystem;
    Talon armWheels;
    Talon mysteryTalon; // talon of unknown origin 
    
    Joystick leftStick;
    double leftInput;
    Joystick rightStick;
    double rightInput;
    
    DigitalInput topLimit;
    DigitalInput botLimit;
    int debounce = 0;
    
    Compressor compressor;
    DoubleSolenoid mainLift; //The main giant cylinder
    DoubleSolenoid sideLiftR; //The right side cylinder
    DoubleSolenoid sideLiftL; //The left side cylinder 
    
    final int ID           = 1; //ID number of the PCM (pneumatics control module)
    final int liftForward  = 5; //These need to be the channel numbers on the PCM (only like this so we can write other code)
    final int liftReverse  = 2;
    final int rSideForward = 4;
    final int rSideReverse = 3;
    final int lSideForward = 6;
    final int lSideReverse = 1;

    final int TALON_FL_ID = 0;
    final int TALON_BL_ID = 1;
    final int TALON_FR_ID = 2;
    final int TALON_BR_ID = 3;

    /**
     * Main Robot initialization
     */
    @Override
    public void robotInit() {
        strongBackTalon_FL = Hardware.Motors.talon(TALON_FL_ID);
        talon_FL = new Talon(TALON_FL_ID);
        talon_BL = new Talon(TALON_BL_ID);
        talon_FR = new Talon(TALON_FR_ID);
        talon_BR = new Talon(TALON_BR_ID);
        driveSystem = new RobotDrive(talon_FL, talon_BL, talon_FR, talon_BR);
        armWheels = new Talon(8);

        leftStick = new Joystick(0);
        rightStick = new Joystick(1);

        topLimit = new DigitalInput(9);
        botLimit = new DigitalInput(8);

        compressor = new Compressor(ID);
        compressor.setClosedLoopControl(true);
        mainLift = new DoubleSolenoid(ID, liftForward, liftReverse);
        sideLiftR = new DoubleSolenoid(ID, rSideForward, rSideReverse);
        sideLiftL = new DoubleSolenoid(ID, lSideForward, lSideReverse);
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
     * @param value: The value to set all solenoids to (forward, reverse, or off);
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
     *
     */
    @Override
    public void disabledInit() {
        // Tell Strongback that the robot is disabled so it can flush and kill commands.
        Strongback.disable();
    }

    /**
     *
     * @return
     */
    public double leftStickReturn() {
        return leftStick.getY() * -1;
    }

    /**
     *
     * @return
     */
    public double rightStickReturn() {
        return rightStick.getY() * -1;
    }
}
