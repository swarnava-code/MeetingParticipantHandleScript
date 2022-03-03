package com.zopsmart.meet.pom;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SignInPage {
    WebDriver driver;
    By username = By.cssSelector("input[type='email']");
    By next = By.xpath("//span[text()='Next']");
    By password = By.name("password");

    public SignInPage(WebDriver driver) {
        this.driver = driver;
    }

    public void sendUsername(String usernameString) {
        driver.findElement(username).sendKeys(usernameString);
    }

    public void sendPassword(String passwordString) {
        driver.findElement(password).sendKeys(passwordString);
    }

    public void clickNext() {
        driver.findElement(next).click();
    }

    public By getUsername() {
        return username;
    }

    public By getNext() {
        return next;
    }

    public By getPassword() {
        return password;
    }

}
