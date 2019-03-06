package com.mobile.hinde.connection;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import java.io.InputStream;
import java.net.URL;

public class Image_URL extends AsyncTask<String, Void, Drawable> {

    private AsyncResponse delegate;

    public Image_URL(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected Drawable doInBackground(String... target) {

        try {
            InputStream is = (InputStream)new URL(target[0]).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Drawable result) {
        delegate.processFinish(result);
    }

}
