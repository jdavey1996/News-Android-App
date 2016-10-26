package com.josh_davey.news_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
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
    //Integer articleAmount = 0;
    //articleAmount = articleAmount + 1;

    public GetArticles(Context ctx, Activity activity) {
        this.ctx = ctx;
        this.activity = activity;
        this.frag = frag;
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
        String locationFilter = params[0];
        try {
            URL locationUrl = new URL("http://josh-davey.com/news_app_data/news_articles-"+locationFilter+".json");
            URL topUrl = new URL("http://josh-davey.com/news_app_data/news_articles-most_viewed_articles.php");
            URL allUrl  = new URL("http://josh-davey.com/news_app_data/news_articles-All.json");
            publishProgress("load");

            Thread.sleep(3000);
            switch (dataFilter) {
                case "loadall":
                    try {
                        JSONArray all_array = new JSONObject(returnJson(allUrl)).getJSONArray("articles");
                        //JSONArray top_array = new JSONArray(returnJson(topUrl));

                        //JSONArray location_array = new JSONObject(returnJson(locationUrl)).getJSONArray("articles");

                        ReturnConstructor returnData = new ReturnConstructor();
                        returnData.all = getData(all_array);

                        // returnData.top = getData(top_array);
                        // returnData.local = getData(location_array);
                        returnData.dataFilter = "loadall";
                        returnData.error = false;
                        return returnData;

                    } catch (Exception e) {
                        ReturnConstructor returnData = new ReturnConstructor();
                        returnData.dataFilter = "loadall";
                        returnData.error = true;
                        return returnData;
                    }
                case "all":

                    break;

                case "location":

                    break;

                case "top":

                    break;

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
        if (progress[0].equals("load")) {
            progressDialog.setMessage("Downloading data...");
        }
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(ReturnConstructor result) {
        try {
            progressDialog.dismiss();
            TextView emptyView1 = (TextView)activity.findViewById(R.id.emptyView1);
            TextView emptyView2 = (TextView)activity.findViewById(R.id.emptyView2);
            TextView emptyView3 = (TextView)activity.findViewById(R.id.emptyView3);

            if (result.error == true) {
                Toast.makeText(ctx, "Failure downloading. You're now viewing historic data.", Toast.LENGTH_SHORT).show();
                SQLiteDB db = new SQLiteDB(ctx);
                switch (result.dataFilter){
                    case "loadall":
                        //setListView for other two
                        setListView(activity,ctx,db.getArticles("all_articles"),(ListView) activity.findViewById(R.id.lvAllArticles),emptyView3);
                        break;
                    case "all":

                        break;

                    case "location":

                        break;

                    case "top":

                        break;
                }
            }else {
               switch (result.dataFilter) {
                    case "loadall":
                        Toast.makeText(ctx, "Download successful!", Toast.LENGTH_SHORT).show();

                        //Get database instance and delete existing articles from table.
                        SQLiteDB db = new SQLiteDB(ctx);
                        db.deleteAll("all_articles");

                        //Add all articles downloaded
                        for (int i = 0; i<result.all.size(); i++)
                        {
                            db.addArticle(result.all.get(i),"all_articles");
                            Log.i("CHECK DOWNLOADED DATA",result.all.get(i).getArticleTitle() );
                        }

                        //
                        setListView(activity,ctx,db.getArticles("all_articles"),(ListView) activity.findViewById(R.id.lvAllArticles),emptyView3);

                        break;

                    case "all":

                        break;

                    case "location":

                        break;

                    case "top":

                        break;
                }
            }
        }
        catch (Exception e)
        {
        }
    }

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



    public ArrayList<ArticleConstructor> getData(JSONArray array)
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



    public void setListView(Activity activity, Context ctx, ArrayList<ArticleConstructor> list, ListView lv, TextView empty)
    {
        ListAdapter adapter = new ArticleArrayAdapter(activity,ctx, list);
        ListView listView = lv;
        listView.setAdapter(adapter);
        listView.setEmptyView(empty);
    }

    class ReturnConstructor
    {
        public String dataFilter;
        public ArrayList<ArticleConstructor> all = new ArrayList<ArticleConstructor>();
        public ArrayList<ArticleConstructor> local = new ArrayList<ArticleConstructor>();
        public ArrayList<ArticleConstructor> top = new ArrayList<ArticleConstructor>();
        public boolean error;
    }
}
