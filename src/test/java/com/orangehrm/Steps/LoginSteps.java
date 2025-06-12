package com.orangehrm.Steps;

import com.orangehrm.PageActions.LoginActions;
import com.orangehrm.factory.DriverFactory;
import com.orangehrm.hooks.Hooks;
import com.orangehrm.utils.ConfigReader;
import io.cucumber.java.en.*;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class LoginSteps {

    WebDriver driver = DriverFactory.getDriver();
    private static Logger logger = Hooks.logger;
    public LoginActions lp;


    public LoginSteps() {
        lp = new LoginActions(driver);
    }


    @Given("User is on the login page")
    public void user_is_on_the_login_page() {

        logger.info("Entered URL");


    }

    String username = "";

    @When("User enters valid {string} and {string}    And User clicks on the login button")
    public void user_enters_valid_and_and_user_clicks_on_the_login_button(String string, String string2) throws InterruptedException {
        username = string;
        System.out.println("Email ids " + username);
        lp.enterUsername(username);
        logger.info("Entered Username");
        lp.enterPassword(string2);
        logger.info("Entered Password");
        Thread.sleep(3000);
        lp.clickLogin();
        logger.info("Clicked on the Login Button");
        Thread.sleep(3000);
    }

    @Then("User should be navigated to the home page successfully")
    public void user_should_be_navigated_to_the_home_page_successfully() throws InterruptedException {
        String actualResult = lp.getDashboardText();
        String expectedResult = "Dashboard";
        Thread.sleep(3000);

        if (expectedResult.equals(actualResult)) {
            logger.info("Titles Matched");
        } else {
            logger.info("Titles Not Matched");
        }
    }

}
