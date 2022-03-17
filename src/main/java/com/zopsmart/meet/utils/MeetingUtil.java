package com.zopsmart.meet.utils;

import com.zopsmart.meet.model.DbConfig;
import com.zopsmart.meet.model.MeetSchedule;
import com.zopsmart.meet.model.MyMessage;
import com.zopsmart.meet.pom.JoinNowPage;
import com.zopsmart.meet.pom.RecodingPage;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MeetingUtil {
    MyUtil utils = new MyUtil();
    DataBaseUtil dataBaseUtil;
    MyMessage myMessage = new MyMessage();
    final int minimumParticipantToLeftTheCall = 2;
    int countCompletionOfTab = 0;
    String meetingCode;
    int waitSwitchTabInMs = 500;
    final int minimumPreJoinInMinute = 2;

    public void startAndHandleAllMeetings(WebDriver driver, List<MeetSchedule> meetingSchedule, DataBaseUtil dataBaseUtil) {
        this.dataBaseUtil = dataBaseUtil;
        Date currentTime;
        Collections.sort(meetingSchedule);
        validateExpireTime(meetingSchedule);
        try {
            while (countCompletionOfTab != meetingSchedule.size()) {
                for (MeetSchedule meetSchedule : meetingSchedule) {
                    if (meetSchedule.getMeetStatus().equals(false)) {
                        if (meetSchedule.getRightTimeStatus()) {
                            String windowHandle = meetSchedule.getWindowHandleCode();
                            driver.switchTo().window(windowHandle);
                            if (meetSchedule.getJoinedAlreadyStatus()) {
                                if (meetSchedule.getRecordingStatus()) {
                                    Thread.sleep(waitSwitchTabInMs);
                                    if (checkParticipant(driver, meetSchedule)) {
                                        try {
                                            leaveTheMeetingCall(driver);
                                            meetSchedule.setMeetDbStatus(myMessage.recCompleted);
                                            dataBaseUtil.updateDbStatus(meetSchedule);
                                            meetSchedule.setParticipantStatus(true);
                                            meetSchedule.setMeetStatus(true);
                                            driver.close();
                                            System.out.println(countCompletionOfTab + " <-> " + meetingSchedule.size());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    meetSchedule.setRetry(0);
                                    startRec(driver, meetSchedule);
                                }
                            } else {
                                joinTheCall(driver, meetSchedule);
                            }
                        } else { //check right time or not
                            currentTime = new Date();
                            long differenceInMs = meetSchedule.getMeetingStartTime().getTime() - currentTime.getTime();
                            long differenceInMinutes = TimeUnit.MILLISECONDS.toMinutes(differenceInMs);
                            if (differenceInMinutes <= minimumPreJoinInMinute) { //isItRightTime
                                meetSchedule.setRightTimeStatus(true);
                                joinTheCall(driver, meetSchedule);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            takeScreenshot(driver);
        }
    }

    public void validateExpireTime(List<MeetSchedule> meetingSchedule) {
        Date currentTime;
        System.out.println("\n Total meetings : " + meetingSchedule.size());
        try {
            for (MeetSchedule meetSchedule : meetingSchedule) {
                currentTime = new Date();
                if (!meetSchedule.getMeetingStartTime().after(currentTime)) {
                    //  if meeting time expire, then ignore
                    ++countCompletionOfTab;
                    meetSchedule.setMeetStatus(true);
                    meetSchedule.setOldAlreadyStatus(true);
                    System.out.println(meetSchedule.getMeetingCode() + " - " + meetSchedule.getMeetingStartTime() + " (meet time expired already)");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean checkParticipant(WebDriver driver, MeetSchedule meetSchedule) {
        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        if (checkParticipants(webDriverWait) >= minimumParticipantToLeftTheCall) {
            meetSchedule.setParticipantStatus(true);
            meetSchedule.setMeetStatus(true);
            countCompletionOfTab++;
            return true;
        }
        return false;
    }

    int checkParticipants(WebDriverWait webDriverWait) {
        int numberOfParticipants = 1;
        try {
            RecodingPage recodingPage = new RecodingPage();
            By noOfParticipant = recodingPage.getNoOfParticipant();
            WebElement participants = webDriverWait.until(
                    ExpectedConditions.visibilityOfElementLocated(noOfParticipant)
            );
            System.out.println("participants : " + participants.getText());
            numberOfParticipants = Integer.parseInt(participants.getText());
        } catch (Exception e) {
            e.printStackTrace();
            return numberOfParticipants;
        }
        return numberOfParticipants;
    }

    void leaveTheMeetingCall(WebDriver driver) {
        RecodingPage recodingPage = new RecodingPage();
        driver.findElement(recodingPage.getLeaveButton()).click(); //end call
    }

    void startRec(WebDriver driver, MeetSchedule meetSchedule) {
        meetSchedule.setMeetDbStatus(myMessage.startRecording);
        dataBaseUtil.updateDbStatus(meetSchedule);
        RecodingPage recodingPage = new RecodingPage();
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
            Thread.sleep(1000);
            driver.findElement(recodingPage.getDismissPopup()).click();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(25));
        } catch (Exception e) {
            meetSchedule.setMeetDbStatus(myMessage.recFailed);
            dataBaseUtil.updateDbStatus(meetSchedule);
            e.printStackTrace();
        }

        try {
            driver.findElement(recodingPage.getMoreOptions()).click();
            driver.findElement(recodingPage.getRecordMeeting()).click();
            driver.findElement(recodingPage.getStartRec()).click();
            driver.findElement(recodingPage.getStartPopup()).click();
            Thread.sleep(1000);
            if (driver.findElement(recodingPage.getRecordingIndicator()).isDisplayed()) {
                meetSchedule.setRecordingStatus(true);
                meetSchedule.setMeetDbStatus(myMessage.recInProgress);
                dataBaseUtil.updateDbStatus(meetSchedule);
            }
        } catch (Exception e) {
            meetSchedule.setMeetDbStatus(myMessage.recFailed);
            dataBaseUtil.updateDbStatus(meetSchedule);
            takeScreenshot(driver);
        }
    }

    void takeScreenshot(WebDriver driver) {
        try {
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File file = takesScreenshot.getScreenshotAs(OutputType.FILE);
            File fileWithPath = new File("screenshots/ss_"
                    + (new Date()).toString().replaceAll(" ", "_") + ".jpg");
            try {
                FileUtils.copyFile(file, fileWithPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void joinTheCall(WebDriver driver, MeetSchedule meetSchedule) {
        meetSchedule.setMeetDbStatus(myMessage.newQueue);
        dataBaseUtil.updateDbStatus(meetSchedule);
        //open new tab, if not open already
        if (meetSchedule.getWindowHandleCode() == null) {
            meetingCode = meetSchedule.getMeetingCode();
            driver.switchTo().newWindow(WindowType.TAB);
            //wait for new tab
            while (driver.getWindowHandle() == null) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            driver.navigate().to("https://meet.google.com/" + meetingCode);
            meetSchedule.setWindowHandleCode(driver.getWindowHandle());
        }

        // join
        String windowHandleCode = meetSchedule.getWindowHandleCode();
        if (windowHandleCode != null) {
            driver.switchTo().window(windowHandleCode);
        }
        try {
            checkCamMicBlockPopupAvailability(driver);
            JoinNowPage joinNowPage = new JoinNowPage();
            WebElement joinNow =
                    utils.checkElementAvailability(driver, joinNowPage.getJoinNowButton(), 10);
            if (joinNow != null) {
                joinNow.click();
                meetSchedule.setJoinedAlreadyStatus(true);
                try {
                    dismissInsideCallPopup(driver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // retry
                if (meetSchedule.getRetry() < 3) {
                    meetSchedule.setRetry(meetSchedule.getRetry() + 1);
                    driver.navigate().refresh();
                } else {
                    meetSchedule.setMeetStatus(true);
                    takeScreenshot(driver);
                    driver.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void checkCamMicBlockPopupAvailability(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        JoinNowPage joinNowPage = new JoinNowPage();
        By camMicBlockPopupBy = joinNowPage.getCamMicBlockPopupBy();
        WebElement camMicBlockPopupElement =
                utils.checkElementAvailability(driver, camMicBlockPopupBy, 5);
        if (camMicBlockPopupElement != null) {
            WebElement camMicBlockPopupDismiss =
                    utils.checkElementAvailability(driver, joinNowPage.getCamMicBlockPopupDismissBy(), 5);
            if (camMicBlockPopupDismiss != null) {
                camMicBlockPopupDismiss.click();
            }
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(25));
    }

    void dismissInsideCallPopup(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        RecodingPage recodingPage = new RecodingPage();
        WebElement dismissPopupInsideElement = null;
        try {
            WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            dismissPopupInsideElement =
                    webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(recodingPage.getGotItButton()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dismissPopupInsideElement != null) {
            dismissPopupInsideElement.click();
            Robot robot = new Robot();
            robot.click(KeyEvent.VK_TAB);
            robot.click(KeyEvent.VK_ENTER);
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(25));
    }
}
