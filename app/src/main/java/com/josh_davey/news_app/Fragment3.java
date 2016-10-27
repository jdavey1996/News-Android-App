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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment3, container, false);
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        final SwipeRefreshLayout sw3 = (SwipeRefreshLayout)getActivity().findViewById(R.id.refreshLayout3);
        sw3.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetArticles getData = new GetArticles(getContext(),getActivity());
                getData.execute("all",null);
                sw3.setRefreshing(false);
            }
        });


    }
}
