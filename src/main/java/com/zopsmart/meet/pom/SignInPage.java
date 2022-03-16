package com.zopsmart.meet.pom;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SignInPage {
    By signInButton = By.cssSelector("a[event-action='sign in']");
    By emailText = By.cssSelector("input[type='email']");
    By emailNextButton = By.xpath("//span[text()='Next']");
    By passwordText = By.cssSelector("input[type='password']");
    By passwordNextButton = By.xpath("//span[text()='Next']");

    public void signIn(WebDriver driver, String userName, String password, String url) {
        driver.navigate().to(url);
        //driver.get("https://meet.google.com/");
        driver.findElement(signInButton).click();
        driver.findElement(emailText).sendKeys(userName);
        driver.findElement(emailNextButton).click();

        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement passwordInput = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(passwordText)
        );
        passwordInput.sendKeys(password);
        driver.findElement(passwordNextButton).click();
        //  log.info("login successfully");
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        try {
            webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a[class='gb_A gb_Ka gb_f']")));
        } catch (Exception e) {
            System.out.println("element not found");
        }
    }

}