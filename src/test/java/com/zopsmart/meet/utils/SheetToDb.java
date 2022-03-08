package com.zopsmart.meet.utils;

import com.zopsmart.meet.MeetBase;
import com.zopsmart.meet.model.MeetSchedule;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xddf.usermodel.text.TabAlignment;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.sql.*;

public class SheetToDb {
    final String PATH_FOR_SHEET = "src/test/files/meeting_sheet.xls";
    String pathForPropertyFile = "/home/swarnava/Desktop/password/dataFile.properties";
    static Connection connection = null;
    String dbName;
    String url;
    String tableName;
    String dbUserName;
    String dbPassword;

    @Test(priority = 1)
    void readPropertyFileForDbCredentials() {
        MyProperties myProperties = new MyProperties(pathForPropertyFile);
        dbName = myProperties.getDbName();
        url = myProperties.getUrl();
        tableName = myProperties.getTableName();
        dbUserName = myProperties.getDbUsername();
        dbPassword = myProperties.getDbPassword();
    }

    @Test(priority = 2)
    public void setConnection() {
        try {
            connection = DriverManager.getConnection(url, dbUserName, dbPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 3)
    public void deleteAllDataFromDb() {
        try {
            String query = "delete from " + tableName + ";";
            System.out.println(query);
            PreparedStatement preparedStmt = connection.prepareStatement(query);
            preparedStmt.execute();
        } catch (Exception e) {
        }
    }

    @Test(priority = 4)
    public void readSheetAndImportIntoDb() {
        int numberOfRow;
        try {
            FileInputStream fis = new FileInputStream(PATH_FOR_SHEET);
            Workbook workbook = WorkbookFactory.create(fis);
            numberOfRow = workbook.getSheet("Sheet1").getLastRowNum();
            String meetingDate;
            String meetingTime;
            String meetingCode;
            String query = "";
            for (int i = 1; i <= numberOfRow; i++) {
                meetingCode = workbook.getSheet("Sheet1").getRow(i).getCell(0).getStringCellValue();
                meetingDate = workbook.getSheet("Sheet1").getRow(i).getCell(1).getStringCellValue();
                meetingTime = workbook.getSheet("Sheet1").getRow(i).getCell(2).getStringCellValue();
                query = "Insert into " + tableName +
                        " values  ('" + meetingCode + "', '" + meetingDate + "', '" + meetingTime + "' );";
                try {
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(query);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            workbook.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
