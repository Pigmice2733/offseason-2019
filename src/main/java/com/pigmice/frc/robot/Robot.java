/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.pigmice.frc.robot;

import com.kauailabs.navx.frc.AHRS;
import com.pigmice.frc.robot.subsystems.Drivetrain;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.TimedRobot;

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

        CameraServer server = CameraServer.getInstance();
        server.startAutomaticCapture("Driver Cam", 0);

        Vision.start();
    }

    @Override
    public void autonomousInit() {
        drivetrain.resetSensors();
    }

    @Override
    public void autonomousPeriodic() {

    }
    @Override
    public void teleopInit() {
        drivetrain.stop();
    }

    @Override
    public void teleopPeriodic() {
        if(joystick.getRawButton(5)) {
            drivetrain.arcadeDrive(-joystick.getRawAxis(1), joystick.getRawAxis(4));
        } else {
            drivetrain.arcadeDrive(-0.1 * joystick.getRawAxis(1), 0.1 * joystick.getRawAxis(4));
        }
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

        drivetrain = new Drivetrain(frontLeftMotor, frontRightMotor, new AHRS(Port.kMXP));
    }
}
