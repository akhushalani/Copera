package com.denovo.denovo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.denovo.denovo.R.id.rv;

public class AccountFragment extends Fragment implements RVAdapter.ItemClickCallback {

    private String name;
    private String uid;
    private ArrayList<String> mWishListKeys;
    private ArrayList<Item> mWishList;
    private DatabaseReference mDatabase;
    private RecyclerView wishListRV;
    private WrapContentLinearLayoutManager llm;
    private RVAdapter mAdapter;
    private TextView emptyWishList;


    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            name = user.getDisplayName();
            uid = user.getUid();
        }

        TextView userNameTextView = (TextView) rootView.findViewById(R.id.user_name_text_view);
        userNameTextView.setText(name);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mWishListKeys = new ArrayList<>();
        mWishList = new ArrayList<>();

        emptyWishList = (TextView) rootView.findViewById(R.id.empty_wish_list);

        wishListRV = (RecyclerView) rootView.findViewById(R.id.wishlist_rv);
        llm = new WrapContentLinearLayoutManager(getContext());
        wishListRV.setLayoutManager(llm);

        mAdapter = new RVAdapter(mWishList);
        wishListRV.setAdapter(mAdapter);
        mAdapter.setItemClickCallback(this);

        ((SimpleItemAnimator) wishListRV.getItemAnimator()).setSupportsChangeAnimations(false);

        checkWishListEmpty();

        mDatabase.child("users").child(uid).child("wishList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mWishListKeys = new ArrayList<>();
                for (DataSnapshot keySnapshot : dataSnapshot.getChildren()) {
                    String key = keySnapshot.getValue(String.class);
                    mWishListKeys.add(key);
                }
                checkWishListEmpty();
                getWishList();
                mAdapter.swapDataSet(mWishList, true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    private void checkWishListEmpty() {
        if (mWishListKeys == null || mWishListKeys.isEmpty()) {
            wishListRV.setVisibility(View.GONE);
            emptyWishList.setVisibility(View.VISIBLE);
        } else {
            wishListRV.setVisibility(View.VISIBLE);
            emptyWishList.setVisibility(View.GONE);
        }
    }

    private void getWishList() {
        for (String key : mWishListKeys) {
            mDatabase.child("items").orderByKey().equalTo(key)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                                Item item = itemSnapshot.getValue(Item.class);
                                mWishList.add(item);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    @Override
    public void onItemClick(int p) {
        Item item = mWishList.get(p);

        Intent i = new Intent(getActivity(), ItemActivity.class);
        i.putExtra("item", item.getId());

        startActivityForResult(i, 1);
    }

    @Override
    public void onWantItBtnClick(int p) {
        String itemId = mWishList.get(p).getId();
        mWishList.get(p).onAddedToWishList(uid, itemId);
    }

    @Override
    public void onBargainBtnClick(int p) {
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
                mWishList.set(p, item);
                mAdapter.set(p, item);
                mAdapter.notifyItemChanged(p);
            }
        }
    }

}
