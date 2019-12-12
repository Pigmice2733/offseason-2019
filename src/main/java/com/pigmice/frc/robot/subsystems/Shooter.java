package com.pigmice.frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Shooter {
    TalonSRX motor;

    final double maxVoltage = 0.95;
    double currentVoltage = 0;

    double rampRate = (1.0 / (50 * 5));

    public Shooter(TalonSRX motor) {
        this.motor = motor;
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

        motor.set(ControlMode.PercentOutput, currentVoltage);
    }
}
