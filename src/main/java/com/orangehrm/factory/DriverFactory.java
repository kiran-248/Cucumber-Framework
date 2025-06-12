package com.orangehrm.factory;

import com.orangehrm.utils.ConfigReader;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class DriverFactory {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static ThreadLocal<String> browser = new ThreadLocal<>();
    public static String executionMode = ConfigReader.getexecutionMode();
    public static void setBrowser(String browserName) {
        browser.set(browserName);
    }

    public static String getBrowser() {
        return browser.get();
    }

    public static WebDriver getDriver() {
        if (driver.get() == null) {
            executionMode = ConfigReader.getexecutionMode();

            switch (executionMode.toLowerCase()) {
                case "local":
                    driver.set(createLocalDriver(getBrowser()));
                    break;
                case "grid":
                    driver.set(createGridDriver(getBrowser()));
                    break;
                case "remote":
                    driver.set(createRemoteDriver(getBrowser()));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid execution mode: " + executionMode);
            }

            driver.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitCount()));
            driver.get().manage().window().maximize();
        }
        return driver.get();
    }

    public static void quitDriver() {
        WebDriver webDriver = driver.get();
        try {
            if (webDriver != null) {
                webDriver.quit();
            }
        } catch (Exception e) {
            System.err.println("Error while quitting the driver: " + e.getMessage());
        } finally {
            driver.remove();
            browser.remove();
        }
    }

    // ==================== Local Drivers ====================

    private static WebDriver createLocalDriver(String browser) {
        switch (browser.toLowerCase()) {
            case "chrome":
                return new ChromeDriver(getChromeOptions());
            case "firefox":
                try {
                    Thread.sleep(4000); // ⏱ Delay only for Firefox
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return new FirefoxDriver(getFirefoxOptions());
            case "edge":
                return new EdgeDriver(getEdgeOptions());
            case "safari":
                return new SafariDriver(getSafariOptions());
            default:
                throw new IllegalArgumentException("Invalid browser: " + browser);
        }
    }

    // ==================== Grid Drivers ====================

    private static WebDriver createGridDriver(String browser) {
        String gridUrl = ConfigReader.getGrid();

        try {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setPlatform(Platform.LINUX);

            switch (browser.toLowerCase()) {
                case "chrome":
                    capabilities.setBrowserName("chrome");
                    capabilities.setCapability(ChromeOptions.CAPABILITY, getChromeOptions());
                    break;
                case "firefox":
                    capabilities.setBrowserName("firefox");
                    capabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, getFirefoxOptions());
                    break;
                case "edge":
                    capabilities.setBrowserName("MicrosoftEdge");
                    capabilities.setCapability(EdgeOptions.CAPABILITY, getEdgeOptions());
                    break;
                case "safari":
                    capabilities.setBrowserName("safari");
                    capabilities.merge(getSafariOptions());
                    break;
                default:
                    throw new IllegalArgumentException("Invalid browser for Grid: " + browser);
            }

            return new RemoteWebDriver(new URL(gridUrl), capabilities);

        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to connect to Selenium Grid", e);
        }
    }

    // ==================== Remote Drivers (BrowserStack) ====================

    private static WebDriver createRemoteDriver(String browser) {
        String browserstackUrl = ConfigReader.getBsURL();

        try {
            DesiredCapabilities caps = new DesiredCapabilities();

            switch (browser.toLowerCase()) {
                case "chrome":
                    caps.setCapability("browserName", "Chrome");
                    caps.setCapability(ChromeOptions.CAPABILITY, getChromeOptions());

                    Map<String, Object> bsOptions = new HashMap<>();
                    bsOptions.put("os", "Windows");
                    bsOptions.put("osVersion", "11");
                    bsOptions.put("browserVersion", "latest");
                    bsOptions.put("resolution", "1920x1080");
                    bsOptions.put("seleniumVersion", "4.0.0");
                    bsOptions.put("sessionName", "Automation Test");

                    caps.setCapability("bstack:options", bsOptions);
                    break;

                case "firefox":
                    caps.setCapability("browserName", "Firefox");
                    caps.setCapability(FirefoxOptions.FIREFOX_OPTIONS, getFirefoxOptions());

                    Map<String, Object> firefoxOptions = new HashMap<>();
                    firefoxOptions.put("os", "Windows");
                    firefoxOptions.put("osVersion", "11");
                    firefoxOptions.put("browserVersion", "latest");
                    firefoxOptions.put("resolution", "1920x1080");
                    firefoxOptions.put("seleniumVersion", "4.0.0");
                    firefoxOptions.put("sessionName", "Automation Test");

                    caps.setCapability("bstack:options", firefoxOptions);
                    break;

                case "edge":
                    caps.setCapability("browser", "Edge");
                    caps.setCapability("browser_version", "latest");
                    caps.setCapability(EdgeOptions.CAPABILITY, getEdgeOptions());
                    break;

                case "safari":
                    caps.setCapability("browser", "Safari");
                    caps.setCapability("browser_version", "latest");
                    caps.merge(getSafariOptions());
                    break;

                default:
                    throw new IllegalArgumentException("Invalid browser for BrowserStack: " + browser);
            }

            return new RemoteWebDriver(new URL(browserstackUrl), caps);

        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to connect to BrowserStack", e);
        }
    }

    // ==================== Options for each browser ====================

    private static ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        if (ConfigReader.getHeadlessValue()) {
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        options.addArguments("--disable-notifications", "--incognito", "--start-maximized", "--disable-cache", "--disable-extensions");

        Map<String, Object> selenoidOptions = new HashMap<>();
        selenoidOptions.put("enableVNC", true);
        selenoidOptions.put("enableVideo", true);
        options.setCapability("selenoid:options", selenoidOptions);

        return options;
    }

    private static FirefoxOptions getFirefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        if (ConfigReader.getHeadlessValue()) {
            options.addArguments("-headless");
        }
        options.addArguments("-private");
        options.addPreference("dom.webnotifications.enabled", false);
        options.addPreference("browser.cache.disk.enable", false);
        return options;
    }

    private static EdgeOptions getEdgeOptions() {
        EdgeOptions options = new EdgeOptions();
        if (ConfigReader.getHeadlessValue()) {
            options.addArguments("-headless");
        }
        options.addArguments("--inPrivate", "--disable-notifications", "--start-maximized");
        return options;
    }

    private static SafariOptions getSafariOptions() {
        SafariOptions options = new SafariOptions();
        options.setCapability("cleanSession", true);
        options.setAutomaticInspection(false);
        options.setAutomaticProfiling(false);
        return options;
    }
}
