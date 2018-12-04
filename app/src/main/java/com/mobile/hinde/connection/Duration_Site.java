package com.mobile.hinde.connection;

import android.os.AsyncTask;
import android.text.Html;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Duration_Site extends AsyncTask<String, Void, String> {

    public AsyncResponse delegate = null;

    public Duration_Site(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... urls) {
        String result = "";
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL("https://space-time-12b72.firebaseapp.com/object_list.json");
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = reader.readLine()) != null) {
                if(line.contains("duration")){
                    result = line.substring(5);
                }
            }
        }catch(MalformedURLException mue){

        }catch(IOException ioe){

        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return result.toString();

    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}
