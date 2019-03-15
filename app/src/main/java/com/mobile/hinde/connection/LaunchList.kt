package com.mobile.hinde.connection

import android.os.AsyncTask
import org.json.JSONArray
import java.net.URL

class LaunchList(private val delegate : AsyncResponse): AsyncTask<Any, Any, Any>() {
    override fun doInBackground(vararg params: Any?): JSONArray {
        val apiResponse = URL("https://space-time-12b72.firebaseapp.com/launch_list.json").readText()
        return JSONArray(apiResponse)
    }

    override fun onPostExecute(result: Any) {
        delegate.processFinish(result)
    }
}