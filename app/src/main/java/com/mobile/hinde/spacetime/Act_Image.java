package com.mobile.hinde.spacetime;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobile.hinde.connection.AsyncResponse;
import com.mobile.hinde.connection.Image_List;
import com.mobile.hinde.connection.Image_URL;
import com.mobile.hinde.utils.Tool;
import com.mobile.hinde.utils.UserSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Act_Image extends AppCompatActivity {

    private ImageView mImage;
    private TextView mLegend;
    private Button mSave;

    private String mImageName;
    private String mImageTitle;
    private String mImageLegend;
    private String mURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        final int mMaxWidth = (int)(dm.widthPixels * 0.8);
        final int mMaxHeight = (int)(dm.heightPixels * 0.8);

        final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize }
        );
        final int actionBarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        mLegend = findViewById(R.id.text_legend);
        mSave = findViewById(R.id.save);
        mImage = findViewById(R.id.image);


        Intent intent = getIntent();
        mImageName = intent.getExtras().getString("name");
        mImageTitle = intent.getExtras().getString("title");
        mImageLegend = intent.getExtras().getString("legend");
        mURL = intent.getExtras().getString("URL");

        new Image_URL(new AsyncResponse(){
            @Override
            public void processFinish(Object output) {        // Create a storage reference from our app
                mImage.setImageDrawable((Drawable) output);
                int width = ((Drawable)output).getIntrinsicWidth();
                int height = ((Drawable)output).getIntrinsicHeight();
                if(width > 0) {
                    float ratio = (float) mMaxWidth / (float) width;
                    int newHeight = (int) (height * ratio) + mLegend.getHeight() + mSave.getHeight() + actionBarHeight;
                    getWindow().setLayout(mMaxWidth, newHeight);
                }
            }
        }).execute(mURL);

        getSupportActionBar().setTitle(mImageTitle);
        mLegend.setText(mImageLegend);
        getWindow().setLayout(mMaxWidth, mMaxHeight);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onDestroy(){
        super.onDestroy();
    }

}
