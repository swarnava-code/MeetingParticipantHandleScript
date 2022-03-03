package com.zopsmart.meet.utils;

import com.zopsmart.meet.pom.HomePage;
import com.zopsmart.meet.pom.SignInPage;
import io.github.bonigarcia.wdm.WebDriverManager;
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
import org.testng.annotations.Test;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ScriptUtility {
    WebDriver driver;
    WebDriverWait wait;

    public ScriptUtility(WebDriver driver, WebDriverWait webDriverWait) {
        this.driver = driver;
        this.wait = webDriverWait;
    }

    public void login() throws InterruptedException {
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
    }

    public void googleMeetRecoding() throws InterruptedException, AWTException, IOException, ParseException {
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

                driver.findElement(By.id("i3")).sendKeys(link);
                WebElement clickOnJoinButton = wait.until(ExpectedConditions
                        .visibilityOfElementLocated(By.cssSelector("#yDmH0d > c-wiz > div > div.S3RDod > div " +
                                "> div.Qcuypc > div.Ez8Iud > div > div.KOM0mb > " +
                                "div.VfPpkd-dgl2Hf-ppHlrf-sM5MNb > button > span")));
                clickOnJoinButton.click();
                WebElement clickOnJoinNow = wait.until(ExpectedConditions
                        .elementToBeClickable(By.xpath("//span[text()='Join now']")));
                clickOnJoinNow.click();
                if (!meetingRecord.contains(link)) {
                    if (!meetingRecord.contains(link)) {
                        meetingRecord.add(link);
                    }
                    WebElement clickOnActivities = wait.until(ExpectedConditions
                            .elementToBeClickable(By.cssSelector("button[aria-label='Activities']")));
                    clickOnActivities.click();
                    WebElement clickOnRecoding = wait.until(ExpectedConditions
                            .elementToBeClickable(By.xpath("//li/span/span[text()='Recording']")));
                    clickOnRecoding.click();
                    WebElement clickOnStartRecoding = wait.until(ExpectedConditions
                            .elementToBeClickable(By.xpath("//button/span[text()='Start recording']")));
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

    public void closeRecoding() throws InterruptedException {
        List<String> windows = new ArrayList<String>(driver.getWindowHandles());
        System.out.println(windows);
        while (true) {
            for (int i = 0; i < windows.size(); i++) {
                WebElement noOfParticipantText = driver.switchTo().window(windows.get(i)).findElement(By.xpath("//div[@class='uGOf1d']"));
                String noOfParticipantValue = noOfParticipantText.getText();
                if (!noOfParticipantValue.equals("") && !noOfParticipantValue.equals(null)) {
                    int noOfParticipant = Integer.parseInt(noOfParticipantText.getText());
                    if (noOfParticipant > 1) {
                        driver.findElement(By.xpath("//button/i[text()='call_end']")).click();
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

}
