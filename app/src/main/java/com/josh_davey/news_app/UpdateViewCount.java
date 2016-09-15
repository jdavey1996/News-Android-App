package com.josh_davey.news_app;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class UpdateViewCount extends AsyncTask<String, String, String>{
    Context ctx;
    Activity activity;

    public UpdateViewCount(Context ctx, Activity activity) {
        this.ctx = ctx;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        String article = params[0];

                try {
                    URL url = new URL("http://josh-davey.com/news_app_data/news_articles-update_view_count.php");

                    //Article number to send.
                    String data  = URLEncoder.encode("article", "UTF-8")
                            + "=" + URLEncoder.encode(article, "UTF-8");

                    return addView(url,data);
                }
                catch (Exception e)
                {
                    return null;
                }
    }

    @Override
    protected void onPostExecute(String result) {

    }

    private String addView(URL url, String data)
    {
        try {
            //Sets the connection.
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            //Creates the output stream and buffered writer to write the string data to and send to the server.
            OutputStream oStream = con.getOutputStream();
            BufferedWriter buffer = new BufferedWriter(new OutputStreamWriter(oStream, "UTF-8"));

            //Writes data to the buffer ready to be sent.
            buffer.write(data);
            //Closes the buffer. This automatically runs the .flush() method which sends the data.
            buffer.close();
            //Closes the stream. All data has been sent to the connected URL.
            oStream.close();

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

            //Returns string response containing the data.
            return response.toString();
        }
        catch (Exception e)
        {
        }
    return null;
    }
}
