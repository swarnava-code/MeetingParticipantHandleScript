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

    By moreOptions = By.xpath("(//div[@jscontroller='wg1P6b'])[2]");
    By recordMeeting = By.xpath("(//li[@role='menuitem'])[2]");
    By startRec = By.xpath("(//button[@class='VfPpkd-LgbsSe VfPpkd-LgbsSe-OWXEXe-k8QpJ VfPpkd-LgbsSe-OWXEXe-dgl2Hf Kjnxrf C1Uh5b DuMIQc qfvgSe pKAeHb'])[2]");
    By startPopup = By.cssSelector("button[data-mdc-dialog-action='ok']");
    By recordingIndicator = By.cssSelector("div[class='KHSqkf']");
    By dismissPopup = By.xpath("(//span[text()='Dismiss'])[2]");

    By participant = By.xpath("//div[@class='uGOf1d']");

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public By getCallEnd() {
        return callEnd;
    }

    public void setCallEnd(By callEnd) {
        this.callEnd = callEnd;
    }

    public void setShowEveryOneStatus(By showEveryOneStatus) {
        this.showEveryOneStatus = showEveryOneStatus;
    }

    public void setStartRecording(By startRecording) {
        this.startRecording = startRecording;
    }

    public void setRecording(By recording) {
        this.recording = recording;
    }

    public void setActivities(By activities) {
        this.activities = activities;
    }

    public By getMoreOptions() {
        return moreOptions;
    }

    public void setMoreOptions(By moreOptions) {
        this.moreOptions = moreOptions;
    }

    public By getRecordMeeting() {
        return recordMeeting;
    }

    public void setRecordMeeting(By recordMeeting) {
        this.recordMeeting = recordMeeting;
    }

    public By getStartRec() {
        return startRec;
    }

    public void setStartRec(By startRec) {
        this.startRec = startRec;
    }

    public By getStartPopup() {
        return startPopup;
    }

    public void setStartPopup(By startPopup) {
        this.startPopup = startPopup;
    }

    public By getRecordingIndicator() {
        return recordingIndicator;
    }

    public void setRecordingIndicator(By recordingIndicator) {
        this.recordingIndicator = recordingIndicator;
    }

    public By getDismissPopup() {
        return dismissPopup;
    }

    public void setDismissPopup(By dismissPopup) {
        this.dismissPopup = dismissPopup;
    }

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
