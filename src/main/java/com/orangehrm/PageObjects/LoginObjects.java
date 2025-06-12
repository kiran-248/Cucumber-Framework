package com.orangehrm.PageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class LoginObjects {

    WebDriver driver;
    public LoginObjects(WebDriver driver)

    {
        this.driver = driver;
        PageFactory.initElements(driver,this);

    }


    public By username = By.name("username");
    public By password = By.name("password");
    public By loginBtn = By.cssSelector("button[type='submit']");
    public By dashboard = By.xpath("//h6[normalize-space()='Dashboard']");

}
