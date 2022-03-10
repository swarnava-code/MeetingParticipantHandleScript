package com.zopsmart.meet.utils;

import java.awt.*;

public class Robot {
    java.awt.Robot robot;

    public Robot() {
        init();
    }

    void init() {
        try {
            this.robot = new java.awt.Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void click(int keycode) {
        robot.keyPress(keycode);
        robot.keyRelease(keycode);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
