package com.mobile.hinde.spacetime

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.EditText

import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.hinde.database.DBHandler
import com.mobile.hinde.utils.UserSettings

import java.util.HashMap

class ActUser : AppCompatActivity() {

    private var mDBHandler: DBHandler? = null
    private var mFFirestore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        mDBHandler = DBHandler(this)

        val id_setting = mDBHandler!!.getUserId()

        mFFirestore = FirebaseFirestore.getInstance()

        if (id_setting != null && "" != id_setting.value) {
            UserSettings.userId = id_setting.value
            val i = Intent(this, ActCommunicate::class.java)
            i.action = id_setting.value
            startActivity(i)
        }
    }

    fun registerUser() {
        val inputField = findViewById<EditText>(R.id.inputField)
        if ("" == inputField.text.toString()) {
            return
        }

        val docRef = mFFirestore!!.collection("users").document(inputField.text.toString())
        docRef.get().addOnCompleteListener { task ->
            if (task.result!!.exists()) {
                //value exists in Database
                //Display error
            } else {
                UserSettings.userId = inputField.text.toString()
                UserSettings.money = 0
                mDBHandler!!.registerUser(inputField.text.toString())
                createFireStoreDoc(inputField.text.toString())
                val i = Intent(applicationContext, ActCommunicate::class.java)
                i.action = inputField.text.toString()
                startActivity(i)
            }
        }
    }

    private fun createFireStoreDoc(userId: String) {
        val data = HashMap<String, Any>()
        data["userId"] = userId
        data["money"] = 0
        mFFirestore!!.collection("users").document(userId).set(data)
    }
}
