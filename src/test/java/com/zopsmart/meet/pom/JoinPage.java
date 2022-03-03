package com.zopsmart.meet.pom;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class JoinPage {
    WebDriver driver;
    By joinNow = By.xpath("//span[text()='Join now']");

    public JoinPage(WebDriver driver) {
        this.driver = driver;
    }

    public By getJoinNow() {
        return joinNow;
    }

}
