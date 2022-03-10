package com.zopsmart.meet.utils;

import java.io.FileInputStream;
import java.util.Properties;

public class MyProperty {
    private String meetUrl;
    private String meetUsername;
    private String meetPassword;
    private String dbName;
    private String dbUsername;
    private String dbPassword;
    private String dbUrl;
    private String tableName;
    private String sheetPath;
    private String sheetName;

    public MyProperty(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            Properties properties = new Properties();
            properties.load(fis);
            meetUrl = properties.getProperty("meetUrl");
            meetUsername = properties.getProperty("meetUsername");
            meetPassword = properties.getProperty("meetPassword");
            dbName = properties.getProperty("dbName");
            tableName = properties.getProperty("tableName");
            dbUsername = properties.getProperty("dbUsername");
            dbPassword = properties.getProperty("dbPassword");
            String initialDbUrl = properties.getProperty("initialDbUrl");
            dbUrl = initialDbUrl + dbName;
            sheetPath = properties.getProperty("sheetPath");
            sheetName = properties.getProperty("sheetName");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getMeetUrl() {
        return meetUrl;
    }

    public String getMeetUsername() {
        return meetUsername;
    }

    public String getMeetPassword() {
        return meetPassword;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getTableName() {
        return tableName;
    }

    public String getSheetPath() {
        return sheetPath;
    }

    public String getSheetName() {
        return sheetName;
    }

    @Override
    public String toString() {
        return "MyProperty{" +
                "meetUrl='" + meetUrl + '\'' +
                ", \nmeetUsername='" + meetUsername + '\'' +
                ", \nmeetPassword='" + meetPassword + '\'' +
                ", \ndbName='" + dbName + '\'' +
                ", \ndbUsername='" + dbUsername + '\'' +
                ", \ndbPassword='" + dbPassword + '\'' +
                ", \ndbUrl='" + dbUrl + '\'' +
                ", \ntableName='" + tableName + '\'' +
                ", \nsheetPath='" + sheetPath + '\'' +
                ", \nsheetName='" + sheetName + '\'' +
                '}';
    }

}