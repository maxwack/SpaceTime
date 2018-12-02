package com.mobile.hinde.spacetime;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.mobile.hinde.alarm.Alarm_receiver;
import com.mobile.hinde.view.DynamicSineWaveView;

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

    public void onClick(View v){
        Context context = getContext();
        Map<String, Object> data = new HashMap<>();
        switch (v.getId()) {
            case  R.id.but_Sun: {
                AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(context, Alarm_receiver.class);
                intent.setAction("callSun");
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

                alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() +
                                5 * 1000, alarmIntent);

                Button but_Sun = v.findViewById(R.id.but_Sun);
                but_Sun.setVisibility(View.INVISIBLE);

                DynamicSineWaveView wavesView = getView().findViewById(R.id.sunSineWave);
                wavesView.setVisibility(View.VISIBLE);
                wavesView.addWave(0.5f, 0.5f, 0, 0, 0); // Fist wave is for the shape of other waves.
                wavesView.addWave(0.5f, 2f, 0.5f, getResources().getColor(android.R.color.holo_red_dark), 0);
                wavesView.addWave(0.1f, 2f, 0.7f, getResources().getColor(android.R.color.holo_blue_dark), 0);
                wavesView.startAnimation();

                String ret = "";
                addMessage("SUN")
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    Exception e = task.getException();
                                    if (e instanceof FirebaseFunctionsException) {
                                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                        FirebaseFunctionsException.Code code = ffe.getCode();
                                        Object details = ffe.getDetails();
                                    }

                                    // [START_EXCLUDE]
                                    Log.w(TAG, "addMessage:onFailure", e);
                                    return;
                                    // [END_EXCLUDE]
                                }

                                // [START_EXCLUDE]
                                String result = task.getResult();
                                Toast.makeText(getActivity(), result,
                                        Toast.LENGTH_LONG).show();
                                // [END_EXCLUDE]
                            }
                        });
                break;
            }

            case R.id.but_Moon: {
                // do something for button 2 click
                break;
            }
        }

    }

    private Task<String> addMessage(String text) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("object", text);

        return mFunctions
                .getHttpsCallable("addMessage")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        HashMap<String,String> result = (HashMap<String,String>) task.getResult().getData();
                        return result.get("duration");
                    }
                });
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
