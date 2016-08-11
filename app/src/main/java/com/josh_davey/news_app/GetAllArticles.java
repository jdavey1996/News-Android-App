package com.josh_davey.news_app;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetAllArticles extends AsyncTask<String, String,ArrayList<ArticleConstructor>>{
    Context ctx;
    Activity activity;

    public GetAllArticles(Context ctx, Activity activity) {
        this.ctx = ctx;
        this.activity = activity;
    }

    @Override
    protected ArrayList<ArticleConstructor> doInBackground(String... params) {
        String datafilter = params[0];
        try {
            ArrayList<ArticleConstructor> data = new ArrayList<ArticleConstructor>();

            URL url = new URL("http://josh-davey.com/news_app_data/news_articles-"+datafilter+".json");

            //Gets the articles array stored within the downloaded json object.
            JSONArray array = returnJson(url).getJSONArray("articles");

            //Loops through al objects within the articles array, adding them to an articles object, then the an ArrayList to be returned to the onPostExecute method.
            for (int i = 0; i < array.length(); i++) {
                JSONObject temp = array.getJSONObject(i);
                ArticleConstructor obj = new ArticleConstructor(temp.getString("number").toString(),temp.getString("title").toString(),temp.getString("desc").toString());
                data.add(obj);
            }

            return data;
        }
        catch (Exception e)
        {
            //Catches exceptions and displays them in the Log.
            Log.e("Exception: ", e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<ArticleConstructor> result) {
        //Uses a listadapter to set the data for the listview in fragment 3.
        final ListAdapter adapter = new ArticleArrayAdapter(activity,ctx, result);
        final ListView list = (ListView) activity.findViewById(R.id.lvAllArticles);
        list.setAdapter(adapter);
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
            Log.e("Exception: ", e.toString());
        }
        return null;
    }
}
