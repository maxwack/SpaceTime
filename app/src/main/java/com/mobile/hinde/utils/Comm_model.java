package com.mobile.hinde.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mobile.hinde.alarm.Broadcast_Service;
import com.mobile.hinde.connection.AsyncResponse;
import com.mobile.hinde.connection.Duration_Site;
import com.mobile.hinde.database.DBHandler;
import com.mobile.hinde.spacetime.Frag_Communicate;
import com.mobile.hinde.view.DynamicSineWaveView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Comm_model {

    private Frag_Communicate controller;
    private Context c;
    private View v;
    private DBHandler mdbHandler;

    public Comm_model(Frag_Communicate controller){
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

                    String id = "but_Send_" + targetName;
                    int obj_ID = controller.getResources().getIdentifier(id, "id",c.getPackageName());
                    Button but_Send_Sun = v.findViewById(obj_ID);
                    but_Send_Sun.setVisibility(View.INVISIBLE);

                    id = targetName + "_SineWave";
                    obj_ID = controller.getResources().getIdentifier(id, "id", c.getPackageName());
                    DynamicSineWaveView wavesView = v.findViewById(obj_ID);
                    wavesView.setVisibility(View.VISIBLE);
                    wavesView.startAnimation();

                    id = targetName + "_Timer";
                    obj_ID = controller.getResources().getIdentifier(id, "id", c.getPackageName());
                    TextView txt_Sun = v.findViewById(obj_ID);
                    txt_Sun.setVisibility(View.VISIBLE);
                    mdbHandler.updateData(targetName, System.currentTimeMillis(), System.currentTimeMillis()+ 2 * duration);
                }
                catch(NullPointerException | JSONException npe){

                }
            }
        }).execute(targetName);
    }

    public void checkRemainingTimer(){
        long currTime = System.currentTimeMillis();
        HashMap<String,Long> targetMap = mdbHandler.searchStartedData();
        for(Map.Entry<String, Long> entry : targetMap.entrySet()){
            if(currTime < entry.getValue()){

                String id = "but_Send_" + entry.getKey();
                int obj_ID = controller.getResources().getIdentifier(id, "id",c.getPackageName());
                Button but_send_Sun = v.findViewById(obj_ID);
                but_send_Sun.setVisibility(View.INVISIBLE);

                id = entry.getKey() + "_SineWave";
                obj_ID = controller.getResources().getIdentifier(id, "id", c.getPackageName());
                DynamicSineWaveView wavesView = v.findViewById(obj_ID);
                wavesView.setVisibility(View.VISIBLE);
                wavesView.startAnimation();

                long duration = entry.getValue() - currTime;
                Intent intent = new Intent(c,Broadcast_Service.class);
                intent.setAction(entry.getKey());
                intent.putExtra("duration",duration);
                c.startService(intent);
            }else{
                Intent intent = new Intent("finish");
                intent.putExtra("target",entry.getKey());
                c.sendBroadcast(intent);
            }
        }
    }

    public void resetMainView(String targetName){

        String id = "but_Accept_" + targetName;
        int obj_ID = c.getResources().getIdentifier(id, "id",c.getPackageName());
        Button but_Accept_Sun = v.findViewById(obj_ID);
        but_Accept_Sun.setVisibility(View.INVISIBLE);

        id = "but_Send_" + targetName;
        obj_ID = controller.getResources().getIdentifier(id, "id",c.getPackageName());
        Button but_Send_Sun = v.findViewById(obj_ID);
        but_Send_Sun.setVisibility(View.VISIBLE);

        mdbHandler.resetExpectedEnd(Constant.SUN_NAME);

        id = targetName + "_Timer";
        obj_ID = controller.getResources().getIdentifier(id, "id",c.getPackageName());
        TextView txt_Sun = v.findViewById(obj_ID);
        txt_Sun.setVisibility(View.INVISIBLE);
    }

    public void updateTickingView(String targetName, String action, long timer){
        if(action.equals("finish")){

            String id = targetName + "_SineWave";
            int obj_ID = c.getResources().getIdentifier(id, "id",c.getPackageName());
            DynamicSineWaveView wavesView = v.findViewById(obj_ID);
            wavesView.stopAnimation();
            wavesView.setVisibility(View.INVISIBLE);

            long remain = 0;
            id = targetName + "_Timer";
            obj_ID = c.getResources().getIdentifier(id, "id",c.getPackageName());
            TextView txt = v.findViewById(obj_ID);
            txt.setText(Tool.formatTimeToString(remain));

            id = "but_Accept_" + targetName;
            obj_ID = c.getResources().getIdentifier(id, "id",c.getPackageName());
            Button but_Accept =  v.findViewById(obj_ID);
            but_Accept.setVisibility(View.VISIBLE);

            mdbHandler.resetExpectedEnd("SUN");
        }else {
            String id = targetName + "_Timer";
            int obj_ID = c.getResources().getIdentifier(id, "id",c.getPackageName());
            TextView txt = v.findViewById(obj_ID);
            txt.setText(Tool.formatTimeToString(timer));
        }
    }

    public void createSineWave(int id){
        DynamicSineWaveView mSineWave =  v.findViewById(id);
        mSineWave.setVisibility(View.INVISIBLE);
        mSineWave.addWave(0.5f, 0.5f, 0, 0, 0); // Fist wave is for the shape of other waves.
        mSineWave.addWave(0.5f, 2f, 0.5f, ContextCompat.getColor(c,android.R.color.holo_red_dark), 4);
        mSineWave.addWave(0.1f, 2f, 0.7f, ContextCompat.getColor(c,android.R.color.holo_blue_dark), 4);
    }

    public void setView(View v){
        this.v = v;
    }
}
