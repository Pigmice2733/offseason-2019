package com.pigmice.frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;


public class Shooter {
    private static final double feedRate = -0.3;
    private static final double holdRate = 0.1;
    private static final double feederActivation = 0.8;
    TalonSRX flywheelMotor;
    VictorSPX feederMotor;

    final double maxVoltage = 0.95;
    double currentVoltage = 0;

    double rampRate = (1.0 / (50 * 5));

    public Shooter(TalonSRX flywheelMotor, VictorSPX feederMotor) {
        this.flywheelMotor = flywheelMotor;
        this.feederMotor = feederMotor;
    }

    public void go(double speed) {
        output(Math.min(speed, maxVoltage));
    }

    public void stop() {
        output(0);
    }

    private void output(double target) {
        if (target > currentVoltage) {
            currentVoltage += rampRate;
        } else if (target < currentVoltage) {
            currentVoltage -= rampRate;
        }

        if (currentVoltage > feederActivation) {
            feederMotor.set(ControlMode.PercentOutput, feedRate);
        } else {
            feederMotor.set(ControlMode.PercentOutput, holdRate);
        }

        flywheelMotor.set(ControlMode.PercentOutput, currentVoltage);
    }
}
