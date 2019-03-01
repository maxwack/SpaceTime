package com.mobile.hinde.spacetime;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mobile.hinde.utils.Tool;
import com.mobile.hinde.utils.UserSettings;

public class Act_Communicate extends AppCompatActivity
        implements  Frag_Communicate.OnFragmentInteractionListener,
        frag_launch.OnFragmentInteractionListener{

    private boolean mDisplay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communicate);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Frag_Main mainFrag = new Frag_Main();
        transaction.replace(R.id.root_frag, mainFrag);
        transaction.commit();

        TextView userTxt = findViewById(R.id.userIdTxt);
        userTxt.setText(getIntent().getAction());
    }

    @Override
    protected void onResume(){
        super.onResume();
        TextView moneyCount = findViewById(R.id.moneyCount);
        moneyCount.setText(Tool.formatMoneyCount(UserSettings.getInstance().getMoney()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showListImage(View v){

        if(mDisplay){
            mDisplay = false;
            ImageButton back = findViewById(R.id.imageButton);
            back.setImageResource(android.R.drawable.ic_dialog_dialer);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Frag_Main mainFrag = new Frag_Main();
            transaction.replace(R.id.root_frag, mainFrag);
            transaction.commit();
        }else{
            mDisplay = true;
            ImageButton back = findViewById(R.id.imageButton);
            back.setImageResource(android.R.drawable.ic_media_play);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Frag_ImageList imageListFrag = new Frag_ImageList();
            transaction.replace(R.id.root_frag, imageListFrag);
            transaction.commit();
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }
}
