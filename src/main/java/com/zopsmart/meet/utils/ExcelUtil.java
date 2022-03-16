package com.zopsmart.meet.utils;

import com.zopsmart.meet.model.MeetSchedule;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExcelUtil {

    public static final int MEETING_CODE = 0;
    public static final int MEETING_DATE = 1;
    public static final int MEETING_TIME = 2;

    public List<MeetSchedule> readData(String sheetPath, String sheetName) {
        List<MeetSchedule> meetingSchedule = new ArrayList<>();
        int numberOfRow;
        MeetSchedule meetSchedule;
        SimpleDateFormat dateParser = new SimpleDateFormat("MMM d yyyy HH:mm:ss");
        try {
            FileInputStream fis = new FileInputStream(sheetPath);
            Workbook workbook = WorkbookFactory.create(fis);
            numberOfRow = workbook.getSheet(sheetName).getLastRowNum();
            String meetingCode;
            String meetingStartDate;
            String meetingStartTime;
            String meetingEndDate;
            String meetingEndTime;
            String meetingStatus = null;
            for (int i = 1; i <= numberOfRow; i++) {

                meetingCode = workbook.getSheet("Sheet1").getRow(i).getCell(0).getStringCellValue();
                meetingStartDate = workbook.getSheet("Sheet1").getRow(i).getCell(1).getStringCellValue();
                meetingStartTime = workbook.getSheet("Sheet1").getRow(i).getCell(2).getStringCellValue();
                meetingEndDate = workbook.getSheet("Sheet1").getRow(i).getCell(3).getStringCellValue();
                meetingEndTime = workbook.getSheet("Sheet1").getRow(i).getCell(4).getStringCellValue();

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
            workbook.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return meetingSchedule;
    }

}
