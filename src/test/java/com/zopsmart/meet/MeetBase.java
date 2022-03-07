package com.zopsmart.meet;

import com.zopsmart.meet.model.MeetSchedule;
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
import java.util.*;

public class MeetBase {
    public WebDriver driver;
    WebDriverWait webDriverWait;
    MyUtils myUtils = new MyUtils();
    String username;
    String password;
    static List<MeetSchedule>  meetingSchedule = new ArrayList<MeetSchedule>();
    MyUtils utils = new MyUtils();
    Robo robo = new Robo();
    String meetingCode;
    String pathForPropertyFile = "/home/swarnava/Desktop/password/dataFile.properties";
    String pathForSheet = "src/test/files/meeting_sheet.xls";
    
    @BeforeClass
    public void setUpDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = myUtils.enableCameraAndMic(false);
        driver = new ChromeDriver(chromeOptions);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));//100
        driver.manage().window().maximize();
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }

}
