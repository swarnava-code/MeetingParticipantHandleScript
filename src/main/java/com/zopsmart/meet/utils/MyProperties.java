package com.zopsmart.meet.utils;

import java.io.FileInputStream;
import java.util.Properties;

public class MyProperties {
    private String username;
    private String password;

    public MyProperties(String path){
        try {
            FileInputStream fis = new FileInputStream(path);
            Properties properties = new Properties();
            properties.load(fis);
            username = properties.getProperty("username");
            password = properties.getProperty("password");
        } catch (Exception e) {
            System.out.println("Exception handled: "+e.toString());
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}