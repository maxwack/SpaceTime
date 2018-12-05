package com.mobile.hinde.connection;

import android.os.AsyncTask;
import android.text.Html;

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

public class Duration_Site extends AsyncTask<String, Void, Long> {

    public AsyncResponse delegate = null;

    public Duration_Site(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected Long doInBackground(String... target) {
        StringBuilder result = new StringBuilder();
        HttpURLConnection urlConnection = null;
        Long duration = 0l;
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

            JSONObject json_Res = new JSONObject(result.toString());

            duration = json_Res.getLong(target[0]);
        }catch(MalformedURLException mue){

        }catch(IOException ioe){

        }catch(JSONException jsone){

        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return duration;

    }

    @Override
    protected void onPostExecute(Long result) {
        delegate.processFinish(result);
    }
}
