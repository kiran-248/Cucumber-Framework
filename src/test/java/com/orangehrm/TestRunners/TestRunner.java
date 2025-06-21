package com.orangehrm.TestRunners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import io.qameta.allure.testng.AllureTestNg;
import org.testng.ITestContext;
import org.testng.annotations.*;

@Listeners({AllureTestNg.class})
@CucumberOptions(


        features = "src/test/Resources/Features/",
        glue = {"com.orangehrm.Steps", "com.orangehrm.hooks"},
        tags = "@login",
        dryRun = false,
        plugin = {
                "pretty", // Prints test steps in console
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"

        }


)

public class TestRunner extends AbstractTestNGCucumberTests {
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }

    @BeforeClass(alwaysRun = true)
    @Parameters({"browser", "delay"})
    public void setup(@Optional("chrome") String browserName, @Optional("0") String delay, ITestContext context) {
        try {
            int delayMs = Integer.parseInt(delay);
            if (delayMs > 0) {
                Thread.sleep(delayMs);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Use browserName from context only if provided via TestNG Runner
        if (browserName == null || browserName.isEmpty()) {
            Object browserFromContext = context.getAttribute("browser");
            if (browserFromContext != null) {
                browserName = browserFromContext.toString();
            } else {
                browserName = "chrome"; // Fallback default
            }
        }

        context.setAttribute("browser", browserName);
    }


}
