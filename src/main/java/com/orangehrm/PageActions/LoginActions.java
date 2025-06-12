package com.orangehrm.PageActions;

import com.orangehrm.PageObjects.LoginObjects;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginActions {
    public WebDriver driver;
    public LoginObjects loginObjects;


    public LoginActions(WebDriver driver) {
        this.driver = driver;
        this.loginObjects = new LoginObjects(driver);

    }

    public void enterUsername(String email) {
        WebElement element = driver.findElement(loginObjects.username);
        element.sendKeys(email);
    }

    public void enterPassword(String pwd) {
        WebElement element = driver.findElement(loginObjects.password);
        element.sendKeys(pwd);
    }

    public void clickLogin() throws InterruptedException {

        WebElement element = driver.findElement(loginObjects.loginBtn);
        element.click();
    }

    public String getDashboardText() throws InterruptedException {

        WebElement element = driver.findElement(loginObjects.dashboard);
        element.getText();
        return element.getText();
    }


}

