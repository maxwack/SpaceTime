package com.mobile.hinde.spacetime;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Act_Image extends AppCompatActivity {

    private ImageView mImage;
    private TextView mLegend;
    private Button mSave;

    private String mImageName;
    private String mImageTitle;
    private String mImageLegend;

    private final FirebaseStorage mStorage = FirebaseStorage.getInstance();

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



        StorageReference storageRef = mStorage.getReference();
        StorageReference pathReference = storageRef.child(mImageName);

        final long ONE_MEGABYTE = 1024 * 1024;
        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                mImage.setImageBitmap(bmp);

                if(bmp.getWidth() != 0) {
                    float ratio = (float) mMaxWidth / (float) bmp.getWidth();
                    int newHeight = (int) (bmp.getHeight() * ratio) + mLegend.getHeight() + mSave.getHeight() + actionBarHeight;
                    getWindow().setLayout(mMaxWidth, newHeight);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

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
