package com.mobile.hinde.utils

import com.mobile.hinde.spacetime.R
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Environment
import android.view.ContextThemeWrapper
import android.widget.LinearLayout


class Tools{
    companion object {
       fun formatTimeToString(time:Long):String{
           val milliTime = time/1000
           return "%02d:%02d:%02d".format(milliTime / 3600, (milliTime % 3600) / 60, (milliTime % 60))
       }

       fun formatMoneyCount(amount:Long):String{
           return "%04d".format(amount)
       }

       fun displayError(context: Context, title:String, message:String){
           // Initialize a new instance of
           val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))

           // Set the alert dialog title
           builder.setTitle(title)

           // Display a message on alert dialog
           builder.setMessage(message)

           // Set a positive button and its click listener on alert dialog
           builder.setPositiveButton("OK"){_,_ ->
           }

           // Finally, make the alert dialog using builder
           val dialog: AlertDialog = builder.create()
           dialog.window!!.setBackgroundDrawableResource(R.drawable.but_border)
           // Display the alert dialog on app interface
           dialog.show()
       }

        fun displayShopMessage(context: Context, title:String, message:String){
            // Initialize a new instance of
            val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))

            // Set the alert dialog title
            builder.setTitle(title)

            // Display a message on alert dialog
            builder.setMessage(message)

            // Set a positive button and its click listener on alert dialog
            builder.setNegativeButton("OK"){_,_ ->
            }

            builder.setIcon(context.getDrawable(R.drawable.coin))

            // Set a positive button and its click listener on alert dialog
            builder.setPositiveButton("BUY"){_,_ ->
                //TODO Open shop activity
            }

            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()
            dialog.window!!.setBackgroundDrawableResource(R.drawable.but_border)
            // Display the alert dialog on app interface
            dialog.show()
        }

        /* Checks if external storage is available for read and write */
        fun isExternalStorageWritable(): Boolean {
            return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        }

        /* Checks if external storage is available to at least read */
        fun isExternalStorageReadable(): Boolean {
            return Environment.getExternalStorageState() in
                    setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
        }
    }
}