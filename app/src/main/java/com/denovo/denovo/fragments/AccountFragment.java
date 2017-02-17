package com.denovo.denovo.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.denovo.denovo.models.Item;
import com.denovo.denovo.interfaces.OnDataReceivedListener;
import com.denovo.denovo.R;
import com.denovo.denovo.models.User;
import com.denovo.denovo.views.WrapContentLinearLayoutManager;
import com.denovo.denovo.activities.ItemActivity;
import com.denovo.denovo.adapters.RVAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
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

        //instantiate the database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //find views from xml
        profilePic = (TextView) rootView.findViewById(R.id.prof_pic);
        final TextView userNameTextView = (TextView) rootView.findViewById(R.id
                .user_name_text_view);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //get the name of the current user
                    name = user.getDisplayName();
                    //display the name of the current user
                    userNameTextView.setText(name);
                    //get the uid of the current user
                    uid = user.getUid();

                    //add value event listener on the current user in the users branch of the database
                    mDatabase.child("users").orderByKey().equalTo(uid)
                            .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                //create user from data from the database
                                User user = userSnapshot.getValue(User.class);

                                //display the current user's initials in their profile pic
                                profilePic.setText(user.getInitials());
                                //get the color of the user's profile pic
                                String color = user.getColor();
                                //set the background color of the current user's profile pic
                                switch (color) {
                                    case "red":
                                        profilePic.setBackgroundResource(R.drawable.profile_red);
                                        break;
                                    case "pink":
                                        profilePic.setBackgroundResource(R.drawable.profile_pink);
                                        break;
                                    case "purple":
                                        profilePic.setBackgroundResource(R.drawable.profile_purple);
                                        break;
                                    case "blue":
                                        profilePic.setBackgroundResource(R.drawable.profile_blue);
                                        break;
                                    case "teal":
                                        profilePic.setBackgroundResource(R.drawable.profile_teal);
                                        break;
                                    case "green":
                                        profilePic.setBackgroundResource(R.drawable.profile_green);
                                        break;
                                    case "yellow":
                                        profilePic.setBackgroundResource(R.drawable.profile_yellow);
                                        break;
                                    case "orange":
                                        profilePic.setBackgroundResource(R.drawable.profile_orange);
                                        break;
                                    case "gray":
                                        profilePic.setBackgroundResource(R.drawable.profile_gray);
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    //add value event listener to listen for changes in the current user's wishList
                    mDatabase.child("users").child(uid).child("wishList").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //create a new arrayList to store the keys of the items that are in the current user's wishList
                            mWishListKeys = new ArrayList<>();
                            for (DataSnapshot keySnapshot : dataSnapshot.getChildren()) {
                                //get each key from the database
                                String key = keySnapshot.getValue(String.class);
                                //add each key to the arrayList
                                mWishListKeys.add(key);
                            }

                            //update the view to display the updated wishList
                            checkWishListEmpty();

                            //create the wishList from the arrayList of wishListKeys
                            getWishList(new OnDataReceivedListener() {
                                @Override
                                public void onStart(int listSize) {
                                    mItemsLeft = listSize;
                                }

                                @Override
                                public void onNext() {
                                    //iterate through wishListKeys and update the adapter when finished
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

        //create new arrayLists
        mWishListKeys = new ArrayList<>();
        mWishList = new ArrayList<>();

        //find emptyWishList from xml
        emptyWishList = (TextView) rootView.findViewById(R.id.empty_wish_list);

        //find wishListRV from xml
        wishListRV = (RecyclerView) rootView.findViewById(R.id.wishlist_rv);
        //attach a WrapContentLinearLayoutManager to the wishListRV
        llm = new WrapContentLinearLayoutManager(getContext());
        wishListRV.setLayoutManager(llm);

        //hook up RVAdapter to wishListRV
        mAdapter = new RVAdapter(mWishList);
        wishListRV.setAdapter(mAdapter);

        mAdapter.setItemClickCallback(this);

        ((SimpleItemAnimator) wishListRV.getItemAnimator()).setSupportsChangeAnimations(false);

        //update the view to display the wishList or the emptyWishList screen if the wishList is empty
        checkWishListEmpty();

        //find editProfile from xml
        ImageView editProfile = (ImageView) rootView.findViewById(R.id.edit_profile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create new dialogue
                final Dialog dialog = new Dialog(getContext());
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_edit_profile);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                //find views from xml
                View red = dialog.findViewById(R.id.red);
                View pink = dialog.findViewById(R.id.pink);
                View purple = dialog.findViewById(R.id.purple);
                View blue = dialog.findViewById(R.id.blue);
                View teal = dialog.findViewById(R.id.teal);
                View green = dialog.findViewById(R.id.green);
                View yellow = dialog.findViewById(R.id.yellow);
                View orange = dialog.findViewById(R.id.orange);
                View gray = dialog.findViewById(R.id.gray);

                //for each color swatch, if it is clicked, update the user's profile pick with the selected color and dismiss the dialog
                red.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateProfileColor("red");
                        dialog.dismiss();
                    }
                });

                pink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateProfileColor("pink");
                        dialog.dismiss();
                    }
                });

                purple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateProfileColor("purple");
                        dialog.dismiss();
                    }
                });

                blue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateProfileColor("blue");
                        dialog.dismiss();
                    }
                });

                teal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateProfileColor("teal");
                        dialog.dismiss();
                    }
                });

                green.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateProfileColor("green");
                        dialog.dismiss();
                    }
                });

                yellow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateProfileColor("yellow");
                        dialog.dismiss();
                    }
                });

                orange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateProfileColor("orange");
                        dialog.dismiss();
                    }
                });

                gray.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateProfileColor("gray");
                        dialog.dismiss();
                    }
                });
            }
        });

        return rootView;
    }

    /**
     * If the wishList is empty hide the wishListRV and display the emptyWishList view
     * if not hide the emptyWishList view, and display the wishListRV
     */
    private void checkWishListEmpty() {
        if (mWishListKeys == null || mWishListKeys.isEmpty()) {
            //if the wishList is empty, hide wishListRV and display emptyWishList
            wishListRV.setVisibility(View.GONE);
            emptyWishList.setVisibility(View.VISIBLE);
        } else {
            //else display wishListRV and hide emptyWishList
            wishListRV.setVisibility(View.VISIBLE);
            emptyWishList.setVisibility(View.GONE);
        }
    }

    /**
     * Construct the wishlist of items from the wishListKeys arrayList
     *
     * @param listener is the dataReceivedListener
     */
    private void getWishList(final OnDataReceivedListener listener) {
        //create new wishlist;
        mWishList = new ArrayList<>();
        //pass is the size of the wishListKeys array to the listener.onStart method
        listener.onStart(mWishListKeys.size());
        for (String key : mWishListKeys) {
            //for every item key, add an event listener on the item that it is referencing
            mDatabase.child("items").orderByKey().equalTo(key)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                                //create item from data from the database
                                Item item = itemSnapshot.getValue(Item.class);
                                //add the item to the wishList
                                mWishList.add(item);
                                //move on to the the next key in the wishListKeys arrayList
                                listener.onNext();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    /**
     * Update the current user's profile pic color
     *
     * @param color is the name of the color that the user has selected to be their profile pic color
     */
    public void updateProfileColor(final String color) {
        //get reference to the color of the current user
        DatabaseReference colorRef = mDatabase.child("users").child(uid).child("color");
        colorRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                //set the user's color to the color passed into this method
                mutableData.setValue(color);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    /**
     * When an item card is clicked, launch ItemActivity
     *
     * @param p is the position of the wishListRV
     */
    @Override
    public void onItemClick(int p) {
        //get the item at the current position of the wishListRV
        Item item = mWishList.get(p);

        //launch ItemActivity and pass in the item id
        Intent i = new Intent(getActivity(), ItemActivity.class);
        i.putExtra("item", item.getId());

        startActivityForResult(i, 1);
    }

    /**
     * When the wantItBtn is clicked, add the item to the user's wishList
     *
     * @param p is the position of the wishListRV
     */
    @Override
    public void onWantItBtnClick(int p) {
        //get the id of the item at the current position of the wishListRV
        String itemId = mWishList.get(p).getId();
        //add or remove the item from the user's wishList
        mWishList.get(p).onAddedToWishList(uid, itemId);
    }

    /**
     * Ignore the offer button in the AccountFragment
     *
     * @param p is the position of the wishListRV
     */
    @Override
    public void onOfferBtnClick(int p) {

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
        //attach the authStateListener onStart
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            //detach the authStateListener onStop
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
