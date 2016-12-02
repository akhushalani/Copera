package com.denovo.denovo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class FeedFragment extends Fragment implements RVAdapter.ItemClickCallback {

    private static final String TAG = "FeedFragment";
    private ArrayList<Item> feed;
    private RVAdapter adapter;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(llm);

        ArrayList<Question> questionList = new ArrayList<>();
        Question a = new Question("I am a huge fan of the Lord of the Ring series; however, i am " +
                "not sure I'm into the whole magic thing. Would you recommend this book for a " +
                "reader like me?");
        a.setAnswer("This book is great for all fiction fans! As long as you have an imagination " +
                "and an open-mind you will adore this modern age classic!");
        for (int i = 0; i < 7; i++) {
            questionList.add(a);
        }


        feed = new ArrayList<>();
        feed.add(new Item("Book", R.drawable.book, "Dulaney FBLA", "Abhinav Khushalani", 1.5, 4,
                "What " +
                "did Harry" +
                " Potter know about magic? He was stuck with the decidedly un-magical Dursleys, " +
                "who hated him. He slept in a closet and ate their leftovers. But an owl " +
                "messenger changes all that, with an invitation to attend the Hogwarts School for" +
                " Wizards and Witches, where it turns out Harry is already famous.", questionList));
        feed.add(new Item("Shoes", R.drawable.shoes, "Dulaney FBLA", "Nick Owens", 3.0, 5, "Worn once, soles are" +
                " a bit stepped on, mid soles are a bit dirty.", questionList));
        feed.add(new Item("Shirt", R.drawable.shirt, "Dulaney FBLA", "Kevin Zorbach", 0.25, 3, "Printed on " +
                "Gildan shirts just like the original. We also matched the ink for an almost " +
                "identical match. If you're not happy with your purchase for any reason, simply " +
                "let us know and we'll be sure to do whatever it takes to make sure the issue is " +
                "taken care of!", questionList));

        adapter = new RVAdapter(feed);
        rv.setAdapter(adapter);
        adapter.setItemClickCallback(this);

        return rootView;
    }


    @Override
    public void onItemClick(int p) {
        Item item = feed.get(p);

        Intent i = new Intent(getActivity(), ItemActivity.class);
        i.putExtra("item", item);
        i.putExtra("position", p);

        startActivityForResult(i, 1);
    }

    @Override
    public void onWantItBtnClick(int p) {
        feed.get(p).setWantIt();
    }

    @Override
    public void onBargainBtnClick(int p) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == 1) {
                int p = data.getExtras().getInt("position");
                Item item = data.getExtras().getParcelable("item");
                feed.set(p, item);
                adapter.set(p, item);
                adapter.notifyItemChanged(p);
            }
        }
    }
}
