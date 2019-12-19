/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.pigmice.frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.kauailabs.navx.frc.AHRS;
import com.pigmice.frc.lib.utils.Utils;
import com.pigmice.frc.robot.subsystems.Drivetrain;
import com.pigmice.frc.robot.subsystems.Shooter;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

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
    Shooter shooter;

    @Override
    public void robotInit() {
        inititalizeDrivetrain(3, 4, 1, 2);
        initializeShooter();

        Vision.startStreaming();
        Vision.startProcessing();
    }

    @Override
    public void autonomousInit() {
        drivetrain.resetSensors();
    }

    @Override
    public void autonomousPeriodic() {
        System.out.println(Vision.targetVisible());
    }

    @Override
    public void teleopInit() {
        drivetrain.stop();
    }

    @Override
    public void teleopPeriodic() {
        if(joystick.getRawAxis(3) > 0.2) {
            shooter.go(joystick.getRawAxis(3));
        } else {
            shooter.stop();
        }

        double forwardSpeed = -joystick.getRawAxis(1);
        double turnSpeed = joystick.getRawAxis(4);

        if(forwardSpeed <= 0.1 && forwardSpeed >= -0.1) {
            forwardSpeed = 0;
        } else {
            forwardSpeed = Utils.lerp(Math.abs(forwardSpeed), 0.1, 1, 0, 1) * Math.signum(forwardSpeed);
        }

        if (turnSpeed <= 0.1 && turnSpeed >= -0.1) {
            turnSpeed = 0;
        } else {
            turnSpeed = Utils.lerp(Math.abs(turnSpeed), 0.1, 1, 0, 1) * Math.signum(turnSpeed);
        }

        if(joystick.getRawButton(5)) {
            drivetrain.arcadeDrive(forwardSpeed, turnSpeed);
        } else {
            drivetrain.arcadeDrive(0.1 * forwardSpeed, 0.1 * turnSpeed);
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

    private void initializeShooter() {
        TalonSRX leftShooter = new TalonSRX(5);
        TalonSRX rightShooter = new TalonSRX(6);
        VictorSPX feeder = new VictorSPX(7);

        rightShooter.follow(leftShooter);

        shooter = new Shooter(leftShooter, feeder);
    }
}
