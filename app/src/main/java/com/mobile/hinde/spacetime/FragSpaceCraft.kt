package com.mobile.hinde.spacetime

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

class FragSpaceCraft : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.frag_spacecraft,
                container, false)

        val bundle = arguments
        val mCompany = bundle!!.getString("rocket")
        val mMission= bundle.getString("mission")
        val mDate = bundle.getString("date")

//        val linearLayout = view.findViewById<LinearLayout>(R.id.SCBackground)
//        linearLayout.background = resources.getDrawable(R.drawable.icon,"source name" );

        val textName = view.findViewById<TextView>(R.id.SCName)
        textName.text = mMission

        val textDate = view.findViewById<TextView>(R.id.SCDate)
        textDate.text = mDate

        return view
    }


}