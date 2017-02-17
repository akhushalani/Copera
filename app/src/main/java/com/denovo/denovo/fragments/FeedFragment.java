package com.denovo.denovo.fragments;


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

import com.denovo.denovo.models.Item;
import com.denovo.denovo.R;
import com.denovo.denovo.views.WrapContentLinearLayoutManager;
import com.denovo.denovo.activities.ItemActivity;
import com.denovo.denovo.adapters.RVAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

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

        //find swipeRefreshLayout from xml
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        //set layout to refresh when swiped up
        mSwipeRefreshLayout.setRefreshing(true);

        //get unique id of current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        //find rv from xml
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv);
        //set wrapContentLinearLayoutManager on rv
        llm = new WrapContentLinearLayoutManager(getContext());
        rv.setLayoutManager(llm);

        //create new arrayLists
        mFeed = new ArrayList<>();
        mFeedKeys = new ArrayList<>();

        //hook up RVAdapter to rv
        mAdapter = new RVAdapter(mFeed);
        rv.setAdapter(mAdapter);
        mAdapter.setItemClickCallback(this);

        ((SimpleItemAnimator) rv.getItemAnimator()).setSupportsChangeAnimations(false);

        //add child event listener to listen for changes to the items
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("items").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //enable refreshing
                mSwipeRefreshLayout.setRefreshing(true);
                //create item from data read from database
                Item newItem = dataSnapshot.getValue(Item.class);
                //add item key to feedKeys
                mFeedKeys.add(0, dataSnapshot.getKey());
                //add item to feed
                mFeed.add(0, newItem);
                //update the feed
                mAdapter.swapDataSet(mFeed);
                //disable refreshing
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //create item from data read from database
                Item newItem = dataSnapshot.getValue(Item.class);
                //get the key of the item
                String itemKey = dataSnapshot.getKey();

                //get the index of the item
                int itemIndex = mFeedKeys.indexOf(itemKey);
                if (itemIndex > -1) {
                    //swap the new item with the old item
                    mFeed.set(itemIndex, newItem);
                    //update the feed
                    mAdapter.swapDataSet(mFeed, itemIndex);
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child:" + itemKey);
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //get the key of the item
                String itemKey = dataSnapshot.getKey();

                //get the index of the item
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

        return rootView;
    }

    void refreshItems() {
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        //mAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }


    /**
     * when an item card is clicked, start itemActivity for the item
     *
     * @param p is the position of the recyclerView
     */
    @Override
    public void onItemClick(int p) {
        //get the item at the current position
        Item item = mFeed.get(p);

        //start itemActivity for the current item
        Intent i = new Intent(getActivity(), ItemActivity.class);
        i.putExtra("item", item.getId());
        Log.v(TAG, item.getId());

        startActivityForResult(i, 1);
    }

    /**
     * When the want it button is clicked, add the item to the current user's wishList
     *
     * @param p is the position of the recyclerView
     */
    @Override
    public void onWantItBtnClick(int p) {
        //get the id of the item at the current position
        String itemId = mFeed.get(p).getId();
        //add the item to the user's wishList
        mFeed.get(p).onAddedToWishList(uid, itemId);
    }

    @Override
    public void onOfferBtnClick(int p) {

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
