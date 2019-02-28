package com.mobile.hinde.spacetime;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.mobile.hinde.utils.UserSettings;

import java.util.ArrayList;
import java.util.List;

public class Act_ImageList extends AppCompatActivity {

    private final FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private final FirebaseFirestore dbInstance = FirebaseFirestore.getInstance();

    private final UserSettings instance = UserSettings.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);


        dbInstance.collection("users").document(instance.getUserId()).collection("MOON").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                    }
                    TextView title = createTextView();
                    View line = createLine();

                    LinearLayout root = findViewById(R.id.rootLayer);

                    root.addView(title);
                    root.addView(line);

                } else {
                }
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onDestroy(){
        super.onDestroy();
    }

    private TextView createTextView(){
        TextView txt = new TextView(this);

        Typeface font = ResourcesCompat.getFont(this, R.font.digitaldream);
        txt.setTypeface(font);
        txt.setTextSize(20);
        txt.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        txt.setLayoutParams(params);

        return txt;
    }

    private View createLine(){
        View v = new View(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,2);
        v .setLayoutParams(params);
        v.setBackground(ContextCompat.getDrawable(this,R.drawable.bg_full));

        return v;
    }
}
