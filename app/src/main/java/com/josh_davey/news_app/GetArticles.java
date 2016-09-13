package com.josh_davey.news_app;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetArticles extends AsyncTask<String, String,ArrayList<ArticleConstructor>>{
    Context ctx;
    Activity activity;
    Fragment frag;
    public GetArticles(Context ctx, Activity activity, Fragment frag) {
        this.ctx = ctx;
        this.activity = activity;
        this.frag = frag;
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
            Thread.sleep(2000);
            return data;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<ArticleConstructor> result) {
        try {
            //Initialises a listview as null for use in if statements.
            ListView list = null;
            SwipeRefreshLayout sw = null;

            if (frag instanceof Fragment3) {
                //Gets the list for fragment3 if the passed fragment is an instance of fragment3.
                list = (ListView) activity.findViewById(R.id.lvAllArticles);

                //Gets refresh layout for fragment, containing the listview.
                sw = (SwipeRefreshLayout) frag.getView().findViewById(R.id.refreshLayout3);

            } else if (frag instanceof Fragment1) {
                //Gets the list for fragment1 if the passed fragment is an instance of fragment1.
                list = (ListView) activity.findViewById(R.id.lvArticlesLocation);

                //Gets refresh layout for fragment, containing the listview.
                sw = (SwipeRefreshLayout) frag.getView().findViewById(R.id.refreshLayout1);
            }

            if(result != null) {
                //Creates an instance of the arrayadapter, passing the returned results.
                final ListAdapter adapter = new ArticleArrayAdapter(activity, ctx, result);

                //Sets the adapter to the listview.
                list.setAdapter(adapter);
            }
            else
            {
                Toast.makeText(ctx, "Unable to load data, please check your network connection and swipe down to refresh.", Toast.LENGTH_SHORT).show();
            }

            //Sets refresh aniation to false.
            sw.setRefreshing(false);
        }
        catch (Exception e)
        {
        }
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
        }
        return null;
    }
}
