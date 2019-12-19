package com.pigmice.frc.robot;

import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode.PixelFormat;
import edu.wpi.first.wpilibj.SerialPort;

public class Vision {
    private static class SerialConfiguration {
        private static final int BAUD_RATE = 912600;
        private static final char TERMINATOR = '\n';
        private static final SerialPort.Port PORT = SerialPort.Port.kMXP;

        private static final String START_DELIMITER = "[";
        private static final String END_DELIMITER = "]";
        private static final String DATA_SEPARATOR = ",";
        private static final String NULL_RESPONSE = "NONE";
    }

    private static boolean enabled = false;

    private static SerialPort port = null;
    private static Thread visionThread = new Thread(() -> {
        while (!Thread.interrupted()) {
            update();
        }
    });

    private static volatile double targetAngle = 0.0;
    private static volatile boolean targetVisible = false;

    private static StringBuilder readBuffer = new StringBuilder(24);

    private static final int MJPEG_PORT = 1181;
    private static UsbCamera camera = null;
    private static MjpegServer server = null;

    public static void startProcessing() {
        if (port == null) {
            initPort();
        }

        if (!enabled) {
            enabled = true;

            visionThread.setDaemon(true);
            visionThread.start();
        }
    }

    public static void startStreaming() {
        if(camera == null) {
            camera = new UsbCamera("JeVois", 0);
            camera.setVideoMode(PixelFormat.kMJPEG, 320, 160, 15);
            server = new MjpegServer("Camera Server", MJPEG_PORT);
            server.setSource(camera);
        }
    }

    public static double getOffset() {
        return targetAngle;
    }

    public static boolean targetVisible() {
        return targetVisible;
    }

    private static void update() {
        readBuffer.append(port.readString());

        boolean success = false;
        try {
            while (!success) {
                success = parseInput();
                Thread.sleep(5);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static boolean parseInput() {
        int startIndex = readBuffer.lastIndexOf(SerialConfiguration.START_DELIMITER);
        int separatorIndex = readBuffer.lastIndexOf(SerialConfiguration.DATA_SEPARATOR);
        int endIndex = readBuffer.lastIndexOf(SerialConfiguration.END_DELIMITER);

        if (endIndex > startIndex && startIndex > -1) {
            String centerString = readBuffer.substring(startIndex + 1, separatorIndex);
            if (centerString.length() == 4 && centerString.equals(SerialConfiguration.NULL_RESPONSE)) {
                Vision.targetVisible = false;
                Vision.targetAngle = 0.0;
            } else {
                double center = Double.valueOf(centerString);
                double width = Double.valueOf(readBuffer.substring(separatorIndex + 1, endIndex));

                targetAngle = (center - width) / width;
                targetVisible = true;
            }

            readBuffer.delete(0, endIndex + 1);
            return true;
        }

        return false;
    }

    private static void initPort() {
        try {
            port = new SerialPort(SerialConfiguration.BAUD_RATE, SerialConfiguration.PORT);

            port.enableTermination(SerialConfiguration.TERMINATOR);
        } catch (Exception e) {
            System.out.println("Failed to initialze vision");
        }
    }
}
