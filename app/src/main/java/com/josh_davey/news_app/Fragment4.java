package com.josh_davey.news_app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Fragment4 extends Fragment {

    public Fragment4() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Frag4","Created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment4, container, false);

        //Gets the data sent via bundle when creating the fragment. Logs the article number sent via the bundle.
        Bundle bundle = this.getArguments();
        String bundleArticleTitle = bundle.getString("articleTitle", "null");
        String bundleArticleDesc = bundle.getString("articleDesc", "null");

        //Sets title and description for selected article.
        TextView title = (TextView)view.findViewById(R.id.articleTitle);
        title.setText(bundleArticleTitle);
        TextView desc = (TextView)view.findViewById(R.id.articleDesc);
        desc.setText(bundleArticleDesc);
        return view;
    }
}
