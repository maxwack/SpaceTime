package com.mobile.hinde.database;

public class App_Settings {
    private String property;
    private String value;

    public App_Settings(){

    }

    public App_Settings(String property, String value){
        this.property = property;
        this.value = value;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
