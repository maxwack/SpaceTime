package com.mobile.hinde.alarm;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

public class Broadcast_Service extends Service {

    private final static String TAG = "BroadcastService";

    public static final String COUNTDOWN_TICK = "tick";
    public static final String COUNTDOWN_FINISH = "finish";

    private long mDuration = 0;
    Intent bi = new Intent(COUNTDOWN_TICK);

    CountDownTimer cdt = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if(cdt != null) {
            cdt.cancel();
        }
        Log.i(TAG, "Timer cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDuration = intent.getExtras().getLong("duration");

        Log.i(TAG, "Starting timer...");
        cdt = new CountDownTimer(mDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                bi.putExtra("countdown", millisUntilFinished);
                bi.setAction(COUNTDOWN_TICK);
                sendBroadcast(bi);
            }

            @Override
            public void onFinish() {
                bi.setAction(COUNTDOWN_FINISH);
                sendBroadcast(bi);
                Log.i(TAG, "Timer finished");
            }
        };

        cdt.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
