package com.pigmice.frc.robot.subsystems;

import com.revrobotics.CANSparkMax;

public class Drivetrain {
    private CANSparkMax leftDrive, rightDrive;

    public Drivetrain(CANSparkMax leftDrive, CANSparkMax rightDrive) {
        this.leftDrive = leftDrive;
        this.rightDrive = rightDrive;
    }

    public void tankDrive(double leftSpeed, double rightSpeed) {
        leftDrive.set(leftSpeed);
        rightDrive.set(-rightSpeed);
    }

    public void arcadeDrive(double forwardSpeed, double turnSpeed) {
        tankDrive(forwardSpeed + turnSpeed, forwardSpeed - turnSpeed);
    }
}
