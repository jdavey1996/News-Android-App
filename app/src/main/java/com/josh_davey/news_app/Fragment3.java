package com.josh_davey.news_app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class Fragment3 extends Fragment {

    public Fragment3() {
        // Required empty public constructor
    }
    SwipeRefreshLayout sw;

    @Override
    public void onStart() {
        super.onStart();
        loadData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment3, container, false);

        sw = (SwipeRefreshLayout)view.findViewById(R.id.refreshLayout3);

        //Reloads data on swipe down.
        sw.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadData();
                    }
                }
        );
        return view;
    }

    public void loadData()
    {
        try {
            final GetArticles getData = new GetArticles(getContext(), getActivity(), this);
            getData.execute("all");

            Handler handler = new Handler();
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run() {
                    if ( getData.getStatus() == AsyncTask.Status.RUNNING )
                        Toast.makeText(getContext(), "Connection error.", Toast.LENGTH_SHORT).show();
                        getData.cancel(true);
                        sw.setRefreshing(false);
                }
            }, 20000 );

        }
        catch (Exception e)
        {
        }
    }
}
