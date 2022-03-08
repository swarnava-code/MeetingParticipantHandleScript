package com.zopsmart.meet;

import com.zopsmart.meet.model.MeetSchedule;
import com.zopsmart.meet.utils.MyProperties;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MeetScript extends MeetBase {
    Logger log = LogManager.getLogger(MeetScript.class);
    final int minimumParticipantToLeftTheCall = 2;
    int countCompletionOfTab = 0;

    @Test(priority = 1)
    void readPropertyFile() {
        MyProperties myProperties = new MyProperties(pathForPropertyFile);
        username = myProperties.getUsername();
        password = myProperties.getPassword();
        dbName = myProperties.getDbName();
        url = myProperties.getUrl();
        tableName = myProperties.getTableName();
        dbUserName = myProperties.getDbUsername();
        dbPassword = myProperties.getDbPassword();
    }

    @Test(priority = 2)
    public void setDbConnection() {
        try {
            connection = DriverManager.getConnection(url, dbUserName, dbPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 3)
    public void retrieveDataFromDb() {
        String query = "SELECT * from " + tableName + ";";
        log.info("fetching data from db : "+tableName);
        MeetSchedule meetSchedule;
        String meetingCode;
        String meetingDate;
        String meetingTime;
        SimpleDateFormat dateParser = new SimpleDateFormat("MMM d yyyy HH:mm:ss");
        Date meetDateTime = null;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            System.out.println("start while");
            while (resultSet.next()) {
                meetingCode = resultSet.getString(COL_MEET_CODE);
                meetingDate = resultSet.getString(COL_MEET_DATE);
                meetingTime = resultSet.getString(COL_MEET_TIME);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 4)
    void signIn() {
        log.info("sign in...");
        driver.navigate().to("https://meet.google.com/");
        //driver.get("https://meet.google.com/");
        driver.findElement(By.cssSelector("a[event-action='sign in']")).click();
        driver.findElement(By.cssSelector("input[type='email']")).sendKeys(username);
        driver.findElement(By.xpath("//span[text()='Next']")).click();
        By passwordBy = By.cssSelector("input[type='password']");
        WebElement passwordInput = webDriverWait.until(
                ExpectedConditions.visibilityOfElementLocated(passwordBy)
        );
        passwordInput.sendKeys(password);
        driver.findElement(By.xpath("//span[text()='Next']")).click();
        log.info("login successfully");
        log.info("login successfully");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 5)
    void startAndHandleAllMeetings() {
        Date currentTime;
        final int minimumPreJoinInMinute = 2;
        try {
            openMeetingInTabsExceptOld();
            while (countCompletionOfTab != meetingSchedule.size()) {
                for (MeetSchedule meetSchedule : meetingSchedule) {
                    if (meetSchedule.getMeetStatus() == false) {
                        log.info("meeting " + meetSchedule.getMeetingCode() + " is still not done.");
                        if (meetSchedule.getRightTimeStatus()) {
                            String windowHandle = meetSchedule.getWindowHandleCode();
                            driver.switchTo().window(windowHandle);
                            if (meetSchedule.getJoinedAlreadyStatus()) {
                                if (meetSchedule.getRecordingStatus()) {
                                    Thread.sleep(500);
                                    if (checkParticipant(meetSchedule)) {
                                        try {
                                            leaveTheMeetingCall();
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
                                log.info("joining meeting : " + meetSchedule.getMeetingCode());
                                System.out.println("Need to Join for " + meetingSchedule.toString());
                                meetSchedule.setRightTimeStatus(true);
                                joinTheCall(meetSchedule);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            takeScreenshot();
            System.out.println("ss taken, Exception caught : " + e.toString());
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
                    while (driver.getWindowHandle() == null) {
                        Thread.sleep(5000);
                    }
                    driver.navigate().to("https://meet.google.com/" + meetingCode);
                    log.info(meetSchedule.getMeetingCode() + " - " + meetSchedule.getMeetingCode()
                            + " -> opening meeting in tab");
                    meetSchedule.setWindowHandleCode(driver.getWindowHandle());
                } else { //  if old time then ignore
                    ++countCompletionOfTab;
                    meetSchedule.setMeetStatus(true);
                    meetSchedule.setOldAlreadyStatus(true);
                    log.info(meetSchedule.toString() + "\n too late!  ,  can't join in the past time\n");
                    System.out.println(meetSchedule.toString() + "\n too late!  ,  can't join in the past time\n");
                }
                System.out.println(meetSchedule.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void takeScreenshot() {
        try {
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File file = takesScreenshot.getScreenshotAs(OutputType.FILE);
            File fileWithPath = new File("src/test/screenshots/ss_"
                    + (new Date()).toString().replaceAll(" ", "_") + ".jpg");
            try {
                FileUtils.copyFile(file, fileWithPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
        }
    }

    void leaveTheMeetingCall() {
        driver.findElement(By.cssSelector("div[jsaction='JIbuQc:LD0JHb']")).click(); //end call
    }

    void joinTheCall(MeetSchedule meetSchedule) {
        String windowHandleCode = meetSchedule.getWindowHandleCode();
        if (windowHandleCode == null) {
        } else {
            driver.switchTo().window(windowHandleCode);
        }
        try {
            checkCamMicBlockPopupAvailability();
            WebElement joinNow =
                    utils.checkElementAvailability(driver, By.xpath("//span[text()='Join now']"), 10);
            if (joinNow != null) {
                joinNow.click();
                meetSchedule.setJoinedAlreadyStatus(true);
                try {
                    dismissInsideCallPopup();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // retry
                if (meetSchedule.getRetry() < 3) {
                    meetSchedule.setRetry(meetSchedule.getRetry() + 1);
                    driver.navigate().refresh();
                } else {
                    log.info("tried 3 times " + meetSchedule.getMeetingCode());
                    meetSchedule.setMeetStatus(true);
                    takeScreenshot();
                    driver.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean checkParticipant(MeetSchedule meetSchedule) {
        if (checkParticipants(webDriverWait) >= minimumParticipantToLeftTheCall) {
            meetSchedule.setParticipantStatus(true);
            meetSchedule.setMeetStatus(true);
            countCompletionOfTab++;
            return true;
        }
        return false;
    }

    void dismissInsideCallPopup() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
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
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(25));
    }

    void checkCamMicBlockPopupAvailability() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
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
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(25));
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

    void startRec(MeetSchedule meetSchedule) {
        By moreOptions = By.xpath("(//div[@jscontroller='wg1P6b'])[2]");
        By recordMeeting = By.xpath("(//li[@role='menuitem'])[2]");
        By startRec = By.xpath("(//button[@class='VfPpkd-LgbsSe VfPpkd-LgbsSe-OWXEXe-k8QpJ VfPpkd-LgbsSe-OWXEXe-dgl2Hf Kjnxrf C1Uh5b DuMIQc qfvgSe pKAeHb'])[2]");
        By startPopup = By.cssSelector("button[data-mdc-dialog-action='ok']");
        By recordingIndicator = By.cssSelector("div[class='KHSqkf']"); //By.xpath("//i[text()='radio_button_checked']");
        By dismissPopup = By.xpath("(//span[text()='Dismiss'])[2]");
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
            Thread.sleep(1000);
            driver.findElement(dismissPopup).click();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(25));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            driver.findElement(moreOptions).click();
            driver.findElement(recordMeeting).click();
            driver.findElement(startRec).click();
            driver.findElement(startPopup).click();
            Thread.sleep(1000);
            if (driver.findElement(recordingIndicator).isDisplayed()) {
                meetSchedule.setRecordingStatus(true);
            }
        } catch (Exception e) {
            log.info("element not found for startRec() " + e.toString());
            takeScreenshot();
        }
    }

}
