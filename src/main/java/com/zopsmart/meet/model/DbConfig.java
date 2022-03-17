package com.zopsmart.meet.model;

public class DbConfig {
    private String dbUserName;
    private String dbPassword;
    private String dbUrl;
    private String tableName;
    public final int COL_MEETING_CODE = 1;
    public final int COL_MEETING_START_DATE = 2;
    public final int COL_MEETING_START_TIME = 3;
    public final int COL_MEETING_END_DATE = 4;
    public final int COL_MEETING_END_TIME = 5;
    public final int COL_STATUS = 6;

    public DbConfig(String dbUserName, String dbPassword, String dbUrl, String tableName) {
        this.dbUserName = dbUserName;
        this.dbPassword = dbPassword;
        this.dbUrl = dbUrl;
        this.tableName = tableName;
    }

    public String getDbUserName() {
        return dbUserName;
    }

    public void setDbUserName(String dbUserName) {
        this.dbUserName = dbUserName;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }



}
