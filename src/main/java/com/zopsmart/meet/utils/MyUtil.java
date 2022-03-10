package com.zopsmart.meet.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class MyUtil {

    public WebElement checkElementAvailability(WebDriver driver, By by, int waitTimeInSecond) {
        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(waitTimeInSecond));
        WebElement webElement = null;
        try {
            webElement = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return webElement;
    }

    public ChromeOptions enableCameraAndMic(boolean enable) {
        int enableOrDisable = 2;
        if(enable){
            enableOrDisable = 1;
        }
        Map<String, Object> prefs = new HashMap<>();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--disable-notifications");
        prefs.put("profile.default_content_setting_values.media_stream_mic", enableOrDisable);
        prefs.put("profile.default_content_setting_values.media_stream_camera", enableOrDisable);
        chromeOptions.setExperimentalOption("prefs", prefs);
        return chromeOptions;
    }

}
