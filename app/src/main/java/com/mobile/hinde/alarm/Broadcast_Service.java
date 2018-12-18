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

    private final Intent bi = new Intent(COUNTDOWN_TICK);

    private CountDownTimer cdt = null;

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
        long mDuration = intent.getExtras().getLong("duration");

        final String target = intent.getAction();

        Log.i(TAG, "Starting timer...");
        cdt = new CountDownTimer(mDuration, 1000) {
            private final String mTarget = target;
            @Override
            public void onTick(long millisUntilFinished) {

                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                bi.putExtra("countdown", millisUntilFinished);
                bi.putExtra("target",mTarget);
                bi.setAction(COUNTDOWN_TICK);
                sendBroadcast(bi);
            }

            @Override
            public void onFinish() {
                bi.setAction(COUNTDOWN_FINISH);
                bi.putExtra("target",mTarget);
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
