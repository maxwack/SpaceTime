package com.mobile.hinde.connection

import android.os.AsyncTask
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL


class ImageList(private val delegate : AsyncResponse): AsyncTask<String, Any, Any>(){

    override fun doInBackground(vararg params: String?): JSONObject {
        val apiResponse = URL("https://space-time-12b72.firebaseapp.com/%s/image_list.json".format(params[0])).readText()
        val jsonRes = JSONArray(apiResponse)
        val randomInteger = (0 until jsonRes.length()).shuffled().first()

        return jsonRes.getJSONObject(randomInteger)
    }

    override fun onPostExecute(result: Any) {
        delegate.processFinish(result)
    }
}