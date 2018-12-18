package com.mobile.hinde.connection;

import android.os.AsyncTask;

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
import java.util.Calendar;

public class Duration_Site extends AsyncTask<String, Void, JSONObject> {

    private AsyncResponse delegate;

    public Duration_Site(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected JSONObject doInBackground(String... target) {
        StringBuilder result = new StringBuilder();
        HttpURLConnection urlConnection = null;
        JSONObject json_Res = null;
        try {
            Calendar rightNow = Calendar.getInstance();
            String date = rightNow.get(Calendar.YEAR) + "-" + String.format("%02d",rightNow.get(Calendar.MONTH) + 1 ) + "-" + String.format("%02d",rightNow.get(Calendar.DAY_OF_MONTH) );
            URL url = new URL("https://space-time-12b72.firebaseapp.com/" + date + ".json");
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = reader.readLine()) != null) {
                    result.append(line);
            }

            json_Res = new JSONObject(result.toString());

        }catch(MalformedURLException | JSONException mue){

        }catch(IOException ioe){

        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return json_Res;

    }

    @Override
    protected void onPostExecute(JSONObject result) {
        delegate.processFinish(result);
    }
}
