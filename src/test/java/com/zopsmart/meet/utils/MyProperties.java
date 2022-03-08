package com.zopsmart.meet.utils;

import java.io.FileInputStream;
import java.util.*;

public class MyProperties {
    private String username;
    private String password;
    private String dbName;
    private String url;
    private String tableName;
    private String dbUsername;
    private String dbPassword;

    public MyProperties(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            Properties properties = new Properties();
            properties.load(fis);
            username = properties.getProperty("username");
            password = properties.getProperty("password");
            dbName = properties.getProperty("dbName");
            tableName = properties.getProperty("tableName");
            dbUsername = properties.getProperty("dbUsername");
            dbPassword = properties.getProperty("dbPassword");
            String initUrl = properties.getProperty("initialUrl");
            url = initUrl + dbName;
        } catch (Exception e) {
            System.out.println("Exception handled: " + e.toString());
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDbName() {
        return dbName;
    }

    public String getUrl() {
        return url;
    }

    public String getTableName() {
        return tableName;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

}