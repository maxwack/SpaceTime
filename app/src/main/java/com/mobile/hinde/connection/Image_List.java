package com.mobile.hinde.connection;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class Image_List extends AsyncTask<String, Void, JSONObject> {

    private AsyncResponse delegate;

    public Image_List(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected JSONObject doInBackground(String... target) {
        StringBuilder result = new StringBuilder();
        HttpURLConnection urlConnection = null;

        JSONObject res = null;

        try {
            URL url = new URL("https://space-time-12b72.firebaseapp.com/" + target[0] +"/image_list.json");
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = reader.readLine()) != null) {
                result.append(line);
            }

            JSONArray json_Res = new JSONArray(result.toString());
            int maxVal = json_Res.length();
            int random = new Random().nextInt(maxVal);
            res = json_Res.getJSONObject(0);




        } catch(IOException ioe){

        }catch(JSONException jsone){

        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return res;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        delegate.processFinish(result);
    }
}
