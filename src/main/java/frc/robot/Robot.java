/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.subsystems.Drivetrain;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
    Joystick joystick = new Joystick(0);

    Drivetrain drivetrain;

    @Override
    public void robotInit() {
        inititalizeDrivetrain(3, 4, 1, 2);
    }

    @Override
    public void autonomousInit() {
    }

    @Override
    public void autonomousPeriodic() {
    }

    @Override
    public void teleopInit() {
    }

    @Override
    public void teleopPeriodic() {
        drivetrain.arcadeDrive(-joystick.getY(), joystick.getX());
    }

    @Override
    public void testInit() {
    }

    @Override
    public void testPeriodic() {
    }

    public void inititalizeDrivetrain(int frontLeft, int backLeft, int frontRight, int backRight) {
        CANSparkMax frontLeftMotor = new CANSparkMax(frontLeft, MotorType.kBrushless);
        CANSparkMax backLeftMotor = new CANSparkMax(backLeft, MotorType.kBrushless);
        CANSparkMax frontRightMotor = new CANSparkMax(frontRight, MotorType.kBrushless);
        CANSparkMax backRightMotor = new CANSparkMax(backRight, MotorType.kBrushless);

        REVConfig.configureNeo(frontLeftMotor);
        REVConfig.configureNeo(backLeftMotor);
        REVConfig.configureNeo(frontRightMotor);
        REVConfig.configureNeo(backRightMotor);

        backLeftMotor.follow(frontLeftMotor);
        backRightMotor.follow(frontRightMotor);

        drivetrain = new Drivetrain(frontLeftMotor, frontRightMotor);
    }
}
