package com.zopsmart.meet.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BaseUtility {

    public WebDriver initDriver(WebDriver driver){
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = new MyUtils().disableNotification();
        driver = new ChromeDriver(chromeOptions);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().scriptTimeout(Duration.ofMinutes(2));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(100));
        return driver;
    }

    public WebDriverWait initWebDriverWait(WebDriver driver, WebDriverWait webDriverWait){
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        return webDriverWait;
    }

}
