package com.mobile.hinde.utils;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.hinde.spacetime.Act_Full_Image;
import com.mobile.hinde.spacetime.R;

import java.util.ArrayList;

public class Grid_Adapter extends RecyclerView.Adapter<Grid_Adapter.MyViewHolder> {
    ArrayList<String> imgURL;
    Context context;

    public Grid_Adapter(Context context, ArrayList<String> imgURL) {
        this.context = context;
        this.imgURL = imgURL;
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
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // set the data in items
            holder.image.setImageDrawable(Tool.LoadImageFromWebOperations(imgURL.get(position)));
        // implement setOnClickListener event on item view.
            holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open another activity on item click
                Intent intent = new Intent(context, Act_Full_Image.class);
                intent.putExtra("image", imgURL.get(position)); // put image data in Intent
                context.startActivity(intent); // start Intent
            }
        });
    }

    @Override
    public int getItemCount() {
        return imgURL.size();
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