package com.mobile.hinde.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;
import com.mobile.hinde.alarm.Broadcast_Service;
import com.mobile.hinde.connection.AsyncResponse;
import com.mobile.hinde.connection.Duration_Site;
import com.mobile.hinde.database.DBHandler;
import com.mobile.hinde.spacetime.Frag_Send_Accept;
import com.mobile.hinde.spacetime.R;
import com.mobile.hinde.view.DynamicSineWaveView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Comm_model {

    private Frag_Send_Accept controller;
    private Context c;
    private View v;
    private DBHandler mdbHandler;

    public Comm_model(Frag_Send_Accept controller){
        this.controller = controller;
        this.c = controller.getContext();
        this.mdbHandler = controller.getmDBHandler();
    }

    public void startSending(final String targetName){
        new Duration_Site(new AsyncResponse(){

            @Override
            public void processFinish(JSONObject output){
                try{
                    long duration = output.getLong(targetName);
                    Intent intent = new Intent(c,Broadcast_Service.class);
                    intent.setAction(targetName);
                    intent.putExtra("duration",duration * 2);

                    c.startService(intent);

                    Button but_Send = v.findViewById(R.id.but_Send);
                    but_Send.setVisibility(View.INVISIBLE);

                    DynamicSineWaveView wavesView = v.findViewById(R.id.SineWave);
                    wavesView.setVisibility(View.VISIBLE);
                    wavesView.startAnimation();

                    TextView txt_Sun = v.findViewById(R.id.Timer);
                    txt_Sun.setVisibility(View.VISIBLE);
                    mdbHandler.updateData(targetName, System.currentTimeMillis(), System.currentTimeMillis()+ 2 * duration);
                }
                catch(NullPointerException | JSONException npe){

                }
            }
        }).execute(targetName);
    }

    public void checkRemainingTimer(String targetName){
        long currTime = System.currentTimeMillis();
        HashMap<String,Long> targetMap = mdbHandler.searchStartedData(targetName);

        if(targetMap.containsKey(targetName)){
            long value = targetMap.get(targetName);
            if(currTime < value){
                Button but_send = v.findViewById(R.id.but_Send);
                but_send.setVisibility(View.INVISIBLE);

                DynamicSineWaveView wavesView = v.findViewById(R.id.SineWave);
                wavesView.setVisibility(View.VISIBLE);
                wavesView.startAnimation();

                long duration = value - currTime;
                Intent intent = new Intent(c,Broadcast_Service.class);
                intent.setAction(targetName);
                intent.putExtra("duration",duration);
                c.startService(intent);
            }else{
                Intent intent = new Intent("finish");
                intent.putExtra("target",targetName);
                c.sendBroadcast(intent);
            }
        }
    }

    public void resetMainView(String targetName){

        Button but_Accept = v.findViewById(R.id.but_Accept);
        but_Accept.setVisibility(View.INVISIBLE);

        Button but_Send = v.findViewById(R.id.but_Send);
        but_Send.setVisibility(View.VISIBLE);

        mdbHandler.resetExpectedEnd(targetName);

        TextView txt = v.findViewById(R.id.Timer);
        txt.setVisibility(View.INVISIBLE);
    }

    public void updateTickingView(String targetName, String action, long timer){
        if(action.equals("finish")){
            DynamicSineWaveView wavesView = v.findViewById(R.id.SineWave);
            wavesView.stopAnimation();
            wavesView.setVisibility(View.INVISIBLE);

            long remain = 0;
            TextView txt = v.findViewById(R.id.Timer);
            txt.setText(Tool.formatTimeToString(remain));

            Button but_Accept =  v.findViewById(R.id.but_Accept);
            but_Accept.setVisibility(View.VISIBLE);

            mdbHandler.resetExpectedEnd(targetName);
        }else {
            TextView txt = v.findViewById(R.id.Timer);
            txt.setText(Tool.formatTimeToString(timer));
        }
    }

    public void createSineWave(DynamicSineWaveView wave){
        wave.setVisibility(View.INVISIBLE);
        wave.addWave(0.5f, 0.5f, 0, 0, 0); // Fist wave is for the shape of other waves.
        wave.addWave(0.5f, 2f, 0.5f, ContextCompat.getColor(c,android.R.color.holo_red_dark), 4);
        wave.addWave(0.1f, 2f, 0.7f, ContextCompat.getColor(c,android.R.color.holo_blue_dark), 4);
    }

    public void setView(View v){
        this.v = v;
    }
}
