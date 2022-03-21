package com.zopsmart.meet.tool;

import com.zopsmart.meet.MeetBase;
import com.zopsmart.meet.utils.MyProperty;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.sql.*;

public class SheetToDbScript extends MeetBase {
    String pathForSheet;
    static Connection connection = null;
    String dbName;
    String dbUrl;
    String tableName;
    String dbUserName;
    String dbPassword;

    @Test(priority = 1)
    void readPropertyFileForDbCredentials() {
        MyProperty myProperties = new MyProperty(PATH_PROPERTY_FILE);
        dbName = myProperties.getDbName();
        dbUrl = myProperties.getDbUrl();
        tableName = myProperties.getTableName();
        dbUserName = myProperties.getDbUsername();
        dbPassword = myProperties.getDbPassword();
        pathForSheet = myProperties.getSheetPath();
        System.out.println(myProperties.toString());
    }

    @Test(priority = 2)
    public void setConnection() {
        try {
            connection = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
            System.out.println("connection established");
        } catch (SQLException e) {
            System.out.println("connection failed");
            e.printStackTrace();
        }
    }

    @Test(priority = 3)
    public void deleteAllDataFromDb() {
        String query = "DELETE FROM " + tableName + ";";
        try {
            PreparedStatement preparedStmt = connection.prepareStatement(query);
            preparedStmt.execute();
            System.out.println(query+" (success)");
        } catch (Exception e) {
            System.out.println(query+" (fail)");
            e.printStackTrace();
        }
    }

    @Test(priority = 4)
    public void readSheetAndImportIntoDb() {
        int numberOfRow;
        try {
            FileInputStream fis = new FileInputStream(pathForSheet);
            Workbook workbook = WorkbookFactory.create(fis);
            numberOfRow = workbook.getSheet("Sheet1").getLastRowNum();
            System.out.println("no. of rows: " + numberOfRow);

            String meetingCode;
            String meetingStartDate;
            String meetingStartTime;
            String meetingEndDate;
            String meetingEndTime;
            String meetingStatus = null;
            String query = "";
            for (int i = 1; i <= numberOfRow; i++) {
                meetingCode = workbook.getSheet("Sheet1").getRow(i).getCell(0).getStringCellValue();
                meetingStartDate = workbook.getSheet("Sheet1").getRow(i).getCell(1).getStringCellValue();
                meetingStartTime = workbook.getSheet("Sheet1").getRow(i).getCell(2).getStringCellValue();
                meetingEndDate = workbook.getSheet("Sheet1").getRow(i).getCell(3).getStringCellValue();
                meetingEndTime = workbook.getSheet("Sheet1").getRow(i).getCell(4).getStringCellValue();
                query = "INSERT INTO " + tableName +
                        " VALUES  ('"
                        + meetingCode + "', '"
                        + meetingStartDate + "', '"
                        + meetingStartTime + "', '"
                        + meetingEndDate + "', '"
                        + meetingEndTime + "', '"
                        + meetingStatus
                        + "' );";
                System.out.println(query);
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

    @Test(priority = 5)
    void closeConnection() {
        try {
            connection.close();
            System.out.println("db connection closed");
        } catch (SQLException e) {
            System.out.println("failed to close connection");
            e.printStackTrace();
        }
    }

}
