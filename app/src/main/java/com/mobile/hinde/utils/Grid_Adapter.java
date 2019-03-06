package com.mobile.hinde.utils;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobile.hinde.connection.AsyncResponse;
import com.mobile.hinde.connection.Image_URL;
import com.mobile.hinde.spacetime.Act_Image;
import com.mobile.hinde.spacetime.R;

import java.util.ArrayList;

public class Grid_Adapter extends RecyclerView.Adapter<Grid_Adapter.MyViewHolder> {
    ArrayList<String> imgName;
    Context context;

    public Grid_Adapter(Context context, ArrayList<String> imgName) {
        this.context = context;
        this.imgName = imgName;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        // set the view's size, margins, padding and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // set the data in items
        FirebaseFirestore dbInstance = FirebaseFirestore.getInstance();
        dbInstance.collection("images").document(imgName.get(position)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    new Image_URL(new AsyncResponse(){
                        @Override
                        public void processFinish(Object output) {        // Create a storage reference from our app
                            holder.image.setImageDrawable((Drawable) output);
                        }
                    }).execute((String)document.get("URL"));

                    // implement setOnClickListener event on item view.
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // open another activity on item click
                            Intent intent = new Intent(context, Act_Image.class);
                            intent.putExtra("name", imgName.get(position)); // put image data in Intent
                            intent.putExtra("title", (String)document.get("title")); // put image data in Intent
                            intent.putExtra("legend", (String)document.get("legend")); // put image data in Intent
                            context.startActivity(intent); // start Intent
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return imgName.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        ImageView image;
        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            image = itemView.findViewById(R.id.image);
        }
    }
}