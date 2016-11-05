package com.denovo.denovo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;



public class FeedFragment extends Fragment {

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_feed, container, false);

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);

        ArrayList<Item> feed = new ArrayList<>();
        feed.add(new Item("Book", R.drawable.book, "Abhinav Khushalani", 1.5, 4));
        feed.add(new Item("Shoes", R.drawable.shoes, "Nick Owens", 3.0, 5));
        feed.add(new Item("Shirt", R.drawable.shirt, "Kevin Zorbach", 0.25, 3));

        RVAdapter adapter = new RVAdapter(feed);
        rv.setAdapter(adapter);

        return rootView;
    }


}
