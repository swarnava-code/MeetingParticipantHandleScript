package com.zopsmart.meet;

import com.zopsmart.meet.model.DbConfig;
import com.zopsmart.meet.model.MeetSchedule;
import com.zopsmart.meet.pom.SignInPage;
import com.zopsmart.meet.utils.DataBaseUtil;
import com.zopsmart.meet.utils.MeetingUtil;
import com.zopsmart.meet.utils.MyProperty;
import org.testng.annotations.Test;

import java.util.*;

public class MeetScript extends MeetBase {

    @Test(priority = 1)
    void readPropertyFile() {
        MyProperty myProperty = new MyProperty(PATH_PROPERTY_FILE);
        meetUrl = myProperty.getMeetUrl();
        meetUserName = myProperty.getMeetUsername();
        meetPassword = myProperty.getMeetPassword();
        dbUserName = myProperty.getDbUsername();
        dbPassword = myProperty.getDbPassword();
        dbUrl = myProperty.getDbUrl();
        sheetPath = myProperty.getSheetPath();
        sheetName = myProperty.getSheetName();
        tableName = myProperty.getTableName();
        System.out.println(myProperty.toString());
    }

    @Test(priority = 2)
    void recodingTest() {
        try {
            DataBaseUtil dataBaseUtil = new DataBaseUtil();
            SignInPage signInPage = new SignInPage();
            DbConfig dbConfig = new DbConfig(dbUserName, dbPassword, dbUrl, tableName);
            List<MeetSchedule> meetingSchedule = dataBaseUtil.readDataFromDB(dbConfig);

            for (MeetSchedule meet :
                    meetingSchedule) {
                System.out.println(meet.toString());
            }

            signInPage.signIn(driver, meetUserName, meetPassword, meetUrl);
            MeetingUtil meetingUtil = new MeetingUtil();
            meetingUtil.startAndHandleAllMeetings(driver, meetingSchedule);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
