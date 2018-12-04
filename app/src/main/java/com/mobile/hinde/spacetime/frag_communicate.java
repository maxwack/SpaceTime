package com.mobile.hinde.spacetime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.functions.FirebaseFunctions;
import com.mobile.hinde.alarm.Broadcast_Service;
import com.mobile.hinde.connection.AsyncResponse;
import com.mobile.hinde.connection.Duration_Site;
import com.mobile.hinde.utils.Tool;
import com.mobile.hinde.view.DynamicSineWaveView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link frag_communicate.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link frag_communicate#newInstance} factory method to
 * create an instance of this fragment.
 */
public class frag_communicate extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    private OnFragmentInteractionListener mListener;
    private IntentFilter mFilter = new IntentFilter();

    public frag_communicate() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment frag_communicate.
     */
    // TODO: Rename and change types and number of parameters
    public static frag_communicate newInstance(String param1, String param2) {
        frag_communicate fragment = new frag_communicate();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);


        }
        mFilter.addAction(Broadcast_Service.COUNTDOWN_TICK);
        mFilter.addAction(Broadcast_Service.COUNTDOWN_FINISH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.frag_communicate, container, false);

        Button mSunSend =  myView.findViewById(R.id.but_Sun);
        mSunSend.setOnClickListener(this);

        Button mMoonSend =  myView.findViewById(R.id.but_Moon);
        mMoonSend.setOnClickListener(this);

        DynamicSineWaveView mSineWave =  myView.findViewById(R.id.sunSineWave);
        mSineWave.setVisibility(View.INVISIBLE);


        // Inflate the layout for this fragment
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
        Map<String, Object> data = new HashMap<>();
        switch (v.getId()) {
            case  R.id.but_Sun: {
                Duration_Site asyncTask = (Duration_Site) new Duration_Site(new AsyncResponse(){

                    @Override
                    public void processFinish(String output){
                        Intent intent = new Intent(context,Broadcast_Service.class);
                        intent.setAction("callSun");
                        intent.putExtra("duration",30000l);
                        context.startService(intent);

                        Button but_Sun = v.findViewById(R.id.but_Sun);
                        but_Sun.setVisibility(View.INVISIBLE);

                        DynamicSineWaveView wavesView = getView().findViewById(R.id.sunSineWave);
                        wavesView.setVisibility(View.VISIBLE);
                        wavesView.addWave(0.5f, 0.5f, 0, 0, 0); // Fist wave is for the shape of other waves.
                        wavesView.addWave(0.5f, 2f, 0.5f, ContextCompat.getColor(context,android.R.color.holo_red_dark), 4);
                        wavesView.addWave(0.1f, 2f, 0.7f, ContextCompat.getColor(context,android.R.color.holo_blue_dark), 4);
                        wavesView.startAnimation();
                    }
                }).execute();
                break;
            }

            case R.id.but_Moon: {
                // do something for button 2 click
                break;
            }
        }

    }

    @Override
    public void onPause(){
        super.onPause();
        getContext().unregisterReceiver(br);
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
        getContext().stopService(new Intent(getContext(), Broadcast_Service.class));
        Log.i(TAG, "Stopped service");
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(br, mFilter);
        Log.i(TAG, "Registered broacast receiver");
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("finish")){
                DynamicSineWaveView wavesView = getView().findViewById(R.id.sunSineWave);
                wavesView.stopAnimation();
                wavesView.setVisibility(View.INVISIBLE);

                Button but_Sun =  getView().findViewById(R.id.but_Sun);
                but_Sun.setText("ACCEPT");
                but_Sun.setVisibility(View.VISIBLE);
            }else {
                long remain = intent.getExtras().getLong("countdown");
                TextView txt_Sun = getView().findViewById(R.id.sunTimer);
                txt_Sun.setText(Tool.formatTimeToString(remain));
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
