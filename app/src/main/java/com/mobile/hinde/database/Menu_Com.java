package com.mobile.hinde.database;

public class Menu_Com {
    private String name;
    private long last_execute;
    private int is_arrived;
    private int is_returned;

    public Menu_Com(){

    }

    public Menu_Com(String name, long last_execute, int isArrived, int isReturned){
        this.name = name;
        this.last_execute = last_execute;
        this.is_arrived = isArrived;
        this.is_returned = isReturned;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {

        return name;
    }

    public long getLast_execute() {
        return last_execute;
    }

    public void setLast_execute(long last_execute) {
        this.last_execute = last_execute;
    }

    public int getIs_Arrived() {
        return is_arrived;
    }

    public void setIs_Arrived(int is_Arrived) {
        this.is_arrived = is_Arrived;
    }

    public int getIs_Returned() {
        return is_returned;
    }

    public void setIs_Returned(int is_Returned) {
        this.is_returned = is_Returned;
    }
}
