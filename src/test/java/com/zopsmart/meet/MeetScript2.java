package com.zopsmart.meet;

import com.zopsmart.meet.pom.HomePage;
import com.zopsmart.meet.pom.JoinPage;
import com.zopsmart.meet.pom.MeetingPage;
import com.zopsmart.meet.pom.SignInPage;
import com.zopsmart.meet.utils.MyProperties;
import com.zopsmart.meet.utils.MyUtils;
import com.zopsmart.meet.utils.Robo;
import com.zopsmart.meet.utils.ScriptUtility;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MeetScript2 extends MeetBase {

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
                meetingSchedule.put(meetDateTime, meetingCode);
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






    ////###############################################
    /*
    WebDriver driver = null;
    WebDriverWait wait = null;
    ScriptUtility script;

    @BeforeClass
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        WebDriverManager.chromedriver().setup();
        options.addArguments("--disable-notifications");
        HashMap<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.media_stream_mic", 1);
        prefs.put("profile.default_content_setting_values.media_stream_camera", 1);
        prefs.put("profile.default_content_setting_values.geolocation", 2);
        prefs.put("profile.default_content_setting_values.notifications", 2);
        options.setExperimentalOption("prefs", prefs);
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.manage().window().maximize();
        driver.get("https://meet.google.com");
        script = new ScriptUtility(driver, wait);
    }

    @Test(priority = 1)
    public void login() throws InterruptedException {
        //script.login();
        HomePage homePage = new HomePage(driver);
        SignInPage signInPage = new SignInPage(driver);
        WebElement signInButton = wait.until(ExpectedConditions
                .elementToBeClickable(homePage.getSignIn()));
        signInButton.click();
        signInPage.sendUsername("test-hiring@raramuri.tech");
        signInPage.clickNext();
        signInPage.sendPassword("Zopsmart@2022");
        WebElement next = wait.until(ExpectedConditions.elementToBeClickable(signInPage.getNext()));
        next.click();
        Thread.sleep(3000);
    }

    @Test(priority = 2)
    public void googleMeetRecoding() throws InterruptedException, AWTException, IOException, ParseException {
        MeetingPage meetingPage = new MeetingPage(driver);
        JoinPage joinPage = new JoinPage(driver);
        HomePage homePage = new HomePage(driver);
        java.util.List<String> meetingRecord = new ArrayList<>();
        Robot robot = new Robot();
        int rowCount = 0;
        int i = 1;
        FileInputStream fileInputStream = new FileInputStream("./src/test/files/link_data.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        XSSFSheet sheet = workbook.getSheet("Sheet1");
        int rows = sheet.getLastRowNum();
        System.out.println("rows is: " + rows);
        while (true) {
            XSSFRow row = sheet.getRow(i);
            String time = row.getCell(0).toString();
            String link = row.getCell(1).toString();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = new Date();
            String systemTime = formatter.format(date).substring(11).trim();
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            Date date1 = format.parse(systemTime);
            Date date2 = format.parse(time);
            long diff = date2.getTime() - date1.getTime();
            long diffMinutes = diff / (60 * 1000) % 60;

            if (diffMinutes >= 0 && diffMinutes <= 2) {
                System.out.println(link);

                homePage.sendMeetCodeInputField(link);

                WebElement clickOnJoinButton = wait.until(ExpectedConditions
                        .visibilityOfElementLocated(homePage.getJoin()));
                clickOnJoinButton.click();

                WebElement clickOnJoinNow = wait.until(ExpectedConditions
                        .elementToBeClickable(joinPage.getJoinNow()));
                clickOnJoinNow.click();

                if (!meetingRecord.contains(link)) {
                    if (!meetingRecord.contains(link)) {
                        meetingRecord.add(link);
                    }

                    WebElement clickOnActivities = wait.until(ExpectedConditions
                            .elementToBeClickable(meetingPage.getActivities()));
                    clickOnActivities.click();

                    WebElement clickOnRecoding = wait.until(ExpectedConditions
                            .elementToBeClickable(meetingPage.getRecording()));
                    clickOnRecoding.click();

                    WebElement clickOnStartRecoding = wait.until(ExpectedConditions
                            .elementToBeClickable(meetingPage.getStartRecording()));
                    clickOnStartRecoding.click();

                    for (int j = 0; j < 2; ++j) {
                        robot.keyPress(KeyEvent.VK_TAB);
                        robot.keyRelease(KeyEvent.VK_TAB);
                    }
                    Thread.sleep(5000);
                    robot.keyPress(KeyEvent.VK_ENTER);
                    robot.keyRelease(KeyEvent.VK_ENTER);
                }
                rowCount++;
                if (rowCount == rows - 1) {
                    break;
                }
                driver.switchTo().newWindow(WindowType.TAB);
                driver.get("https://meet.google.com");
                Thread.sleep(5000);
            }

            i++;
            if (i > rows) {
                i = 1;
            }
        }
        fileInputStream.close();
    }

    @Test(priority = 3)
    public void closeRecoding() throws InterruptedException {
        MeetingPage meetingPage = new MeetingPage(driver);
        List<String> windows = new ArrayList<String>(driver.getWindowHandles());
        System.out.println(windows);
        while (true) {
            for (int i = 0; i < windows.size(); i++) {
                WebElement noOfParticipantText = driver.switchTo().window(windows.get(i)).findElement(meetingPage.getShowEveryOneStatus());
                String noOfParticipantValue = noOfParticipantText.getText();
                if (!noOfParticipantValue.equals("") && !noOfParticipantValue.equals(null)) {
                    int noOfParticipant = Integer.parseInt(noOfParticipantText.getText());
                    if (noOfParticipant > 1) {
                        meetingPage.clickCallEnd();
                        windows.remove(windows.get(i));
                        Thread.sleep(3000);
                        driver.close();
                    }
                }
            }
            if (windows.size() == 0) {
                break;
            }
        }
    }

     */

}
