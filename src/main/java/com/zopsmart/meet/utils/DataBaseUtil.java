package com.zopsmart.meet.utils;

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
    public static final int MEETING_CODE = 1;
    public static final int MEETING_DATE = 2;
    public static final int MEETING_TIME = 3;

    public List<MeetSchedule> readDataFromDB(String dbUserName, String dbPassword, String dbUrl, String tableName) {
        List<MeetSchedule> meetingSchedule = new ArrayList<>();
        MeetSchedule meetSchedule;
        SimpleDateFormat dateParser = new SimpleDateFormat("MMM d yyyy HH:mm:ss");
        try {
            Connection con = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from " + tableName);
            String meetingCode;
            String meetingDate;
            String meetingTime;
            while (rs.next()) {
                meetingCode = rs.getString(MEETING_CODE);
                meetingDate = rs.getString(MEETING_DATE);
                meetingTime = rs.getString(MEETING_TIME);
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
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return meetingSchedule;
    }

}
