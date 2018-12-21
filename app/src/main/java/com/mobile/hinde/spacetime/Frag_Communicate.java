package com.mobile.hinde.spacetime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mobile.hinde.alarm.Broadcast_Service;
import com.mobile.hinde.database.DBHandler;
import com.mobile.hinde.utils.Comm_model;
import com.mobile.hinde.utils.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Frag_Communicate.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Frag_Communicate#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_Communicate extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private IntentFilter mFilter = new IntentFilter();
    private DBHandler mDBHandler;
    private Comm_model model;

    public Frag_Communicate() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frag_Communicate.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_Communicate newInstance(String param1, String param2) {
        Frag_Communicate fragment = new Frag_Communicate();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

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

        View myView = inflater.inflate(R.layout.frag_communicate, container, false);
        model.setView(myView);

        Button mSunSend =  myView.findViewById(R.id.but_Send_SUN);
        mSunSend.setOnClickListener(this);

        Button mMoonSend =  myView.findViewById(R.id.but_Send_MOON);
        mMoonSend.setOnClickListener(this);

        Button mVoy1Send =  myView.findViewById(R.id.but_Send_VOYAGER1);
        mVoy1Send.setOnClickListener(this);

        model.createSineWave(R.id.SUN_SineWave);
        model.createSineWave(R.id.MOON_SineWave);
        model.createSineWave(R.id.VOYAGER1_SineWave);

        Button mSunAccept =  myView.findViewById(R.id.but_Accept_SUN);
        mSunAccept.setOnClickListener(this);
        mSunAccept.setVisibility(View.INVISIBLE);

        Button mMoonAccept =  myView.findViewById(R.id.but_Accept_MOON);
        mMoonAccept.setOnClickListener(this);
        mMoonAccept.setVisibility(View.INVISIBLE);

        Button mVoy1Accept =  myView.findViewById(R.id.but_Accept_VOYAGER1);
        mVoy1Accept.setOnClickListener(this);
        mVoy1Accept.setVisibility(View.INVISIBLE);


        model.checkRemainingTimer();

        return myView;
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

    public void onClick(final View v){
        final Context context = getContext();
        Intent i ;
        switch (v.getId()) {
            case  R.id.but_Send_SUN:
                model.startSending(Constant.SUN_NAME);
                break;
            case R.id.but_Accept_SUN:
                i = new Intent(context, Act_Image.class);
                i.putExtra("target", Constant.SUN_NAME);
                startActivityForResult(i, Constant.SUN_CODE);
                break;
            case R.id.but_Send_MOON:
                model.startSending(Constant.MOON_NAME);
                break;
            case R.id.but_Accept_MOON:
                i = new Intent(context, Act_Image.class);
                i.putExtra("target", Constant.MOON_NAME);
                startActivityForResult(i, Constant.MOON_CODE);
            break;
            case R.id.but_Send_VOYAGER1:
                model.startSending(Constant.VOYAGER1_NAME);
                break;
            case R.id.but_Accept_VOYAGER1:
                i = new Intent(context, Act_Image.class);
                i.putExtra("target", Constant.VOYAGER1_NAME);
                startActivityForResult(i, Constant.VOYAGER1_CODE);
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
            String action = intent.getAction();
            long remain = intent.getExtras().getLong("countdown");
            model.updateTickingView(target, action, remain);
        }
    };

    public DBHandler getmDBHandler(){
        return mDBHandler;
    }

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
