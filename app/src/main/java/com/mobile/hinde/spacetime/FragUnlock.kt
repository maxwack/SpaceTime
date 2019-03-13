package com.mobile.hinde.spacetime

import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mobile.hinde.utils.Constant
import com.mobile.hinde.utils.Tools
import com.mobile.hinde.utils.UserSettings

import java.util.HashMap
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener



class FragUnlock : Fragment(), View.OnClickListener {

    private var mTarget: String? = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.frag_unlock,
                container, false)

        val bundle = arguments
        mTarget = bundle!!.getString("target")

        val amountTxt = view.findViewById<TextView>(R.id.txt_AMOUNT)
        amountTxt.text = Constant.UNLOCK_FROM_NAME[mTarget!!].toString()

        val unlockButt = view.findViewById<Button>(R.id.but_UNLOCK)
        unlockButt.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View) {

        // Initialize a new instance of
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))

        // Set the alert dialog title
        builder.setTitle("Unlock path to ${mTarget!!.capitalize()}")

        // Display a message on alert dialog
        builder.setMessage("How do you want to unlock it ? ")

        // Set a positive button and its click listener on alert dialog
        builder.setNegativeButton("BUY \n 5$"){_,_ ->
            //TODO start shop activity + unlock
        }

        builder.setIcon(context?.getDrawable(R.drawable.coin))

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("COINS \n ${Constant.UNLOCK_FROM_NAME[mTarget!!]!!}"){_,_ ->
            UserSettings.money = UserSettings.money - Constant.UNLOCK_FROM_NAME[mTarget!!]!!
            (activity as ActCommunicate).updateText()

            val dbInstance = FirebaseFirestore.getInstance()
            val docRef = dbInstance.collection("users").document(UserSettings.userId)
            docRef.set(updateDoc(), SetOptions.merge())
                    .addOnSuccessListener {
                        val acceptSendFrag = FragSendAccept()
                        val bundle = Bundle()
                        bundle.putString("target", mTarget)
                        acceptSendFrag.arguments = bundle
                        val transaction = parentFragment!!.childFragmentManager.beginTransaction()
                        transaction.addToBackStack(null)
                        val fragID = resources.getIdentifier("frag_" + mTarget!!, "id", activity!!.packageName)
                        transaction.replace(fragID, acceptSendFrag, mTarget).commit()
                    }
                    .addOnFailureListener { e -> Log.w("ERROR", "Error writing document", e) }
        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()
        dialog.window!!.setBackgroundDrawableResource(R.drawable.but_border)

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = UserSettings.money > Constant.UNLOCK_FROM_NAME[mTarget!!]!!
        }
        // Display the alert dialog on app interface
        dialog.show()
    }

    private fun updateDoc(): Map<String, Any> {
        val data = HashMap<String, Any>()
        data[mTarget!! + "_unlock"] = "1"
        data["money"] = UserSettings.money
        return data
    }
}