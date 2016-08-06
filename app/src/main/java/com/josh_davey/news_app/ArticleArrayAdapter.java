package com.josh_davey.news_app;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ArticleArrayAdapter extends ArrayAdapter<ArticleConstructor>{
    Activity activity;
    Context context;
    public ArticleArrayAdapter(Activity activity, Context context, ArrayList<ArticleConstructor> data) {
        super(activity, 0, data);
        this.activity = activity;
        this.context = context;
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
        final ArticleConstructor data = getItem(position);

        //Sets the data to be displayed in each textview of the list element in this position.
        taskNum.setText(data.getArticleNum());
        taskTitle.setText(data.getArticleTitle());
        taskDesc.setText(data.getArticleDesc());

        /*Sets an onClickListener for the entire list item.
          When an item is clicked it makes a toast displaying the post number clicked on.*/
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "This is post number"+data.getArticleNum(), Toast.LENGTH_SHORT).show();
            }
        });

        //Updates the dataset for the listview.
        notifyDataSetChanged();

        return convertView;
    }
}




