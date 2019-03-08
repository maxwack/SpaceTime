package com.mobile.hinde.utils

import com.mobile.hinde.spacetime.R
import android.app.AlertDialog
import android.content.Context
import android.view.ContextThemeWrapper

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
           // Display the alert dialog on app interface
           dialog.show()
       }
    }
}