package com.mobile.hinde.spacetime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.mobile.hinde.alarm.Broadcast_Service;
import com.mobile.hinde.database.DBHandler;
import com.mobile.hinde.utils.Comm_model;
import com.mobile.hinde.utils.Constant;
import com.mobile.hinde.utils.Tool;
import com.mobile.hinde.utils.UserSettings;
import com.mobile.hinde.view.DynamicSineWaveView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class Frag_Send_Accept extends Fragment implements View.OnClickListener  {
    private String mTarget;

    private IntentFilter mFilter = new IntentFilter();
    private DBHandler mDBHandler;
    private Comm_model model;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFilter.addAction(Broadcast_Service.COUNTDOWN_TICK);
        mFilter.addAction(Broadcast_Service.COUNTDOWN_FINISH);

        mDBHandler = new DBHandler(getContext());
        model = new Comm_model(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_send_accept,
                container, false);

        Bundle bundle = getArguments();
        mTarget = bundle.getString("target");
        model.setView(view);

        Button mSend =  view.findViewById(R.id.but_Send);
        mSend.setOnClickListener(this);
        model.createSineWave((DynamicSineWaveView) view.findViewById(R.id.SineWave));

        Button mAccept =  view.findViewById(R.id.but_Accept);
        mAccept.setOnClickListener(this);

        model.checkRemainingTimer(mTarget);
        return view ;
    }

    public void onClick(final View v){
        final Context context = getContext();
        Intent i ;
        switch (v.getId()) {
            case  R.id.but_Send:
                model.startSending(mTarget);
                break;
            case R.id.but_Accept:
                UserSettings.getInstance().setMoney(UserSettings.getInstance().getMoney() + Constant.MONEY_FROM_NAME.get(mTarget));

                FirebaseFirestore dbInstance = FirebaseFirestore.getInstance();
                DocumentReference docRef = dbInstance.collection("users").document(UserSettings.getInstance().getUserId());
                Map<String, Object> data = new HashMap<>();
                data.put("money", UserSettings.getInstance().getMoney());
                docRef.set(data, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                TextView moneyCount = getActivity().findViewById(R.id.moneyCount);
                                moneyCount.setText(Tool.formatMoneyCount(UserSettings.getInstance().getMoney()));
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("ERROR", "Error writing document", e);
                            }
                        });

                i = new Intent(context, Act_Image.class);
                i.putExtra("target", mTarget);
                startActivityForResult(i, Constant.CODE_FROM_NAME.get(mTarget));
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            model.resetMainView(Constant.NAME_FROM_CODE.get(requestCode));
        }catch(NullPointerException npe){

        }
    }

    @Override
    public void onPause(){
        super.onPause();
        try {
            getContext().unregisterReceiver(br);
        }catch(Exception e){
            //TODO
        }
        Log.i(TAG, "Unregistered broacast receiver");
    }

    @Override
    public void onStop() {
        try {
            getContext().unregisterReceiver(br);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        try {
            getContext().stopService(new Intent(getContext(), Broadcast_Service.class));
        }catch(Exception e){

        }
        Log.i(TAG, "Stopped service");
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(br, mFilter);
        long currTime = System.currentTimeMillis();
        ArrayList<String> targetList = mDBHandler.searchEndedData(currTime);

        for(String target : targetList){
            Intent intent = new Intent("finish");
            intent.putExtra("target",target);
            getContext().sendBroadcast(intent);
        }
        Log.i(TAG, "Registered broacast receiver");
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String target = (String)intent.getExtras().get("target");

            if(!mTarget.equals(target)){
                return;
            }

            String action = intent.getAction();
            long remain = intent.getExtras().getLong("countdown");
            model.updateTickingView(target, action, remain);
        }
    };

    public DBHandler getmDBHandler(){
        return mDBHandler;
    }

}
