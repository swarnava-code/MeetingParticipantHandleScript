package com.zopsmart.meet.pom;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MeetingPage {
    WebDriver driver;
    By callEnd = By.xpath("//button/i[text()='call_end']");
    By showEveryOneStatus = By.xpath("//div[@class='uGOf1d']");
    By startRecording = By.xpath("//button/span[text()='Start recording']");
    By recording = By.xpath("//li/span/span[text()='Recording']");
    By activities = By.cssSelector("button[aria-label='Activities']");

    public MeetingPage(WebDriver driver) {
        this.driver = driver;
    }

    public void clickCallEnd() {
        driver.findElement(callEnd).click();
    }

    public By getShowEveryOneStatus() {
        return showEveryOneStatus;
    }

    public By getStartRecording() {
        return startRecording;
    }

    public By getRecording() {
        return recording;
    }

    public By getActivities() {
        return activities;
    }

}
