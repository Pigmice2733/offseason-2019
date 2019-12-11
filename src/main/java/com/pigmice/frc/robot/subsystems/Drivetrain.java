package com.pigmice.frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;

public class Drivetrain {
    private CANSparkMax leftDrive, rightDrive;
    private CANEncoder left, right;

    private AHRS navx;

    public Drivetrain(CANSparkMax leftDrive, CANSparkMax rightDrive, AHRS navx) {
        this.leftDrive = leftDrive;
        this.rightDrive = rightDrive;
        this.navx = navx;

        left = leftDrive.getEncoder();
        right = rightDrive.getEncoder();
    }

    public void tankDrive(double leftSpeed, double rightSpeed) {
        leftDrive.set(leftSpeed);
        rightDrive.set(-rightSpeed);
    }

    public void arcadeDrive(double forwardSpeed, double turnSpeed) {
        tankDrive(forwardSpeed + turnSpeed, forwardSpeed - turnSpeed);
    }

    public void stop() {
        tankDrive(0.0, 0.0);
    }

    public void resetSensors() {
        left.setPosition(0.0);
        right.setPosition(0.0);

        navx.setAngleAdjustment(navx.getAngleAdjustment() - navx.getAngle());
    }

    private double rotationAverage() {
        return (left.getPosition() + -right.getPosition())/2;
    }

    public double positionAverage() {
        return (rotationAverage() / 6.68) * 0.3192;
    }
}
