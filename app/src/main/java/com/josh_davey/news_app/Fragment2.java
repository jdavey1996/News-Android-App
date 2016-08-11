package com.josh_davey.news_app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment2 extends Fragment {

    public Fragment2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment2, container, false);

        final SwipeRefreshLayout sw = (SwipeRefreshLayout)view.findViewById(R.id.refreshLayout2);

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
        GetMostViewedArticles getData = new GetMostViewedArticles(getContext(),getActivity(),this);
        getData.execute();
    }
}
