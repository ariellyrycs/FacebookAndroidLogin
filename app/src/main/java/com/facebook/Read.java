package com.facebook;

import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by root on 29/05/14.
 */
public class Read extends AsyncTask<String, Integer, String[]>{
    private static final int HTTP_SUCCESS_CODE = 200;
    private static final int NAME = 0;
    private static final int EMAIL = 1;
    private static final int PICTURE = 2;
    MainActivity vars = new  MainActivity();
    @Override
    protected String[] doInBackground(String... params) {
        try {
            return lastTweet();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new String[]{"error"};
    }

    @Override
    public void onPostExecute(String[] result) {
        if (result != null) {
            vars.textName.setText(result[NAME]);
            vars.textEmail.setText(result[EMAIL]);
            new DownloadImageTask(vars.imageViewpic).execute(result[PICTURE]);
        } else {
            Toast.makeText(vars.getApplicationContext(), "error", Toast.LENGTH_LONG).show();
        }
    }
    public String[] lastTweet() throws IOException, JSONException {
        HttpGet get = new HttpGet(vars.urlTemp);
        HttpResponse r = vars.client.execute(get);
        int status = r.getStatusLine().getStatusCode();

        if (status == HTTP_SUCCESS_CODE) {
            HttpEntity e = r.getEntity();
            String JSONPrint = EntityUtils.toString(e);
            JSONObject object = new JSONObject(JSONPrint);
            JSONObject object1 = object.getJSONObject("picture");
            JSONObject object2 = object1.getJSONObject("data");
            String[] myStringArray = {object.getString("name"), object.getString("email"), object2.getString("url")};
            return myStringArray;
        } else {
            return null;
        }
    }
}