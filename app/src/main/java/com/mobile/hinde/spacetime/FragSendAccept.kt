package com.mobile.hinde.spacetime

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mobile.hinde.connection.AsyncResponse
import com.mobile.hinde.connection.ImageList
import com.mobile.hinde.database.DBHandler
import com.mobile.hinde.utils.CommModel
import com.mobile.hinde.utils.Constant
import com.mobile.hinde.utils.Tools
import com.mobile.hinde.utils.UserSettings
import com.mobile.hinde.view.DynamicSineWaveView

import org.json.JSONException
import org.json.JSONObject

import java.util.HashMap


class FragSendAccept : Fragment(), View.OnClickListener {
    private var mTarget: String? = null

    var mDBHandler: DBHandler? = null

    private var model: CommModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDBHandler = DBHandler(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.frag_send_accept,
                container, false)

        val bundle = arguments
        mTarget = bundle!!.getString("target")

        model = CommModel(this, mTarget!!)
        model!!.v = view

        val mSend = view.findViewById<Button>(R.id.but_Send)
        mSend.setOnClickListener(this)
        model!!.createSineWave(view.findViewById<View>(R.id.SineWave) as DynamicSineWaveView)

        val mAccept = view.findViewById<Button>(R.id.but_Accept)
        mAccept.setOnClickListener(this)
        model!!.checkRemainingTime()
        return view
    }

    override fun onClick(v: View) {
        val context = context
        when (v.id) {
            R.id.but_Send -> model!!.startSending()
            R.id.but_Accept -> {
                UserSettings.money = UserSettings.money + Constant.MONEY_FROM_NAME[mTarget]!!

                val dbInstance = FirebaseFirestore.getInstance()
                val docRef = dbInstance.collection("users").document(UserSettings.userId)
                val data = HashMap<String, Any>()
                data["money"] = UserSettings.money
                docRef.set(data, SetOptions.merge())
                        .addOnSuccessListener {
                            val moneyCount = activity!!.findViewById<TextView>(R.id.moneyCount)
                            moneyCount.text = Tools.formatMoneyCount(UserSettings.money)
                        }
                        .addOnFailureListener { e -> Log.w("ERROR", "Error writing document", e) }

                ImageList(object : AsyncResponse {
                    override fun processFinish(output: Any) {
                        // Create a storage reference from our app
                        try {
                            val result = output as JSONObject
                            addImageToUserList(result.getString("image"))
                            val i = Intent(context, ActImage::class.java)
                            i.putExtra("name", result.getString("image"))
                            i.putExtra("title", result.getString("title"))
                            i.putExtra("legend", result.getString("legend"))
                            i.putExtra("URL", result.getString("URL"))
                            startActivityForResult(i, Constant.CODE_FROM_NAME[mTarget]!!)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }).execute(mTarget)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)
            model!!.resetMainView()
        } catch (npe: NullPointerException) {

        }

    }

    fun updateTickingView(action: String, timer: Long) {
        model!!.updateTickingView(action, timer)
    }


    private fun addImageToUserList(imageName: String) {
        val dbInstance = FirebaseFirestore.getInstance()
        val docRef = dbInstance.collection("users").document(UserSettings.userId)
        docRef.update(mTarget!!, FieldValue.arrayUnion(imageName))
    }
}
