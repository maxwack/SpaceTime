package com.mobile.hinde.spacetime;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobile.hinde.database.App_Settings;
import com.mobile.hinde.database.DBHandler;
import com.mobile.hinde.utils.UserSettings;

import java.util.HashMap;
import java.util.Map;

public class Act_user extends AppCompatActivity {

    private DBHandler mDBHandler;
    private FirebaseFirestore mFFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mDBHandler = new DBHandler(this);

        App_Settings ID_setting = mDBHandler.getUserId();

        mFFirestore = FirebaseFirestore.getInstance();

        if(ID_setting != null && !"".equals(ID_setting.getValue())){
            UserSettings.getInstance().setUserId(ID_setting.getValue());
            Intent i = new Intent(this, Act_Communicate.class);
            i.setAction(ID_setting.getValue());
            startActivity(i);
        }
    }

    public void registerUser(View v){
        final EditText inputField = findViewById(R.id.inputField);
        if("".equals(inputField.getText().toString())){
            return;
        }

        DocumentReference docRef = mFFirestore.collection("users").document(inputField.getText().toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    //value exists in Database
                    //Display error
                } else {
                    UserSettings.getInstance().setUserId(inputField.getText().toString());
                    UserSettings.getInstance().setMoney(0);
                    mDBHandler.registerUser(inputField.getText().toString());
                    createFireStoreDoc(inputField.getText().toString());
                    Intent i = new Intent(getApplicationContext(), Act_Communicate.class);
                    i.setAction(inputField.getText().toString());
                    startActivity(i);
                }
            }
        });
    }

    private void createFireStoreDoc(String userId){
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("money", 0);
        mFFirestore.collection("users").document(userId).set(data);
    }
}
