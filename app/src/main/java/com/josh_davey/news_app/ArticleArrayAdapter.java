package com.josh_davey.news_app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

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



        //Gets the current postition when looping through the arraylist.
        final ArticleConstructor data = getItem(position);


        //Sets task title for each list element.
        TextView taskTitle = (TextView) convertView.findViewById(R.id.articleTitle);
        taskTitle.setText(data.getArticleTitle());

        /*Sets an onClickListener for the entire list item.
          When an item is clicked it makes a toast displaying the post number clicked on.*/
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Updates a database containing article view counts, for the selected article.
                UpdateViewCount update = new UpdateViewCount(context,activity);
                update.execute(data.getArticleNum());

                //Create fragment to send, testing sending article number clicked on to the fragment.
                Fragment4 fragment = new Fragment4();
                Bundle bundle = new Bundle();
                bundle.putString("articleNum", data.getArticleNum());
                bundle.putString("articleTitle", data.getArticleTitle());
                bundle.putString("articleDesc", data.getArticleDesc());
                fragment.setArguments(bundle);

                //Creates the fragment in the frame and is made visible.
                FragmentTransaction ft = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.framefrag,fragment).commit();
                FrameLayout frame = (FrameLayout)activity.findViewById(R.id.framefrag);
                frame.setVisibility(View.VISIBLE);

                //Makes the tabbedlayout invisible so tabs cannot be selected when viewing an article in detailed view.
                TabLayout tabLayout = (TabLayout) activity.findViewById(R.id.tab_layout);
                tabLayout.setVisibility(View.GONE);

                //Enables the actionbar back button.
                ((MainActivity) activity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });

        //Updates the dataset for the listview.
        notifyDataSetChanged();

        return convertView;
    }
}
