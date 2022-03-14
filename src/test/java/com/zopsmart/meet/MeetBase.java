package com.zopsmart.meet;

import com.zopsmart.meet.utils.MyUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.time.Duration;

public class MeetBase {
    public WebDriver driver;
    WebDriverWait webDriverWait;
    String meetUrl;
    String meetUserName;
    String meetPassword;
    String dbUserName;
    String dbPassword;
    String dbUrl;
    String sheetPath;
    String sheetName;
    String tableName;
    final String PATH_PROPERTY_FILE = "/home/swarnava/Desktop/password/dataFile.properties";

    @BeforeClass
    public void setUpDriver() {
        WebDriverManager.chromedriver().setup();
        MyUtil myUtil = new MyUtil();
        ChromeOptions chromeOptions = myUtil.enableCameraAndMic(false);
        driver = new ChromeDriver(chromeOptions);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));//100
        driver.manage().window().maximize();
        webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }

}
