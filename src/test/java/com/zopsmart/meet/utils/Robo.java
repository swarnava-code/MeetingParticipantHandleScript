package com.zopsmart.meet.utils;

import java.awt.*;

public class Robo {
    Robot robot;

    public Robo() {
        init();
    }

    void init(){
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void click(int keycode){
        robot.keyPress(keycode);
        robot.keyRelease(keycode);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
