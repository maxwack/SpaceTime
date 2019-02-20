package com.mobile.hinde.spacetime;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.mobile.hinde.database.App_Settings;
import com.mobile.hinde.database.DBHandler;
import com.mobile.hinde.utils.Constant;

public class Act_user extends AppCompatActivity {

    private DBHandler mDBHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mDBHandler = new DBHandler(this);

        App_Settings ID_setting = mDBHandler.getUserId();

        if(ID_setting != null && !"".equals(ID_setting.getValue())){
            Intent i = new Intent(this, Act_Communicate.class);
            i.setAction(ID_setting.getValue());
            startActivity(i);
        }
    }

    public void registerUser(View v){
        EditText inputField = findViewById(R.id.inputField);
        if("".equals(inputField.getText().toString())){
            return;
        }
        mDBHandler.registerUser(inputField.getText().toString());
        Intent i = new Intent(this, Act_Communicate.class);
        i.setAction(inputField.getText().toString());
        startActivity(i);
    }

}
