package com.mobile.hinde.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.mobile.hinde.database.DBHandler;

public class Alarm_receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        DBHandler dbHandler = new DBHandler(context, null);

        Toast.makeText(context, "DB CREATED", Toast.LENGTH_SHORT).show();
        //dbHandler.updateArrivedData("SUN", 0, 0);

    }
}
