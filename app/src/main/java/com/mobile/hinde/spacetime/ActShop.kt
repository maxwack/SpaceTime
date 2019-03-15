package com.mobile.hinde.spacetime

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.View
import android.util.TypedValue



class ActShop: AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val mMaxWidth = (dm.widthPixels * 0.9).toInt()

        val styledAttributes = theme.obtainStyledAttributes(
                intArrayOf(android.R.attr.actionBarSize)
        )
        val actionBarHeight = styledAttributes.getDimension(0, 0f).toInt()
        val px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                80f,
                dm
        )

        val mMaxHeight = actionBarHeight + px.toInt() * 4 +20

        window.setLayout(mMaxWidth, mMaxHeight)

    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}