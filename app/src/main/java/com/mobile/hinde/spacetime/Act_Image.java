package com.mobile.hinde.spacetime;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobile.hinde.connection.AsyncResponse;
import com.mobile.hinde.connection.Image_List;
import com.mobile.hinde.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

public class Act_Image extends Activity {

    private ImageView mDialog;

    private final FirebaseStorage mStorage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        int mWidth = 0;
        int mHeight = 0;

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        mWidth = (int)(dm.widthPixels * 0.8);
        mHeight = (int)(dm.heightPixels * 0.8);
        getWindow().setLayout(mWidth, mHeight);
        Intent intent = getIntent();
        Image_List asyncTask = (Image_List) new Image_List(new AsyncResponse(){

            @Override
            public void processFinish(JSONObject output) {        // Create a storage reference from our app
                try {
                    StorageReference storageRef = mStorage.getReference();
                    StorageReference pathReference = storageRef.child(output.getString("image"));
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

                    getActionBar().setTitle(output.getString("title"));
                    TextView legend = findViewById(R.id.text_legend);
                    legend.setText(output.getString("legend"));
                }catch(JSONException jsone){

                }
            }
        }).execute(intent.getExtras().getString("target"));

    }

    @Override
    public void onResume() {
        super.onResume();




    }

    public void onDestroy(){
        setResult(Constant.SUN_CODE);
        super.onDestroy();
    }
}
