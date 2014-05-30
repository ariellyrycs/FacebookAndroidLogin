package com.facebook;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by root on 29/05/14.
 */
class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private ProgressDialog mDialog;
    private ImageView bmImage;
    private MainActivity vars = new  MainActivity();



    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    public void onPreExecute() {
        mDialog = ProgressDialog.show(vars.context, "Please wait...", "Retrieving data ...", true);
    }

    protected Bitmap doInBackground(String... urls) {
        String urlDisplay = urls[0];
        Bitmap bitMap = null;
        try {
            InputStream in = new URL(urlDisplay).openStream();
            bitMap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", "image download error");
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return bitMap;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
        mDialog.dismiss();
    }
}