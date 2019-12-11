package com.pigmice.frc.robot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.pigmice.frc.lib.logging.Logger;

import edu.wpi.first.wpilibj.SerialPort;

public class Vision {
    private static SerialPort port;

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new ThreadFactory() {
        public Thread newThread(Runnable r) {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        }
    });

    private static ScheduledFuture<?> self;
    private static boolean enabled;

    // Roll-over input from camera since messages don't always line up with
    private static String remainingInput = "";
    private static volatile double targetAngle = 0.0;
    private static volatile boolean targetVisible = false;

    private static Logger.ComponentLogger logger = Logger.createComponent(Vision.class);

    public static void start() {
        if (!enabled) {
            enabled = true;
            initPort();
            self = scheduler.scheduleAtFixedRate(Vision::update, 1000, 20, TimeUnit.MILLISECONDS);
        }
    }

    public static void stop() {
        enabled = false;
        self.cancel(true);
        self = null;
        port = null;
        targetVisible = false;
        targetAngle = 0.0;
    }

    public static double getOffset() {
        return targetAngle;
    }

    public static boolean targetVisible() {
        return targetVisible;
    }

    private static void update() {
        try {
            parseInput(remainingInput + port.readString());
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private static void parseInput(String input) {
        int startIndex = input.lastIndexOf("START");
        int separatorIndex = input.lastIndexOf(",");
        int endIndex = input.lastIndexOf("END");

        if (endIndex > startIndex && startIndex > -1) {
            String centerString = input.substring(startIndex + 6, separatorIndex);
            if (centerString.length() == 4 && centerString.toLowerCase().equals("none")) {
                Vision.targetVisible = false;
                Vision.targetAngle = 0.0;
            } else {
                double center = Double.valueOf(centerString);
                double width = Double.valueOf(input.substring(separatorIndex + 2, endIndex));

                // Compensate for camera offset and convert to angle
                targetAngle = (center - width) / width;
                targetVisible = true;
            }

            remainingInput = input.substring(endIndex + 3);
        } else {
            remainingInput = input;
        }
    }

    private static void initPort() {
        try {
            port = new SerialPort(57600, SerialPort.Port.kMXP);
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }
}
