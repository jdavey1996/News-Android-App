package com.josh_davey.news_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetArticles extends AsyncTask<String, String, GetArticles.ReturnConstructor>{
    Context ctx;
    Activity activity;
    Fragment frag;
    ProgressDialog progressDialog;

    public GetArticles(Context ctx, Activity activity) {
        this.ctx = ctx;
        this.activity = activity;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Initialises the progress dialog to use the correct styles. This is then set in the onProgressUpdate method.
        progressDialog = new ProgressDialog(ctx, R.style.AppTheme_Dark_Dialog);
        //Adds circle spinner, and non measurable progress.
        progressDialog.setIndeterminate(true);
        //Prevent being cancelled by touching outside of dialog or by back button.
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        //Adds cancel button.
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Dismisses dialog
                dialog.dismiss();
                //Cancels async task
                cancel(true);
            }
        });
    }

    @Override
    protected ReturnConstructor doInBackground(String... params) {
        String dataFilter = params[0];
        String locationFilter = params[1];
        try {
            URL locationUrl = new URL("http://josh-davey.com/news_app_data/news_articles-"+locationFilter+".json");
            URL topUrl = new URL("http://josh-davey.com/news_app_data/news_articles-top_articles.php");
            URL allUrl  = new URL("http://josh-davey.com/news_app_data/news_articles-All.json");

            switch (dataFilter) {
                case "loadall":
                    try {
                        publishProgress("loadall");
                        Thread.sleep(3000);

                        ReturnConstructor returnData = new ReturnConstructor();
                        returnData.dataFilter = "loadall";

                        //Get all articles data.
                        JSONArray all_array = new JSONObject(returnJson(allUrl)).getJSONArray("articles");
                        returnData.all = extractData(all_array);

                        //Get top articles data.
                        JSONArray top_array = new JSONArray(returnJson(topUrl));
                        returnData.top = extractData(top_array);

                        //Check if a JSON file at 'locationUrl' exists
                        Boolean check = checkDataFileExists(locationUrl);
                        //If it exists, get data and add to return variable.
                        if (check==true)
                        {
                            //Get local articles data.
                            JSONArray location_array = new JSONObject(returnJson(locationUrl)).getJSONArray("articles");
                            returnData.local = extractData(location_array);
                        }
                        //If doesn't exist, add null to return variable and add location filter - to check later whether location is null or just a city that's unknown.
                        else if (check ==false){
                            returnData.local = null;
                            returnData.location = locationFilter;
                        }

                        returnData.error = false;
                        return returnData;

                    } catch (Exception e) {
                        ReturnConstructor returnData = new ReturnConstructor();
                        returnData.dataFilter = "loadall";
                        returnData.error = true;
                        return returnData;
                    }
                case "all":
                    try {
                        publishProgress("all");
                        Thread.sleep(3000);
                        ReturnConstructor returnData = new ReturnConstructor();
                        returnData.dataFilter = "all";

                        //Get all articles data.
                        JSONArray all_array = new JSONObject(returnJson(allUrl)).getJSONArray("articles");
                        returnData.all = extractData(all_array);

                        returnData.error = false;
                        return returnData;
                    }catch (Exception e)
                    {
                        ReturnConstructor returnData = new ReturnConstructor();
                        returnData.dataFilter = "all";
                        returnData.error = true;
                        return returnData;
                    }

                case "top":
                    try {
                        publishProgress("top");
                        Thread.sleep(3000);
                        ReturnConstructor returnData = new ReturnConstructor();
                        returnData.dataFilter = "top";

                        //Get top articles data.
                        JSONArray top_array = new JSONArray(returnJson(topUrl));
                        returnData.top = extractData(top_array);

                        returnData.error = false;
                        return returnData;
                    }catch (Exception e)
                    {
                        ReturnConstructor returnData = new ReturnConstructor();
                        returnData.dataFilter = "top";
                        returnData.error = true;
                        return returnData;
                    }

                case "location":
                    try {
                        publishProgress("location");
                        Thread.sleep(3000);
                        ReturnConstructor returnData = new ReturnConstructor();
                        returnData.dataFilter = "location";

                        //Check if a JSON file at 'locationUrl' exists
                        Boolean check = checkDataFileExists(locationUrl);
                        //If it exists, get data and add to return variable.
                        if (check==true)
                        {
                            //Get local articles data.
                            JSONArray location_array = new JSONObject(returnJson(locationUrl)).getJSONArray("articles");
                            returnData.local = extractData(location_array);
                        }
                        //If doesn't exist, add null to return variable.
                        else if (check == false){
                            returnData.local = null;
                            returnData.location = locationFilter;
                        }
                        returnData.error = false;
                        return returnData;
                    }catch (Exception e)
                    {
                        ReturnConstructor returnData = new ReturnConstructor();
                        returnData.dataFilter = "location";
                        returnData.error = true;
                        return returnData;
                    }
            }
        }
        catch (Exception e)
        {
            return null;
        }
        return null;
    }
    //Depending on the function executed, the correct progress dialog is set and displayed.
    @Override
    protected void onProgressUpdate(String... progress) {
        super.onProgressUpdate(progress);
        if (progress[0].equals("loadall")) {
            progressDialog.setMessage("Attempting to download all app data...");
        }
        else if(progress[0].equals("all")) {
            progressDialog.setMessage("Attempting to download data for tab 'ALL'...");
        }
        else if (progress[0].equals("top")) {
            progressDialog.setMessage("Attempting to download data for tab 'MOST VIEWED'...");
        }
        else if (progress[0].equals("location")) {
            progressDialog.setMessage("Attempting to download data for tab 'LOCAL'...");
        }
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(ReturnConstructor result) {
        try {
            progressDialog.dismiss();
            //Initiate SQLite database instance.
            SQLiteDB db = new SQLiteDB(ctx);
            //Initiate textviews for displaying text when listviews are empty.
            TextView emptyView1 = (TextView)activity.findViewById(R.id.emptyView1);
            TextView emptyView2 = (TextView)activity.findViewById(R.id.emptyView2);
            TextView emptyView3 = (TextView)activity.findViewById(R.id.emptyView3);

            if (result.error == true) {
                Toast.makeText(ctx, "Connection error, unable to update data - You're viewing old data.", Toast.LENGTH_SHORT).show();
                switch (result.dataFilter){
                    //If error occurs, load old data.
                    case "loadall":
                        setListView(activity,ctx,db.getArticles("local_articles"),(ListView) activity.findViewById(R.id.lvLocationArticles),emptyView1);
                        setListView(activity,ctx,db.getArticles("top_articles"),(ListView) activity.findViewById(R.id.lvTopArticles),emptyView2);
                        setListView(activity,ctx,db.getArticles("all_articles"),(ListView) activity.findViewById(R.id.lvAllArticles),emptyView3);
                        break;
                    case "all":
                        setListView(activity,ctx,db.getArticles("all_articles"),(ListView) activity.findViewById(R.id.lvAllArticles),emptyView3);
                        break;
                    case "location":
                        setListView(activity,ctx,db.getArticles("local_articles"),(ListView) activity.findViewById(R.id.lvLocationArticles),emptyView1);
                        break;
                    case "top":
                        setListView(activity,ctx,db.getArticles("top_articles"),(ListView) activity.findViewById(R.id.lvTopArticles),emptyView2);
                        break;
                }
            }else {
                switch (result.dataFilter) {
                    case "loadall":
                        Toast.makeText(ctx, "Download successful!", Toast.LENGTH_SHORT).show();
                        //ALL ARTICLES
                        //Delete existing articles from table.
                        db.deleteAll("all_articles");
                        //Add all articles downloaded
                        for (int i = 0; i < result.all.size(); i++) {
                                db.addArticle(result.all.get(i), "all_articles");
                            }
                        setListView(activity, ctx, db.getArticles("all_articles"), (ListView) activity.findViewById(R.id.lvAllArticles), emptyView3);

                        //TOP ARTICLES
                        //Delete existing articles from table.
                        db.deleteAll("top_articles");
                        //Add all articles downloaded
                        for (int i = 0; i < result.top.size(); i++) {
                            db.addArticle(result.top.get(i), "top_articles");
                        }
                        setListView(activity, ctx, db.getArticles("top_articles"), (ListView) activity.findViewById(R.id.lvTopArticles), emptyView2);

                        //LOCATION ARTICLES
                        //Delete existing articles from table.
                        db.deleteAll("local_articles");
                        //If null is returned (no url exists for current location), set empty view text.
                        if (result.local == null)
                        {
                            Toast.makeText(ctx, "An location error occurred. Please see the Local tab for more info.", Toast.LENGTH_SHORT).show();
                            if(result.location == null)
                            {
                                emptyView1.setText("Unable to acquire your location, please try again later.");
                            }
                            else {
                                emptyView1.setText("No articles available for your location");
                            }
                        }
                        else {
                            //Add all articles downloaded.
                            for (int i = 0; i < result.local.size(); i++) {
                                db.addArticle(result.local.get(i), "local_articles");
                            }
                        }
                        //Set data from db to listview. Sets no data to listview if null is returned above as db.delete is ran previously.
                        setListView(activity, ctx, db.getArticles("local_articles"), (ListView) activity.findViewById(R.id.lvLocationArticles), emptyView1);

                        break;

                    case "all":
                        Toast.makeText(ctx, "Download successful!", Toast.LENGTH_SHORT).show();
                        //ALL
                        //Delete existing articles from table.
                        db.deleteAll("all_articles");
                        //Add all articles downloaded
                        for (int i = 0; i < result.all.size(); i++) {
                            db.addArticle(result.all.get(i), "all_articles");
                        }
                        //Set list data.
                        setListView(activity, ctx, db.getArticles("all_articles"), (ListView) activity.findViewById(R.id.lvAllArticles), emptyView3);
                        break;

                    case "top":
                        Toast.makeText(ctx, "Download successful!", Toast.LENGTH_SHORT).show();
                        //TOP
                        //Delete existing articles from table.
                        db.deleteAll("top_articles");
                        //Add all articles downloaded
                        for (int i = 0; i < result.top.size(); i++) {
                            db.addArticle(result.top.get(i), "top_articles");
                        }
                        //Set list data.
                        setListView(activity, ctx, db.getArticles("top_articles"), (ListView) activity.findViewById(R.id.lvTopArticles), emptyView2);
                        break;

                    case "location":
                        //LOCATION
                        //Delete existing articles from table.
                        db.deleteAll("local_articles");
                        //If null is returned (no url exists for current location), set empty view text.
                        if (result.local == null)
                        {
                            Toast.makeText(ctx, "An location error occurred. Please see the Local tab for more info.", Toast.LENGTH_SHORT).show();
                            if(result.location == null)
                            {
                                emptyView1.setText("Unable to acquire your location, please try again later.");
                            }
                            else {
                                emptyView1.setText("No articles available for your location");
                            }
                        }
                        else {
                            Toast.makeText(ctx, "Download successful!", Toast.LENGTH_SHORT).show();
                            //Add all articles downloaded.
                            for (int i = 0; i < result.local.size(); i++) {
                                db.addArticle(result.local.get(i), "local_articles");
                            }
                        }
                        //Set data from db to listview. Sets no data to listview if null is returned above as db.delete is ran previously.
                        setListView(activity, ctx, db.getArticles("local_articles"), (ListView) activity.findViewById(R.id.lvLocationArticles), emptyView1);
                        break;
                }
            }
        }
        catch (Exception e)
        {
        }
    }


    //Method to download data from URL.
    private String returnJson(URL url) {
        try {
            //Sets the connection.
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(30000);
            con.setReadTimeout(30000);

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

            //Returns data.
            return response.toString();
        } catch (Exception e) {
            return null;
        }
    }

    //Method to extract data from JSON array into an ArticleConstructor constructed ArrayList.
    public ArrayList<ArticleConstructor> extractData(JSONArray array)
    {
        try {
            ArrayList<ArticleConstructor> data = new ArrayList<ArticleConstructor>();

            //Loops through al objects within the articles array, adding them to an articles object, then the an ArrayList to be returned to the onPostExecute method.
            for (int i = 0; i < array.length(); i++) {
                JSONObject temp = array.getJSONObject(i);
                ArticleConstructor obj = new ArticleConstructor(temp.getString("number").toString(), temp.getString("title").toString(), temp.getString("desc").toString());
                data.add(obj);
            }
            return data;
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    //Method to set listview data.
    public void setListView(Activity activity, Context ctx, ArrayList<ArticleConstructor> list, ListView lv, TextView empty)
    {
        ListAdapter adapter = new ArticleArrayAdapter(activity,ctx, list);
        ListView listView = lv;
        listView.setAdapter(adapter);
        listView.setEmptyView(empty);
    }

    //Method to check if JSON file exists at URL. If true, return true. If false, return false. If exception occurs (no internet con), throw exception back to calling method.
    public boolean checkDataFileExists(URL url) {
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("HEAD");
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);
            if ((con.getResponseCode() == HttpURLConnection.HTTP_OK))
                return true;
            else
                return false;
        } catch (Exception e) {
            throw new RuntimeException("Unable to check network if exists, network error");
        }
    }


    class ReturnConstructor
    {
        public String dataFilter;
        public ArrayList<ArticleConstructor> all = new ArrayList<ArticleConstructor>();
        public ArrayList<ArticleConstructor> local = new ArrayList<ArticleConstructor>();
        public ArrayList<ArticleConstructor> top = new ArrayList<ArticleConstructor>();
        public boolean error;
        public String location;
    }
}
