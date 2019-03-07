package com.mobile.hinde.spacetime

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mobile.hinde.utils.Constant
import com.mobile.hinde.utils.UserSettings

import java.util.HashMap

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

        if (UserSettings.money >= Constant.UNLOCK_FROM_NAME[mTarget]!!) {

            UserSettings.money = UserSettings.money - Constant.UNLOCK_FROM_NAME[mTarget]!!
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
    }

    private fun updateDoc(): Map<String, Any> {
        val data = HashMap<String, Any>()
        data[mTarget!! + "_unlock"] = "1"
        data["money"] = UserSettings.money
        return data
    }
}