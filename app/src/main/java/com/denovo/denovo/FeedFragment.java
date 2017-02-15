package com.denovo.denovo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.media.CamcorderProfile.get;


public class FeedFragment extends Fragment implements RVAdapter.ItemClickCallback {

    private static final String TAG = "FeedFragment";

    private ArrayList<Item> mFeed;
    private ArrayList<String> mFeedKeys;
    private WrapContentLinearLayoutManager llm;
    private RVAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private DatabaseReference mDatabase;
    private String uid;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        mSwipeRefreshLayout.setRefreshing(true);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv);
        llm = new WrapContentLinearLayoutManager(getContext());
        rv.setLayoutManager(llm);

        mFeed = new ArrayList<>();
        mFeedKeys = new ArrayList<>();

        mAdapter = new RVAdapter(mFeed);
        rv.setAdapter(mAdapter);
        mAdapter.setItemClickCallback(this);

        ((SimpleItemAnimator) rv.getItemAnimator()).setSupportsChangeAnimations(false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("items").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mSwipeRefreshLayout.setRefreshing(true);
                Item newItem = dataSnapshot.getValue(Item.class);
                mFeedKeys.add(0, dataSnapshot.getKey());
                mFeed.add(0, newItem);
                mAdapter.swapDataSet(mFeed);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Item newItem = dataSnapshot.getValue(Item.class);
                String itemKey = dataSnapshot.getKey();

                int itemIndex = mFeedKeys.indexOf(itemKey);
                if (itemIndex > -1) {
                    mFeed.set(itemIndex, newItem);
                    mAdapter.swapDataSet(mFeed, itemIndex);
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + itemKey);
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String itemKey = dataSnapshot.getKey();

                int itemIndex = mFeedKeys.indexOf(itemKey);
                if (itemIndex > -1) {
                    // Remove data from the list
                    mFeedKeys.remove(itemIndex);
                    mFeed.remove(itemIndex);

                    // Update the RecyclerView
                    mAdapter.notifyItemRemoved(itemIndex);
                } else {
                    Log.w(TAG, "onChildRemoved:unknown_child:" + itemKey);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load feed.", Toast.LENGTH_SHORT).show();
            }
        });

        /* ArrayList<Question> questionList = new ArrayList<>();
        Question a = new Question("I am a huge fan of the Lord of the Ring series; however, i am " +
                "not sure I'm into the whole magic thing. Would you recommend this book for a " +
                "reader like me?");
        a.setAnswer("This book is great for all fiction fans! As long as you have an imagination " +
                "and an open-mind you will adore this modern age classic!");
        for (int i = 0; i < 7; i++) {
            questionList.add(a);
        }


        mFeed = new ArrayList<>();
        mFeed.add(new Item("Book", "book.png", "Dulaney FBLA", "Abhinav Khushalani", 1.5, 4,
                "What " +
                "did Harry" +
                " Potter know about magic? He was stuck with the decidedly un-magical Dursleys, " +
                "who hated him. He slept in a closet and ate their leftovers. But an owl " +
                "messenger changes all that, with an invitation to attend the Hogwarts School for" +
                " Wizards and Witches, where it turns out Harry is already famous.", questionList));
        mFeed.add(new Item("Shoes", "shoes.png", "Dulaney FBLA", "Nick Owens", 3.0, 5, "Worn " +
                "once, soles are" +
                " a bit stepped on, mid soles are a bit dirty.", questionList));
        mFeed.add(new Item("Shirt", "shirt.png", "Dulaney FBLA", "Kevin Zorbach", 0.25, 3,
                "Printed on " +
                "Gildan shirts just like the original. We also matched the ink for an almost " +
                "identical match. If you're not happy with your purchase for any reason, simply " +
                "let us know and we'll be sure to do whatever it takes to make sure the issue is " +
                "taken care of!", questionList));

        for (Item item : mFeed) {
            writeNewItem(item);
        } */

        return rootView;
    }

    void refreshItems() {
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        //mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void getAllItems(DataSnapshot dataSnapshot) {
        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
            Item item  = singleSnapshot.getValue(Item.class);
            mFeed.add(0, item);
            mAdapter.swapDataSet(mFeed);
            mAdapter.notifyItemInserted(0);
        }
    }

    private void writeNewItem(String name, String yardSale, String donor, double price, int
            rating, String description) {
        Item item = new Item(name, "shoes.png", yardSale, donor, price, rating, description,
                new ArrayList<String>());
        DatabaseReference childRef = mDatabase.child("items").push();
        childRef.setValue(item);
    }

    private void writeNewItem(Item item) {
        DatabaseReference childRef = mDatabase.child("items").push();
        childRef.setValue(item);
    }

    @Override
    public void onItemClick(int p) {
        Item item = mFeed.get(p);

        Intent i = new Intent(getActivity(), ItemActivity.class);
        i.putExtra("item", item.getId());
        Log.v(TAG, item.getId());

        startActivityForResult(i, 1);
    }

    @Override
    public void onWantItBtnClick(int p) {
        String itemId = mFeed.get(p).getId();
        mFeed.get(p).onAddedToWishList(uid, itemId);
    }

    @Override
    public void onOfferBtnClick(int p) {
        Intent intent = new Intent(getActivity(), BargainActivity.class);
        intent.putExtra("item_key", "-KYLXskpzmhDq5citlod");
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == 1) {
                int p = data.getExtras().getInt("position");
                Item item = data.getExtras().getParcelable("item");
                mFeed.set(p, item);
                mAdapter.set(p, item);
                mAdapter.notifyItemChanged(p);
            }
        }
    }
}
