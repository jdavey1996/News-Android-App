package com.josh_davey.news_app;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ArticleArrayAdapter extends ArrayAdapter<Article>{
    Activity activity;

    public ArticleArrayAdapter(Activity activity, ArrayList<Article> data) {
        super(activity, 0, data);
        this.activity = activity;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater taskInflater = LayoutInflater.from(getContext());
        if (convertView == null) {
            convertView = taskInflater.inflate(R.layout.article, parent, false);
        }

        //Initialises elements from the article xml sheet.
        TextView taskNum = (TextView) convertView.findViewById(R.id.section1);
        TextView taskTitle = (TextView) convertView.findViewById(R.id.section2);
        TextView taskDesc = (TextView) convertView.findViewById(R.id.section3);

        //Gets the current postition when looping through the arraylist.
        final Article data = getItem(position);

        //Sets the data to be displayed in each textview of the list element in this position.
        taskNum.setText(data.getArticleNum());
        taskTitle.setText(data.getArticleTitle());
        taskDesc.setText(data.getArticleDesc());

        //Updates the dataset for the listview.
        notifyDataSetChanged();

        return convertView;
    }

}
