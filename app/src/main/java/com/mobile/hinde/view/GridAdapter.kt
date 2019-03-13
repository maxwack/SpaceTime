package com.mobile.hinde.view

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.hinde.connection.AsyncResponse
import com.mobile.hinde.connection.ImageURL
import com.mobile.hinde.spacetime.ActImage
import com.mobile.hinde.spacetime.R
import java.io.ByteArrayOutputStream

class GridAdapter(private val context: Context, private val imgName: List<String>): RecyclerView.Adapter<GridAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val image:ImageView = itemView.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.grid_item,parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val dbInstance = FirebaseFirestore.getInstance()
        dbInstance.collection("images").document(imgName[position]).get()
                .addOnCompleteListener { task ->
            if(task.isSuccessful){
                val document = task.result!!

                if(context.fileList().contains(imgName[position])){
                    val bmpImage = BitmapFactory.decodeStream(context.openFileInput(imgName[position]))
                    holder.image.setImageBitmap(bmpImage)
                }else {
                    ImageURL(object : AsyncResponse {
                        override fun processFinish(output: Any) {
                            holder.image.setImageDrawable(output as Drawable)
                            context.openFileOutput(imgName[position], Context.MODE_PRIVATE).use {
                                val bitmap = (output as BitmapDrawable).bitmap
                                ByteArrayOutputStream().use{ byteS ->
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteS)
                                    it.write(byteS.toByteArray())
                                }
                            }
                        }
                    }).execute(document!!["URL"] as String)
                }

                holder.itemView.setOnClickListener {
                    val intent= Intent(context, ActImage::class.java)
                    intent.putExtra("name", imgName[position])
                    intent.putExtra("title", document["title"] as String)
                    intent.putExtra("legend", document["legend"] as String)
                    intent.putExtra("URL", document["URL"] as String)

                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return imgName.size
    }
}