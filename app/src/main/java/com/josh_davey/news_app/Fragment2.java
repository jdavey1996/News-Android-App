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

public class Fragment2 extends Fragment {
    public Fragment2()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment2, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        final SwipeRefreshLayout sw2 = (SwipeRefreshLayout)getActivity().findViewById(R.id.refreshLayout2);
        sw2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetArticles getData = new GetArticles(getContext(),getActivity());
                getData.execute("top",null);
                sw2.setRefreshing(false);
            }
        });

    }
}
