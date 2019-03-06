package com.mobile.hinde.utils;

import android.graphics.drawable.Drawable;

import java.io.InputStream;
import java.net.URL;

public class Tool {


    public static String formatTimeToString(long time){
        time = time / 1000;
        return String.format("%02d:%02d:%02d", time / 3600, (time % 3600) / 60, (time % 60));
    }

    public static String formatMoneyCount(long amount){
        return String.format("%04d", amount);
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream)new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }
}
