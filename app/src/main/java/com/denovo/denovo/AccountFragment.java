package com.denovo.denovo;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AccountFragment extends Fragment implements RVAdapter.ItemClickCallback {

    private static final String TAG = "AccountFragment";

    private String name;
    private String uid;
    private ArrayList<String> mWishListKeys;
    private ArrayList<Item> mWishList;
    private DatabaseReference mDatabase;
    private RecyclerView wishListRV;
    private WrapContentLinearLayoutManager llm;
    private RVAdapter mAdapter;
    private TextView emptyWishList;
    private TextView profilePic;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private int mItemsLeft;


    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    name = user.getDisplayName();
                    uid = user.getUid();
                    mDatabase.child("users").child(uid).child("wishList").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            mWishListKeys = new ArrayList<>();
                            for (DataSnapshot keySnapshot : dataSnapshot.getChildren()) {
                                String key = keySnapshot.getValue(String.class);
                                mWishListKeys.add(key);
                            }
                            checkWishListEmpty();
                            getWishList(new OnDataReceivedListener() {
                                @Override
                                public void onStart(int listSize) {
                                    mItemsLeft = listSize;
                                }

                                @Override
                                public void onNext() {
                                    mItemsLeft--;
                                    if (mItemsLeft == 0) {
                                        mAdapter.swapDataSet(mWishList, true);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        profilePic = (TextView) rootView.findViewById(R.id.prof_pic);

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

        ImageView editProfile = (ImageView) rootView.findViewById(R.id.edit_profile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_edit_profile);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                View red = dialog.findViewById(R.id.red);
                View pink = dialog.findViewById(R.id.pink);
                View purple = dialog.findViewById(R.id.purple);
                View blue = dialog.findViewById(R.id.blue);
                View teal = dialog.findViewById(R.id.teal);
                View green = dialog.findViewById(R.id.green);
                View yellow = dialog.findViewById(R.id.yellow);
                View orange = dialog.findViewById(R.id.orange);
                View gray = dialog.findViewById(R.id.gray);

                red.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profilePic.setBackgroundResource(R.drawable.profile_red);
                        dialog.dismiss();
                    }
                });

                pink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profilePic.setBackgroundResource(R.drawable.profile_pink);
                        dialog.dismiss();
                    }
                });

                purple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profilePic.setBackgroundResource(R.drawable.profile_purple);
                        dialog.dismiss();
                    }
                });

                blue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profilePic.setBackgroundResource(R.drawable.profile_blue);
                        dialog.dismiss();
                    }
                });

                teal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profilePic.setBackgroundResource(R.drawable.profile_teal);
                        dialog.dismiss();
                    }
                });

                green.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profilePic.setBackgroundResource(R.drawable.profile_green);
                        dialog.dismiss();
                    }
                });

                yellow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profilePic.setBackgroundResource(R.drawable.profile_yellow);
                        dialog.dismiss();
                    }
                });

                orange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profilePic.setBackgroundResource(R.drawable.profile_orange);
                        dialog.dismiss();
                    }
                });

                gray.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profilePic.setBackgroundResource(R.drawable.profile_gray);
                        dialog.dismiss();
                    }
                });
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

    private void getWishList(final OnDataReceivedListener listener) {
        mWishList = new ArrayList<>();
        listener.onStart(mWishListKeys.size());
        for (String key : mWishListKeys) {
            mDatabase.child("items").orderByKey().equalTo(key)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                                Item item = itemSnapshot.getValue(Item.class);
                                mWishList.add(item);
                                listener.onNext();
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

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
