package com.zopsmart.meet;

import com.zopsmart.meet.utils.MyProperties;
import com.zopsmart.meet.utils.MyUtils;
import com.zopsmart.meet.utils.Robo;
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
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class MeetScript extends MeetBase {
//    Map<Date, String> meetingSchedule = new TreeMap<>();
//    MyUtils utils = new MyUtils();
//    Robo robo = new Robo();
//    String meetingCode;

    @Test(priority = 1)
    void readPropertyFile() {
        String absPath = "/home/swarnava/Desktop/password/dataFile.properties";
        //String absPath = "src/test/files/dataFile.properties";
        MyProperties myProperties = new MyProperties(absPath);
        username = myProperties.getUsername();
        password = myProperties.getPassword();
        System.out.println(username);
    }

    @Test(priority = 1)
    public void readSheet() {
        int numberOfRow;
        SimpleDateFormat dateParser = new SimpleDateFormat("MMM d yyyy HH:mm:ss");
        try {
            FileInputStream fis = new FileInputStream("./src/test/files/meeting_sheet.xls");
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
                //meetingSchedule.put(meetDateTime, meetingCode);
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

/*
    @Test(priority = 3)
    void startAndHandleAllMeetings() {
        Date currentTime = null;
        System.out.println("\n Total meetings : " + meetingSchedule.size());
        for (Date meetingTime : meetingSchedule.keySet()) {
            System.out.println(meetingTime + " - " + meetingSchedule.get(meetingTime));
            currentTime = new Date();
            meetingCode = meetingSchedule.get(meetingTime);
            // We can join 2 min. before, but not more than 2 min.
            int minimumPreJoinInMinute = 2;
            if (meetingTime.after(currentTime)) {
                long differenceInMs = meetingTime.getTime() - currentTime.getTime();
                long differenceInMinutes = TimeUnit.MILLISECONDS.toMinutes(differenceInMs);
                if (differenceInMinutes <= minimumPreJoinInMinute) {
                    System.out.println("Need to Join immediately for " + meetingTime);
                    mainController();
                } else {
                    // Join 2 minute ago, else wait
                    while (differenceInMinutes > minimumPreJoinInMinute) {
                        currentTime = new Date();
                        differenceInMs = meetingTime.getTime() - currentTime.getTime();
                        differenceInMinutes = TimeUnit.MILLISECONDS.toMinutes(differenceInMs);
                        System.out.println(currentTime + " => " + differenceInMinutes + " min");
                        utils.waitForSomeTime(5);
                    }
                    System.out.println("Now we can join");
                    mainController();
                }
            } else {
                System.out.println("We missed the meeting to record -> " + meetingTime);
            }
            System.out.println("=====================================");
        }
    }

 */

    void mainController() {
        int minimumParticipantToLeftTheCall = 2;
        applyAndJoin();
        dismissInsideCallPopup();
        startRec();
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
            return numberOfParticipants;
        }
        return numberOfParticipants;
    }

    void clickAttemptTwoTime(WebElement webElement) {
        try {
            webElement.click();
        } catch (Exception e) {
            System.out.println("element not clickable : " + webElement);
        }
        try {
            webElement.click();
        } catch (Exception e) {
        }
    }

    void startRec() {
        WebElement clickOnActivities = webDriverWait.until(ExpectedConditions
                .elementToBeClickable(By.cssSelector("button[aria-label='Activities']")));
        clickAttemptTwoTime(clickOnActivities);

        WebElement clickOnRecoding = webDriverWait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//li/span/span[text()='Recording']")));
        clickAttemptTwoTime(clickOnRecoding);

        WebElement clickOnStartRecoding = webDriverWait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//button/span[text()='Start recording']")));
        clickAttemptTwoTime(clickOnStartRecoding);

        WebElement clickOnStartPopupButton = webDriverWait.until(ExpectedConditions
                .elementToBeClickable(By.xpath("//span[text()='Start']")));
        clickAttemptTwoTime(clickOnStartPopupButton);
    }

}