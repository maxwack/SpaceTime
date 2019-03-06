package com.mobile.hinde.spacetime;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.mobile.hinde.utils.Constant;
import com.mobile.hinde.utils.UserSettings;

import java.util.HashMap;
import java.util.Map;

public class Frag_Unlock extends Fragment implements View.OnClickListener  {

    private String mTarget ="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_unlock,
                container, false);

        Bundle bundle = getArguments();
        mTarget = bundle.getString("target");

        TextView amountTxt = view.findViewById(R.id.txt_AMOUNT);
        amountTxt.setText(String.valueOf(Constant.UNLOCK_FROM_NAME.get(mTarget)));

        Button unlock_butt = view.findViewById(R.id.but_UNLOCK);
        unlock_butt.setOnClickListener(this);

        return view ;
    }

    public void onClick(final View v){

        if(UserSettings.getInstance().getMoney() >= Constant.UNLOCK_FROM_NAME.get(mTarget)){

            UserSettings.getInstance().setMoney(UserSettings.getInstance().getMoney() - Constant.UNLOCK_FROM_NAME.get(mTarget));
            ((Act_Communicate)getActivity()).updateText();

            FirebaseFirestore dbInstance = FirebaseFirestore.getInstance();
            DocumentReference docRef = dbInstance.collection("users").document(UserSettings.getInstance().getUserId());
            docRef.set(updateDoc(), SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Frag_Send_Accept acceptSendFrag = new Frag_Send_Accept();
                            Bundle bundle = new Bundle();
                            bundle.putString("target", mTarget);
                            acceptSendFrag.setArguments(bundle);
                            FragmentTransaction transaction = getParentFragment().getChildFragmentManager().beginTransaction();
                            transaction.addToBackStack(null);
                            int fragID = getResources().getIdentifier("frag_" + mTarget,"id", getActivity().getPackageName());
                            transaction.replace(fragID, acceptSendFrag, mTarget).commit();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("ERROR", "Error writing document", e);
                        }
                    });
        }
    }

    private Map<String, Object> updateDoc(){
        Map<String, Object> data = new HashMap<>();
        data.put(mTarget + "_unlock", "1");
        data.put("money", UserSettings.getInstance().getMoney());
        return data;
    }
}