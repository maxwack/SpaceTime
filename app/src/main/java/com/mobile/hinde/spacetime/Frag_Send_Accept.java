package com.mobile.hinde.spacetime;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobile.hinde.connection.AsyncResponse;
import com.mobile.hinde.connection.Image_List;
import com.mobile.hinde.database.DBHandler;
import com.mobile.hinde.utils.Comm_model;
import com.mobile.hinde.utils.Constant;
import com.mobile.hinde.utils.Tool;
import com.mobile.hinde.utils.UserSettings;
import com.mobile.hinde.view.DynamicSineWaveView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Frag_Send_Accept extends Fragment implements View.OnClickListener  {
    private String mTarget;

    private DBHandler mDBHandler;

    private Comm_model model;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

                new Image_List(new AsyncResponse(){
                    @Override
                    public void processFinish(Object output) {        // Create a storage reference from our app
                        try {
                            JSONObject result = (JSONObject)output;
                            addImageToUserList(result.getString("image"));
                            Intent i = new Intent(context, Act_Image.class);
                            i.putExtra("name", result.getString("image"));
                            i.putExtra("title", result.getString("title"));
                            i.putExtra("legend", result.getString("legend"));
                            i.putExtra("URL", result.getString("URL"));
                            startActivityForResult(i, Constant.CODE_FROM_NAME.get(mTarget));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).execute(mTarget);
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

    public DBHandler getmDBHandler(){
        return mDBHandler;
    }


    public void updateTickingView(String targetName, String action, long timer){
        model.updateTickingView(targetName,action,timer);
    }


    public void addImageToUserList(String imageName){
        FirebaseFirestore dbInstance = FirebaseFirestore.getInstance();
        DocumentReference docRef = dbInstance.collection("users").document(UserSettings.getInstance().getUserId());
        docRef.update(mTarget, FieldValue.arrayUnion(imageName));
    }
}
