package com.mobile.hinde.utils;

public class UserSettings {
    private static UserSettings instance = new UserSettings();
    private String userId;
    private long money;

    private UserSettings(){
    }

    public static UserSettings getInstance(){
        return instance;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getUserId(){
        return userId;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }
}
