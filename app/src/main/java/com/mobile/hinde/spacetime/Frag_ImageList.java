package com.mobile.hinde.spacetime;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.mobile.hinde.utils.UserSettings;
import com.mobile.hinde.view.Line;

import java.util.ArrayList;
import java.util.List;

public class Frag_ImageList extends Fragment {

    private final FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private final FirebaseFirestore dbInstance = FirebaseFirestore.getInstance();

    private final UserSettings instance = UserSettings.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_image_list,
                container, false);

        dbInstance.collection("users").document(instance.getUserId()).collection("MOON").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                    }

                    // Create LinearLayout
                    LinearLayout ll = new LinearLayout(getActivity());
                    ll.setOrientation(LinearLayout.HORIZONTAL);
                    TextView title = createTextView();
                    Line line = new Line(getActivity());
                    line.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));

                    LinearLayout root = getView().findViewById(R.id.rootLayer);

                    ll.addView(title);
                    ll.addView(line);

                    root.addView(ll);

                } else {
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onDestroy(){
        super.onDestroy();
    }

    private TextView createTextView(){
        TextView txt = new TextView(getActivity());

        Typeface font = ResourcesCompat.getFont(getActivity(), R.font.digitaldream);
        txt.setTypeface(font);
        txt.setTextSize(20);
        txt.setText("MOON");
        txt.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        txt.setLayoutParams(params);

        return txt;
    }

    private View createLine(){
        View v = new View(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,2);
        v .setLayoutParams(params);
        v.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
        return v;
    }
}
