package com.mobile.hinde.spacetime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobile.hinde.alarm.Broadcast_Service;
import com.mobile.hinde.database.DBHandler;
import com.mobile.hinde.utils.Constant;
import com.mobile.hinde.utils.Tool;
import com.mobile.hinde.utils.UserSettings;

import java.util.ArrayList;
import java.util.Map;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Frag_Communicate.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Frag_Communicate extends Fragment {

    private OnFragmentInteractionListener mListener;
    private IntentFilter mFilter = new IntentFilter();
    private DBHandler mDBHandler;


    public Frag_Communicate() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFilter.addAction(Broadcast_Service.COUNTDOWN_TICK);
        mFilter.addAction(Broadcast_Service.COUNTDOWN_FINISH);

        mDBHandler = new DBHandler(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View myView = inflater.inflate(R.layout.frag_communicate, container, false);

        FirebaseFirestore dbInstance = FirebaseFirestore.getInstance();
        DocumentReference docRef = dbInstance.collection("users").document(UserSettings.getInstance().getUserId());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String,Object> userMap = document.getData();

                        UserSettings.getInstance().setMoney((long)userMap.get("money"));

                        TextView moneyCount = getActivity().findViewById(R.id.moneyCount);
                        moneyCount.setText(Tool.formatMoneyCount(UserSettings.getInstance().getMoney()));

                        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

                        Frag_Send_Accept acceptSendSunFrag = new Frag_Send_Accept();
                        Bundle bundleSun = new Bundle();
                        bundleSun.putString("target", Constant.SUN_NAME);
                        acceptSendSunFrag.setArguments(bundleSun);
                        transaction.replace(R.id.frag_SUN, acceptSendSunFrag, Constant.SUN_NAME);

                        Frag_Send_Accept acceptSendMoonFrag = new Frag_Send_Accept();
                        Bundle bundleMoon = new Bundle();
                        bundleMoon.putString("target", Constant.MOON_NAME);
                        acceptSendMoonFrag.setArguments(bundleMoon);
                        transaction.replace(R.id.frag_MOON, acceptSendMoonFrag, Constant.MOON_NAME);

                        if(userMap.containsKey(Constant.VOYAGER1_NAME)){
                            Frag_Send_Accept acceptSendVoy1Frag = new Frag_Send_Accept();
                            Bundle bundleVoy1 = new Bundle();
                            bundleVoy1.putString("target", Constant.VOYAGER1_NAME);
                            acceptSendVoy1Frag.setArguments(bundleVoy1);
                            transaction.replace(R.id.frag_VOYAGER1, acceptSendVoy1Frag, Constant.VOYAGER1_NAME);
                        }else{
                            Frag_Unlock unlockVoy1Frag = new Frag_Unlock();
                            Bundle bundleVoy1 = new Bundle();
                            bundleVoy1.putString("target", Constant.VOYAGER1_NAME);
                            unlockVoy1Frag.setArguments(bundleVoy1);
                            transaction.replace(R.id.frag_VOYAGER1, unlockVoy1Frag, Constant.VOYAGER1_NAME);
                        }

                        if(userMap.containsKey(Constant.INSIGHT_NAME)){
                            Frag_Send_Accept acceptSendInsFrag = new Frag_Send_Accept();
                            Bundle bundleIns = new Bundle();
                            bundleIns.putString("target", Constant.INSIGHT_NAME);
                            acceptSendInsFrag.setArguments(bundleIns);
                            transaction.replace(R.id.frag_INSIGHT, acceptSendInsFrag, Constant.INSIGHT_NAME);
                        }else{
                            Frag_Unlock unlockInsFrag = new Frag_Unlock();
                            Bundle bundleIns = new Bundle();
                            bundleIns.putString("target", Constant.INSIGHT_NAME);
                            unlockInsFrag.setArguments(bundleIns);
                            transaction.replace(R.id.frag_INSIGHT, unlockInsFrag, Constant.INSIGHT_NAME);
                        }

//                        transaction.addToBackStack(null);
                        transaction.commit();
                        getChildFragmentManager().executePendingTransactions();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        return myView;
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
            Intent intent = new Intent(Broadcast_Service.COUNTDOWN_FINISH);
            intent.putExtra("target",target);
            getContext().sendBroadcast(intent);
        }
        Log.i(TAG, "Registered broacast receiver");
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String target = (String)intent.getExtras().get("target");

            if(getChildFragmentManager().findFragmentByTag(target) != null){
                Frag_Send_Accept frag = (Frag_Send_Accept)getChildFragmentManager().findFragmentByTag(target);
                String action = intent.getAction();
                long remain = intent.getExtras().getLong("countdown");
                frag.updateTickingView(target, action,remain);
            }
        }
    };

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
