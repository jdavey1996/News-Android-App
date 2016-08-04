package com.josh_davey.news_app;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetData extends AsyncTask<String, String,String>{
    Context ctx;
    Activity activity;

    public GetData(Context ctx, Activity activity) {
        this.ctx = ctx;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL("http://josh-davey.com/news_app_data/news_articles.json");

            JSONArray array = returnJson(url).getJSONArray("articles");

            for (int i = 0; i < array.length(); i++) {
                JSONObject temp = array.getJSONObject(i);
                Log.i("DATA", temp.getString("title").toString());
            }

            return null;
        }
        catch (Exception e)
        {
            Log.i("doInBackground error", e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {

    }

    private JSONObject returnJson(URL url) {
        try {
            //Sets the connection.
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            //Gets response from the server. Reads inputstream and builds a string response.
            InputStream iStream = con.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                response.append(line);

            //Closes reader and inputstream.
            reader.close();
            iStream.close();

            //Convert reader response to a JSON object.
            JSONObject data = new JSONObject(response.toString());

            //Returns JSON object containing the data.
            return data;
        } catch (Exception e) {
            //Catches exceptions and displays them in the Log.
            Log.e("GetData exception: ", e.toString());
        }
        return null;
    }
}
