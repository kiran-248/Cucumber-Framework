package com.orangehrm.hooks;

import com.orangehrm.factory.DriverFactory;
import com.orangehrm.utils.ConfigReader;
import com.orangehrm.utils.ReportArchiver;
import com.orangehrm.utils.ScenarioContext;
import io.cucumber.java.*;
import io.qameta.allure.Allure;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.Reporter;

import java.awt.*;
import java.io.*;


public class Hooks {

      public static Logger logger;
    //private static final AtomicInteger threadCounter = new AtomicInteger(0);


    public Hooks() {

    }

    @Before
    public void setup(Scenario scenario) throws IOException, AWTException, InterruptedException {

        /*
        int threadNumber = threadCounter.incrementAndGet();

        if (threadNumber > 1) {
            // Delay only for 2nd thread (or beyond)
            Thread.sleep(20000); // or 10000 for 10 seconds
        } */


        String browser = null;

        // Try to get from TestNG context
        try {
            if (Reporter.getCurrentTestResult() != null &&
                    Reporter.getCurrentTestResult().getTestContext() != null) {
                browser = (String) Reporter.getCurrentTestResult().getTestContext().getAttribute("browser");
            }
        } catch (Exception e) {
            System.out.println("TestNG context not available. Falling back to system/config.");
        }

        // Try system property (set via -Dbrowser=firefox)
        if (browser == null || browser.isEmpty()) {
            browser = System.getProperty("browser");
        }

        // Fallback to config file
        if (browser == null || browser.isEmpty()) {
            browser = ConfigReader.getBrowser();
        }

        // Final fallback
        if (browser == null || browser.isEmpty()) {
            browser = "chrome";
        }

        DriverFactory.setBrowser(browser);
        WebDriver driver = DriverFactory.getDriver();

        logger = Logger.getLogger("Skeleton");
        PropertyConfigurator.configure(ConfigReader.getlogpath());

        driver.get(ConfigReader.getUrl());
        logger.info("Entered URL");

        final String finalBrowser = browser;

        Allure.getLifecycle().updateTestCase(testResult -> {
            testResult.setName(scenario.getName() + " [" + finalBrowser + "]");
        });
    }

    @After
    public void tearDown(Scenario scenario) throws IOException {
        WebDriver driver = DriverFactory.getDriver();
        if (scenario.isFailed()) {
            File sourcePath = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            byte[] fileContent = FileUtils.readFileToByteArray(sourcePath);
            scenario.attach(fileContent, "image/png", "image");
        }

        boolean scenarioFailed = scenario.isFailed();


        String safeScenarioName = scenario.getName().replaceAll("[^a-zA-Z0-9.-]", "_");
        DriverFactory.quitDriver();
        logger.info("Browser closed");
        ScenarioContext.reset();



    }

    @BeforeAll
    public static void cleanAllureResults() {
        ReportArchiver ReportArchiver = new ReportArchiver();
        ReportArchiver.archiveAllureResults();

        File resultsDir = new File("allure-results");
        if (resultsDir.exists()) {
            for (File file : resultsDir.listFiles()) {
                file.delete();
            }
            System.out.println(" Cleared Allure results before the test run.");
        }
    }




}