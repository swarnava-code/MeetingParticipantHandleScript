package com.zopsmart.meet.utils;

import com.zopsmart.meet.model.DbConfig;
import com.zopsmart.meet.model.MeetSchedule;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataBaseUtil {
    DbConfig dbConfig;

    public DataBaseUtil(DbConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public List<MeetSchedule> readDataFromDB() {
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
                meetingCode = rs.getString(dbConfig.COL_MEETING_CODE);
                meetingStartDate = rs.getString(dbConfig.COL_MEETING_START_DATE);
                meetingStartTime = rs.getString(dbConfig.COL_MEETING_START_TIME);
                meetingEndDate = rs.getString(dbConfig.COL_MEETING_END_DATE);
                meetingEndTime = rs.getString(dbConfig.COL_MEETING_END_TIME);
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

    public boolean updateDbStatus(MeetSchedule meetSchedule) {
        String dbUserName = dbConfig.getDbUserName();
        String dbPassword = dbConfig.getDbPassword();
        String dbUrl = dbConfig.getDbUrl();
        String tableName = dbConfig.getTableName();
        String meetCode = meetSchedule.getMeetingCode();
        String dbStatus = meetSchedule.getMeetDbStatus();
        try {
            Connection con = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
            PreparedStatement ps = null;
            String query = "UPDATE " + tableName + " SET Status=? where meetCode=? ";
            ps = con.prepareStatement(query);
            ps.setString(1, dbStatus);
            ps.setString(2, meetCode);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("dbStatus update failed due to "+e);
            return false;
        }
        return true;
    }

}
