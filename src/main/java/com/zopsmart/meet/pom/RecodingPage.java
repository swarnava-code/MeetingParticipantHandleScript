package com.zopsmart.meet.pom;

import org.openqa.selenium.By;

public class RecodingPage {
    private By noOfParticipant = By.xpath("//div[@class='uGOf1d']");
    private By leaveButton = By.cssSelector("div[jsaction='JIbuQc:LD0JHb']");
    private By moreOptions = By.xpath("(//div[@jscontroller='wg1P6b'])[2]");
    private By recordMeeting = By.xpath("//ul/li[@role='menuitem']/span[text()='Record meeting'][@jsname='K4r5Ff']");
    private By startRec = By.xpath("//div[@class='WUFI9b hJHCb']/div/div/div/div/div/p/div/div/button[@aria-label='Start recording']");
    private By startPopup = By.cssSelector("button[data-mdc-dialog-action='ok']");
    private By recordingIndicator = By.cssSelector("div[class='KHSqkf']"); //By.xpath("//i[text()='radio_button_checked']");
    private By dismissPopup = By.xpath("(//span[text()='Dismiss'])[2]");
    private By gotItButton = By.xpath("//span[text()='Got it']");

    public By getNoOfParticipant() {
        return noOfParticipant;
    }

    public By getLeaveButton() {
        return leaveButton;
    }

    public By getMoreOptions() {
        return moreOptions;
    }

    public By getRecordMeeting() {
        return recordMeeting;
    }

    public By getStartRec() {
        return startRec;
    }

    public By getStartPopup() {
        return startPopup;
    }

    public By getRecordingIndicator() {
        return recordingIndicator;
    }

    public By getDismissPopup() {
        return dismissPopup;
    }

    public By getGotItButton() {
        return gotItButton;
    }

}
