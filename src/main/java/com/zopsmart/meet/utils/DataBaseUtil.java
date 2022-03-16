package com.zopsmart.meet.utils;

import com.zopsmart.meet.model.DbConfig;
import com.zopsmart.meet.model.MeetSchedule;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataBaseUtil {
    public static final int COL_MEETING_CODE = 1;
    public static final int COL_MEETING_START_DATE = 2;
    public static final int COL_MEETING_START_TIME = 3;
    public static final int COL_MEETING_END_DATE = 4;
    public static final int COL_MEETING_END_TIME = 5;
    public static final int COL_STATUS = 6;

    public List<MeetSchedule> readDataFromDB(DbConfig dbConfig) {
        String dbUserName = dbConfig.getDbUserName();
        String dbPassword = dbConfig.getDbPassword();
        String dbUrl = dbConfig.getDbUrl();
        String tableName = dbConfig.getTableName();
        List<MeetSchedule> meetingSchedule = new ArrayList<>();
        MeetSchedule meetSchedule;
        SimpleDateFormat dateParser = new SimpleDateFormat("MMM d yyyy HH:mm:ss");
        try {
            Connection con = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from " + tableName);
            String meetingCode;
            String meetingStartDate;
            String meetingStartTime;
            String meetingEndDate;
            String meetingEndTime;
            while (rs.next()) {
                meetingCode = rs.getString(COL_MEETING_CODE);
                meetingStartDate = rs.getString(COL_MEETING_START_DATE);
                meetingStartTime = rs.getString(COL_MEETING_START_TIME);
                meetingEndDate = rs.getString(COL_MEETING_END_DATE);
                meetingEndTime = rs.getString(COL_MEETING_END_TIME);
                Date meetStartDateTime = null;
                Date meetEndDateTime = null;
                try {
                    meetStartDateTime = dateParser.parse(meetingStartDate + " " + meetingStartTime);
                    meetEndDateTime = dateParser.parse(meetingEndDate + " " + meetingEndTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                meetSchedule = new MeetSchedule();
                meetSchedule.setMeetingCode(meetingCode);
                meetSchedule.setMeetingStartTime(meetStartDateTime);
                meetSchedule.setMeetingEndTime(meetEndDateTime);
                meetingSchedule.add(meetSchedule);
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return meetingSchedule;
    }

    public boolean updateStatus(DbConfig dbConfig, MeetSchedule meetSchedule) {
        String dbUserName = dbConfig.getDbUserName();
        String dbPassword = dbConfig.getDbUserName();
        String dbUrl = dbConfig.getDbUserName();
        String tableName = dbConfig.getDbUserName();
        String meetCode = meetSchedule.getMeetingCode();
        String dbStatus = meetSchedule.getMeetingDbStatus();

        /*
               *** use above information to update status into table ***
         */

        System.out.println("tableName:" + tableName);
        System.out.println("meetCode:" + meetCode);
        System.out.println("dbStatus:" + dbStatus);

        return false; //(fail)
    }

}
