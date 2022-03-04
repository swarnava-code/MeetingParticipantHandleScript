package com.zopsmart.meet;

import com.zopsmart.meet.model.MeetSchedule;
import com.zopsmart.meet.utils.MyProperties;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MeetScript2 extends MeetBase {
    final int minimumParticipantToLeftTheCall = 2;
    int countCompletionOfTab = 0;

    @Test(priority = 1)
    void readPropertyFile() {
        String absPath = "/home/swarnava/Desktop/password/dataFile.properties";
        //String absPath = "src/test/files/dataFile.properties";
        MyProperties myProperties = new MyProperties(absPath);
        username = myProperties.getUsername();
        password = myProperties.getPassword();
    }

    @Test(priority = 1)
    public void readSheet() {
        int numberOfRow;
        MeetSchedule meetSchedule;
        SimpleDateFormat dateParser = new SimpleDateFormat("MMM d yyyy HH:mm:ss");
        try {
            FileInputStream fis = new FileInputStream(pathForSheet);
            Workbook workbook = WorkbookFactory.create(fis);
            numberOfRow = workbook.getSheet("Sheet1").getLastRowNum();
            String meetingDate;
            String meetingTime;
            for (int i = 1; i <= numberOfRow; i++) {
                meetingCode = workbook.getSheet("Sheet1").getRow(i).getCell(0).getStringCellValue();
                meetingDate = workbook.getSheet("Sheet1").getRow(i).getCell(1).getStringCellValue();
                meetingTime = workbook.getSheet("Sheet1").getRow(i).getCell(2).getStringCellValue();
                Date meetDateTime = null;
                try {
                    meetDateTime = dateParser.parse(meetingDate + " " + meetingTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                meetSchedule = new MeetSchedule();
                meetSchedule.setMeetingCode(meetingCode);
                meetSchedule.setMeetingTime(meetDateTime);
                meetingSchedule.add(meetSchedule);
            }
            workbook.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 2)
    void signIn() {
        driver.get("https://meet.google.com/");
        driver.findElement(By.cssSelector("a[event-action='sign in']")).click();
        driver.findElement(By.cssSelector("input[type='email']")).sendKeys(username);
        driver.findElement(By.xpath("//span[text()='Next']")).click();
        By passwordBy = By.cssSelector("input[type='password']");
        WebElement passwordInput = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(passwordBy)
        );
        passwordInput.sendKeys(password);
        driver.findElement(By.xpath("//span[text()='Next']")).click();
        try {
            By joinCode = By.cssSelector("input[type='text']");
            webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(joinCode));
        } catch (Exception e) {
        }
    }

    void openMeetingInTabsExceptOld() {
        Date currentTime = null;
        System.out.println("\n Total meetings : " + meetingSchedule.size());
        Collections.sort(meetingSchedule);
        try {
            for (MeetSchedule meetSchedule : meetingSchedule) {
                currentTime = new Date();
                if (meetSchedule.getMeetingTime().after(currentTime)) { //  if not old time then consider
                    meetingCode = meetSchedule.getMeetingCode();


                    driver.switchTo().newWindow(WindowType.TAB);
                    while (driver.getWindowHandle()==null){
                        Thread.sleep(5000);
                    }
                    driver.navigate().to("https://meet.google.com/" + meetingCode);


                    //webDriverWait.until(ExpectedConditions.urlContains("meet.google.com"));
                    meetSchedule.setWindowHandleCode(driver.getWindowHandle());
                } else { //  if old time then ignore
                    ++countCompletionOfTab;
                    meetSchedule.setMeetStatus(true);
                    meetSchedule.setOldAlreadyStatus(true);
                    System.out.println(meetSchedule.toString() + "\n too late!  ,  can't join in the past time\n");
                }
                System.out.println(meetSchedule.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 3)
    void startAndHandleAllMeetings() {
        String tempWindowHandle;
        Date currentTime;
        final int minimumPreJoinInMinute = 2;
        openMeetingInTabsExceptOld();
        while (countCompletionOfTab <= meetingSchedule.size()) {
            for (MeetSchedule meetSchedule : meetingSchedule) {
                if (meetSchedule.getMeetStatus() == false) {
                    if (meetSchedule.getRightTimeStatus()) {
                        String windowHandle = meetSchedule.getWindowHandleCode();
                        driver.switchTo().window(windowHandle);
                        if (meetSchedule.getJoinedAlreadyStatus()) {
                            if (meetSchedule.getRecordingStatus()) {
                                if (checkParticipant(meetSchedule)) {
                                    try {
                                        leaveTheMeetingCall();
                                        meetSchedule.setParticipantStatus(true);
                                        meetSchedule.setMeetStatus(true);
                                        System.out.println(countCompletionOfTab + " <-> " + meetingSchedule.size());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                startRec(meetSchedule);
                            }
                        } else {
                            joinTheCall(meetSchedule);
                        }
                    } else { //check right time or not
                        currentTime = new Date();
                        long differenceInMs = meetSchedule.getMeetingTime().getTime() - currentTime.getTime();
                        long differenceInMinutes = TimeUnit.MILLISECONDS.toMinutes(differenceInMs);
                        if (differenceInMinutes <= minimumPreJoinInMinute) { //isItRightTime
                            System.out.println("Need to Join for " + meetingSchedule.toString());
                            meetSchedule.setRightTimeStatus(true);
                            joinTheCall(meetSchedule);
                        }
                    }
                }
            }
        }
    }

    void leaveTheMeetingCall() {
        driver.findElement(By.cssSelector("div[jsaction='JIbuQc:LD0JHb']")).click(); //end call
    }

    void mainController() {
        int minimumParticipantToLeftTheCall = 2;
        applyAndJoin();
        dismissInsideCallPopup();
        //startRec();
        waitForParticipant(webDriverWait, minimumParticipantToLeftTheCall);
        driver.findElement(By.cssSelector("div[jsaction='JIbuQc:LD0JHb']")).click(); //end call
    }

    void joinNewLink() {
        try {
            driver.switchTo().newWindow(WindowType.TAB);
            Thread.sleep(2000);
            driver.get("https://meet.google.com/" + meetingCode);
        } catch (Exception e) {
            System.out.println("Attempt failed to join : " + meetingCode + ", info: " + e.toString());
            //driver.close();
            joinNewLink();
        }
    }

    void joinTheCall(MeetSchedule meetSchedule) {
        String windowHandleCode = meetSchedule.getWindowHandleCode();
        if (windowHandleCode == null) {
//            driver.switchTo().newWindow(WindowType.TAB);
//            driver.
//                    driver.getWindowHandle()
           // ff
        } else {
            driver.switchTo().window(windowHandleCode);
        }

        try {
            checkCamMicBlockPopupAvailability();
            WebElement joinNow =
                    utils.checkElementAvailability(driver, By.xpath("//span[text()='Join now']"), 10);
            if (joinNow != null) {
                joinNow.click();
            }
            meetSchedule.setJoinedAlreadyStatus(true);
            try {
                dismissInsideCallPopup();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void joinAndStartRecording() {
        checkCamMicBlockPopupAvailability();
        WebElement joinNow =
                utils.checkElementAvailability(driver, By.xpath("//span[text()='Join now']"), 10);
        if (joinNow != null) {
            joinNow.click();
        }
        dismissInsideCallPopup();
        //startRec();
        //checkParticipant();
    }

    boolean checkParticipant(MeetSchedule meetSchedule) {
        if (checkParticipants(webDriverWait) >= minimumParticipantToLeftTheCall) {
            meetSchedule.setParticipantStatus(true);
            meetSchedule.setMeetStatus(true);
            return true;
        }
        return false;
    }

    void applyAndJoin() {
        joinNewLink();
        checkCamMicBlockPopupAvailability();
        WebElement joinNow =
                utils.checkElementAvailability(driver, By.xpath("//span[text()='Join now']"), 10);
        if (joinNow != null) {
            joinNow.click();
        }
    }

    void dismissInsideCallPopup() {
        By dismissPopupInside = By.xpath("//span[text()='Got it']");
        WebElement dismissPopupInsideElement = null;
        try {
            dismissPopupInsideElement =
                    webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(dismissPopupInside));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dismissPopupInsideElement != null) {
            dismissPopupInsideElement.click();
            robo.click(KeyEvent.VK_TAB);
            robo.click(KeyEvent.VK_ENTER);
        }
    }

    void waitForParticipant(WebDriverWait webDriverWait, int minimumParticipantToLeftTheCall) {
        while (checkParticipants(webDriverWait) < minimumParticipantToLeftTheCall) {
            utils.waitForSomeTime(5);
        }
    }

    void checkCamMicBlockPopupAvailability() {
        int i = 0;
        By camMicBlockPopupBy = By.xpath("//div[@class='g3VIld vdySc Up8vH J9Nfi iWO5td']");
        WebElement camMicBlockPopupElement =
                myUtils.checkElementAvailability(driver, camMicBlockPopupBy, 5);
        if (camMicBlockPopupElement != null || camMicBlockPopupElement != null) {
            By camMicBlockPopupDismissBy = By.xpath("(//span[text()='Dismiss'])[2]");
            WebElement camMicBlockPopupDismiss =
                    myUtils.checkElementAvailability(driver, camMicBlockPopupDismissBy, 5);
            if (camMicBlockPopupDismiss != null) {
                camMicBlockPopupDismiss.click();
            }
        }
    }

    int checkParticipants(WebDriverWait webDriverWait) {
        int numberOfParticipants = 1;
        try {
            By noOfParticipant = By.xpath("//div[@class='uGOf1d']");
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

    void clickAttemptTwoTime(WebElement webElement) {
        try {
            webElement.click();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("element not clickable : " + webElement);
        }
        try {
            webElement.click();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void startRec(MeetSchedule meetSchedule) {
        WebDriverWait wait = new WebDriverWait(driver, 5);
        try {
            WebElement popupDismiss = wait.until(ExpectedConditions
                    .elementToBeClickable(By.xpath("(//span[text()='Dismiss'])[2]")));
            popupDismiss.click();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            WebElement clickOnActivities = wait.until(ExpectedConditions
                    .elementToBeClickable(By.cssSelector("button[aria-label='Activities']")));
            clickOnActivities.click();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            WebElement clickOnRecoding = webDriverWait.until(ExpectedConditions
                    .elementToBeClickable(By.xpath("//li/span/span[text()='Recording']")));
            clickOnRecoding.click();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            WebElement clickOnStartRecoding = webDriverWait.until(ExpectedConditions
                    .elementToBeClickable(By.xpath("//button/span[text()='Start recording']")));
            clickOnStartRecoding.click();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            WebElement clickOnStartPopupButton = webDriverWait.until(ExpectedConditions
                    .elementToBeClickable(By.xpath("//span[text()='Start']")));
            clickOnStartPopupButton.click();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            WebElement clickOnStopRecoding = null;
            clickOnStopRecoding =
                    webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//button/span[text()='Stop recording']")));
            meetSchedule.setRecordingStatus(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
