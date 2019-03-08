package com.mobile.hinde.spacetime

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.mobile.hinde.connection.AsyncResponse
import com.mobile.hinde.connection.ImageURL
import com.mobile.hinde.utils.Tools

class ActImage : AppCompatActivity() {

    private var mImage: ImageView? = null
    private var mLegend: TextView? = null
    private var mSave: Button? = null

    private var mImageName: String? = null
    private var mImageTitle: String? = null
    private var mImageLegend: String? = null
    private var mURL: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val mMaxWidth = (dm.widthPixels * 0.8).toInt()
        val mMaxHeight = (dm.heightPixels * 0.8).toInt()

        val styledAttributes = theme.obtainStyledAttributes(
                intArrayOf(android.R.attr.actionBarSize)
        )
        val actionBarHeight = styledAttributes.getDimension(0, 0f).toInt()
        styledAttributes.recycle()

        mLegend = findViewById(R.id.text_legend)
        mSave = findViewById(R.id.save)
        mImage = findViewById(R.id.image)


        val intent = intent
        mImageName = intent.extras!!.getString("name")
        mImageTitle = intent.extras!!.getString("title")
        mImageLegend = intent.extras!!.getString("legend")
        mURL = intent.extras!!.getString("URL")

        ImageURL(object : AsyncResponse {
            override fun processFinish(output: Any) {
                // Create a storage reference from our app
                if(output == null){
                    Tools.displayError(this@ActImage , resources.getString(R.string.error_title), resources.getString(R.string.error_image_not_found))
                }else {
                    mImage!!.setImageDrawable(output as Drawable)
                    val width = output.intrinsicWidth
                    val height = output.intrinsicHeight
                    if (width > 0) {
                        val ratio = mMaxWidth.toFloat() / width.toFloat()
                        val newHeight = (height * ratio).toInt() + mLegend!!.height + mSave!!.height + actionBarHeight
                        window.setLayout(mMaxWidth, newHeight)
                    }
                }
            }
        }).execute(mURL)

        supportActionBar!!.title = mImageTitle
        mLegend!!.text = mImageLegend
        window.setLayout(mMaxWidth, mMaxHeight)
    }

}
