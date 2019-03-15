package com.mobile.hinde.connection

import android.graphics.drawable.Drawable
import android.os.AsyncTask
import java.io.InputStream
import java.net.URL

class ImageURL(private val delegate: AsyncResponse): AsyncTask<String, Any, Any>(){

    override fun doInBackground(vararg params: String?): Drawable? {
        val stream = URL(params[0]).content
        if(stream is InputStream){
            return Drawable.createFromStream(stream,"source")
        }
        return null
    }

    override fun onPostExecute(result: Any) {
        delegate.processFinish(result)
    }

}