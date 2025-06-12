package com.orangehrm.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ScreenshotUtils
{
    public static void captureFullScreen(String testName) {
        try {
            // Create a Robot instance to capture the entire screen
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenFullImage = robot.createScreenCapture(screenRect);

            // Define the screenshot save location
            String screenshotPath = "target/" + testName + "_full_failure.png";
            ImageIO.write(screenFullImage, "PNG", new File(screenshotPath));

            System.out.println("Full-screen screenshot saved: " + screenshotPath);
        } catch (Exception e) {
            System.out.println("Error capturing full-screen screenshot: " + e.getMessage());
        }
    }
}
