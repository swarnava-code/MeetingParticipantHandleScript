package com.zopsmart.meet.pom;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage {
    WebDriver driver;
    By signIn = By.xpath("//a[contains(text(),'Sign in')]");
    By meetCodeInputField = By.id("i3");
    By join = By.cssSelector("#yDmH0d > c-wiz > div > div.S3RDod > div " +
            "> div.Qcuypc > div.Ez8Iud > div > div.KOM0mb > " +
            "div.VfPpkd-dgl2Hf-ppHlrf-sM5MNb > button > span");

    public HomePage(WebDriver driver){
        this.driver = driver;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public By getSignIn() {
        return signIn;
    }

    public By getJoin() {
        return join;
    }

    public void sendMeetCodeInputField(String code){
        driver.findElement(meetCodeInputField).sendKeys(code);
    }

}
