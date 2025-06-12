package com.orangehrm.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;

    // Static block to load properties when class is loaded
    static {
        try {
            FileInputStream file = new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/config.properties");

            properties = new Properties();
            properties.load(file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load config.properties");
        }
    }




    public static String getUrl() {
        return properties.getProperty("URL");
    } //Method to get the URL from the properties file

    public static String getCust()
    {
        return properties.getProperty("cust.name");
    }

    public static String getFA()
    {
        return properties.getProperty("Fa.emp");
    }

    public static String getFo()
    {
        return properties.getProperty("Fo.emp");
    }
    public static String getCA()
    {
        return properties.getProperty("Ca.emp");
    }
    public static String getlogpath()
    {
        return properties.getProperty("log_path");
    }

    public static String getBrowser()
    {
        return properties.getProperty("browser", "chrome");
    }

    public  static boolean getHeadlessValue()
    {
        return Boolean.parseBoolean(properties.getProperty("headless"));
    }

    public static String getexecutionMode()
    {
        return properties.getProperty("executionMode");
    }

    public static String getGrid()
    {
        return properties.getProperty("gridUrl");
    }
    public static String getBsURL()
    {
        return properties.getProperty("bsUrl");
    }
    public static String getBsUsername()
    {
        return properties.getProperty("bsUsername");
    }

    public static String getBsAccesskey()
    {
        return properties.getProperty("bsAccessKey");
    }
    public static String getemail()
    {
        return properties.getProperty("email");
    }
    public static String getPassword()
    {
        return properties.getProperty("password");
    }

    public static int getRetryCount()
    {
        return Integer.parseInt(properties.getProperty("retrycount"));
    }

    public static int getImplicitCount()
    {
        return Integer.parseInt(properties.getProperty("implicit"));
    }

    public static int getExplicitCount()
    {
        return Integer.parseInt(properties.getProperty("explicit"));
    }
    public static int getFluentCount()
    {
        return Integer.parseInt(properties.getProperty("fluent"));
    }

    public static int getPollCount()
    {
        return Integer.parseInt(properties.getProperty("poll"));
    }

    public static String getViewonly()
    {
        return properties.getProperty("viewonly");
    }
    public static String getAnalyst()
    {
        return properties.getProperty("analyst");
    }
    public static String getSuperAdmin()
    {
        return properties.getProperty("superadmin");
    }
    public static String getAdmin()
    {
        return properties.getProperty("admin");
    }




}