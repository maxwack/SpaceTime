package com.mobile.hinde.spacetime

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.mobile.hinde.utils.Tools
import com.mobile.hinde.utils.UserSettings

class ActCommunicate : AppCompatActivity(), FragCommunicate.OnFragmentInteractionListener, FragLaunch.OnFragmentInteractionListener {

    private var mDisplay = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_communicate)

        val transaction = supportFragmentManager.beginTransaction()
        val mainFrag = FragMain()
        transaction.replace(R.id.root_frag, mainFrag)
        transaction.commit()

        val userTxt = findViewById<TextView>(R.id.userIdTxt)
        userTxt.text = intent.action
    }

    override fun onResume() {
        super.onResume()
        val moneyCount = findViewById<TextView>(R.id.moneyCount)
        moneyCount.text = Tools.formatMoneyCount(UserSettings.money)
    }

    fun showListImage() {

        if (mDisplay) {
            mDisplay = false
            val back = findViewById<ImageButton>(R.id.imageButton)
            back.setImageResource(android.R.drawable.ic_dialog_dialer)

            val transaction = supportFragmentManager.beginTransaction()
            val mainFrag = FragMain()
            transaction.replace(R.id.root_frag, mainFrag)
            transaction.commit()
        } else {
            mDisplay = true
            val back = findViewById<ImageButton>(R.id.imageButton)
            back.setImageResource(android.R.drawable.ic_media_play)

            val transaction = supportFragmentManager.beginTransaction()
            val imageListFrag = FragImageList()
            transaction.replace(R.id.root_frag, imageListFrag)
            transaction.commit()
        }

    }

    override fun onFragmentInteraction(uri: Uri) {
        //you can leave it empty
    }

    fun updateText() {
        val moneyCount = findViewById<TextView>(R.id.moneyCount)
        moneyCount.text = Tools.formatMoneyCount(UserSettings.money)
    }
}
