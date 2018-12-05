package com.mobile.hinde.spacetime;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobile.hinde.utils.Constant;

public class Act_Image extends Activity {

    private ImageView mDialog;
    private int mWidth = 0;
    private int mHeight = 0;

    private FirebaseStorage mStorage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        mWidth = (int)(dm.widthPixels * 0.8);
        mHeight = (int)(dm.heightPixels * 0.8);
        getWindow().setLayout(mWidth, mHeight);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Create a storage reference from our app
        StorageReference storageRef = mStorage.getReference();
        StorageReference pathReference = storageRef.child("20150420_active_regions_171.jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                mDialog = findViewById(R.id.image);
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                mDialog.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }

    public void onDestroy(){
        setResult(Constant.SUN_CODE);
        super.onDestroy();
    }
}
