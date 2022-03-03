package com.zopsmart.meet;

import com.zopsmart.meet.utils.BaseUtility;
import com.zopsmart.meet.utils.MyProperties;
import com.zopsmart.meet.utils.MyUtils;
import com.zopsmart.meet.utils.Robo;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class MeetBase {
    public WebDriver driver;
    WebDriverWait webDriverWait;
    MyUtils myUtils = new MyUtils();
    BaseUtility baseUtility = new BaseUtility();
    String username;
    String password;
    Map<Date, String> meetingSchedule = new TreeMap<>();
    MyUtils utils = new MyUtils();
    Robo robo = new Robo();
    String meetingCode;
    String pathForPropertyFile = "/home/swarnava/Desktop/password/dataFile.properties";
    String pathForSheet = "./src/test/files/meeting_sheet.xls";
    

    @BeforeClass
    public void setUpDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = myUtils.disableNotification();
        driver = new ChromeDriver(chromeOptions);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(100));
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    void initDriver(){
        //baseUtility.initDriver(driver);
        //baseUtility.initWebDriverWait(driver, webDriverWait);
        //        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = myUtils.disableNotification();
        driver = new ChromeDriver(chromeOptions);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().scriptTimeout(Duration.ofMinutes(2));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(100));
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    void getUsernameAndPassword() {
        MyProperties myProperties = new MyProperties(pathForPropertyFile);
        username = myProperties.getUsername();
        password = myProperties.getPassword();
    }

    @AfterClass
    public void tearDown() {
        // driver.quit();
    }
}
