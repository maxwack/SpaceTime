package com.mobile.hinde.spacetime;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobile.hinde.utils.Grid_Adapter;
import com.mobile.hinde.utils.UserSettings;

import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        dbInstance.collection("users").document(instance.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String,Object> userMap = document.getData();

                    // Create LinearLayout
                    final LinearLayout ll = new LinearLayout(getActivity());
                    ll.setOrientation(LinearLayout.VERTICAL);
                    TextView title = createTextView();
                    View line = createLine();



                    RecyclerView rv = createRecycler((ArrayList<String>) userMap.get("MOON"));

                    LinearLayout root = getView().findViewById(R.id.rootLayer);

                    ll.addView(title);
                    ll.addView(line);
                    ll.addView(rv);

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
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,5);
        v .setLayoutParams(params);
        v.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorPrimary));
        return v;
    }

    private RecyclerView createRecycler(ArrayList<String> imgList){
        final RecyclerView rv = new RecyclerView(getActivity());
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,RecyclerView.LayoutParams.MATCH_PARENT);
        rv.setLayoutParams(params);

        Grid_Adapter customAdapter = new Grid_Adapter(getActivity(), imgList);
        rv.setAdapter(customAdapter); // set the Adapter to RecyclerView
        // set a GridLayoutManager with 3 number of columns , horizontal gravity and false value for reverseLayout to show the items from start to end
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(),2, LinearLayoutManager.HORIZONTAL,false);
        rv.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        return rv;
    }
}
