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

import com.mobile.hinde.alarm.Broadcast_Service;
import com.mobile.hinde.connection.AsyncResponse;
import com.mobile.hinde.connection.Duration_Site;
import com.mobile.hinde.database.DBHandler;
import com.mobile.hinde.utils.Constant;
import com.mobile.hinde.utils.Tool;
import com.mobile.hinde.view.DynamicSineWaveView;

import org.json.JSONException;
import org.json.JSONObject;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myView = inflater.inflate(R.layout.frag_communicate, container, false);
        Context context = getContext();

        Button mSunSend =  myView.findViewById(R.id.but_Send_Sun);
        mSunSend.setOnClickListener(this);

        Button mMoonSend =  myView.findViewById(R.id.but_Send_Moon);
        mMoonSend.setOnClickListener(this);

        DynamicSineWaveView mSunSineWave =  myView.findViewById(R.id.sunSineWave);
        mSunSineWave.setVisibility(View.INVISIBLE);
        mSunSineWave.addWave(0.5f, 0.5f, 0, 0, 0); // Fist wave is for the shape of other waves.
        mSunSineWave.addWave(0.5f, 2f, 0.5f, ContextCompat.getColor(context,android.R.color.holo_red_dark), 4);
        mSunSineWave.addWave(0.1f, 2f, 0.7f, ContextCompat.getColor(context,android.R.color.holo_blue_dark), 4);

        DynamicSineWaveView mMoonSineWave =  myView.findViewById(R.id.moonSineWave);
        mMoonSineWave.setVisibility(View.INVISIBLE);
        mMoonSineWave.addWave(0.5f, 0.5f, 0, 0, 0); // Fist wave is for the shape of other waves.
        mMoonSineWave.addWave(0.5f, 2f, 0.5f, ContextCompat.getColor(context,android.R.color.holo_red_dark), 4);
        mMoonSineWave.addWave(0.1f, 2f, 0.7f, ContextCompat.getColor(context,android.R.color.holo_blue_dark), 4);

        Button mSunAccept =  myView.findViewById(R.id.but_Accept_Sun);
        mSunAccept.setOnClickListener(this);
        mSunAccept.setVisibility(View.INVISIBLE);

        Button mMoonAccept =  myView.findViewById(R.id.but_Accept_Moon);
        mMoonAccept.setOnClickListener(this);
        mMoonAccept.setVisibility(View.INVISIBLE);



        long currTime = System.currentTimeMillis();
        HashMap<String,Long> targetMap = mDBHandler.searchStartedData();
        for(Map.Entry<String, Long> entry : targetMap.entrySet()){
            if(currTime < entry.getValue()){
                switch(entry.getKey()){
                    case "SUN":
                        mSunSend.setVisibility(View.INVISIBLE);
                        mSunSineWave.setVisibility(View.VISIBLE);
                        mSunSineWave.startAnimation();
                        break;
                    case "MOON":
                        break;
                }
                long duration = entry.getValue() - currTime;
                Intent intent = new Intent(getContext(),Broadcast_Service.class);
                intent.setAction(entry.getKey());
                intent.putExtra("duration",duration);
                getContext().startService(intent);
            }else{
                Intent intent = new Intent("finish");
                intent.putExtra("target",entry.getKey());
                getContext().sendBroadcast(intent);
            }
        }

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
            case  R.id.but_Send_Sun: {
                Duration_Site asyncTask = (Duration_Site) new Duration_Site(new AsyncResponse(){

                    @Override
                    public void processFinish(JSONObject output){
                        try{
                            long duration = output.getLong("SUN");
                            Intent intent = new Intent(context,Broadcast_Service.class);
                            intent.setAction("SUN");
                            intent.putExtra("duration",duration * 2);
//                              intent.putExtra("duration",1000l);

                            context.startService(intent);

                            Button but_Send_Sun = v.findViewById(R.id.but_Send_Sun);
                            but_Send_Sun.setVisibility(View.INVISIBLE);

                            DynamicSineWaveView wavesView = getView().findViewById(R.id.sunSineWave);
                            wavesView.setVisibility(View.VISIBLE);
                            wavesView.startAnimation();

                            TextView txt_Sun = getView().findViewById(R.id.sunTimer);
                            txt_Sun.setVisibility(View.VISIBLE);
                            mDBHandler.updateData("SUN", System.currentTimeMillis(), System.currentTimeMillis()+ 2 * duration);
                        }
                        catch(NullPointerException | JSONException npe){

                        }
                    }
                }).execute("SUN");
                break;
            }
            case R.id.but_Accept_Sun:{
                Intent i = new Intent(context, Act_Image.class);
                i.putExtra("target", "SUN");
                startActivityForResult(i, Constant.SUN_CODE);
                break;
            }

            case R.id.but_Send_Moon: {
                Duration_Site asyncTask = (Duration_Site) new Duration_Site(new AsyncResponse(){

                    @Override
                    public void processFinish(JSONObject output){
                        try{
                            Intent intent = new Intent(context,Broadcast_Service.class);
                            intent.setAction("MOON");
                            intent.putExtra("duration",output.getLong("MOON"));
//                              intent.putExtra("duration",1000l);

                            context.startService(intent);

                            Button but_Send = v.findViewById(R.id.but_Send_Moon);
                            but_Send.setVisibility(View.INVISIBLE);

                            DynamicSineWaveView wavesView = getView().findViewById(R.id.moonSineWave);
                            wavesView.setVisibility(View.VISIBLE);
                            wavesView.startAnimation();

                            TextView txt = getView().findViewById(R.id.moonTimer);
                            txt.setVisibility(View.VISIBLE);
                        }
                        catch(NullPointerException npe){

                        }
                        catch(JSONException jsone){

                        }
                    }
                }).execute("MOON");
                break;
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            // check if the request code is same as what is passed  here it is 2
            switch (requestCode) {
                case Constant.SUN_CODE:
                    Button but_Accept_Sun = getView().findViewById(R.id.but_Accept_Sun);
                    but_Accept_Sun.setVisibility(View.INVISIBLE);
                    Button but_Send_Sun = getView().findViewById(R.id.but_Send_Sun);
                    but_Send_Sun.setVisibility(View.VISIBLE);

                    mDBHandler.resetExpectedEnd("SUN");

                    TextView txt_Sun = getView().findViewById(R.id.sunTimer);
                    txt_Sun.setVisibility(View.INVISIBLE);
                    break;
                case Constant.MOON_CODE:
                    Button but_Accept_Moon = getView().findViewById(R.id.but_Accept_Moon);
                    but_Accept_Moon.setVisibility(View.INVISIBLE);
                    Button but_Send_Moon = getView().findViewById(R.id.but_Send_Moon);
                    but_Send_Moon.setVisibility(View.VISIBLE);
                    TextView txt_Moon = getView().findViewById(R.id.moonTimer);
                    txt_Moon.setVisibility(View.INVISIBLE);
                    break;
            }
        }catch(NullPointerException npe){

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
            switch(target){
                case "SUN":
                    if(intent.getAction().equals("finish")){
                        DynamicSineWaveView wavesView = getView().findViewById(R.id.sunSineWave);
                        wavesView.stopAnimation();
                        wavesView.setVisibility(View.INVISIBLE);

                        long remain = 0;
                        TextView txt = getView().findViewById(R.id.sunTimer);
                        txt.setText(Tool.formatTimeToString(remain));

                        Button but_Accept =  getView().findViewById(R.id.but_Accept_Sun);
                        but_Accept.setVisibility(View.VISIBLE);

                        mDBHandler.resetExpectedEnd("SUN");
                    }else {
                        long remain = intent.getExtras().getLong("countdown");
                        TextView txt = getView().findViewById(R.id.sunTimer);
                        txt.setText(Tool.formatTimeToString(remain));
                    }
                    break;

                case "MOON":
                    if(intent.getAction().equals("finish")){
                        DynamicSineWaveView wavesView = getView().findViewById(R.id.moonSineWave);
                        wavesView.stopAnimation();
                        wavesView.setVisibility(View.INVISIBLE);

                        Button but_Accept =  getView().findViewById(R.id.but_Accept_Moon);
                        but_Accept.setVisibility(View.VISIBLE);
                    }else {
                        long remain = intent.getExtras().getLong("countdown");
                        TextView txt = getView().findViewById(R.id.moonTimer);
                        txt.setText(Tool.formatTimeToString(remain));
                    }
                    break;
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
