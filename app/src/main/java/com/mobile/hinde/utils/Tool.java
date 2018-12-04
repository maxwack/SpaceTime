package com.mobile.hinde.utils;

public class Tool {


    public static String formatTimeToString(long time){
        time = time / 1000;
        return String.format("%02d:%02d:%02d", time / 3600, (time % 3600) / 60, (time % 60));
    }
}