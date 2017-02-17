package com.denovo.denovo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.denovo.denovo.adapters.RVAdapter;
import com.denovo.denovo.interfaces.OnDataReceivedListener;
import com.denovo.denovo.models.Chapter;
import com.denovo.denovo.R;
import com.denovo.denovo.models.Item;
import com.denovo.denovo.models.User;
import com.denovo.denovo.views.WrapContentLinearLayoutManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

import static android.R.attr.path;
import static android.R.attr.value;
import static com.denovo.denovo.R.id.map;


public class ManageChapterActivity extends AppCompatActivity implements OnMapReadyCallback, RVAdapter.ItemClickCallback {

    private static final String TAG = "ManageChapterActivity";
    String mChapterName;
    private GoogleMap mMap;
    private TextView mChapterNameView;
    private double mChapterLat;
    private double mChapterLong;
    private LatLng mChapterLoc;
    private String uid;
    private String chapterKey;
    private DatabaseReference mDatabase;
    private ArrayList<String> mItemListKeys;
    private ArrayList<Item> mItemList;
    private WrapContentLinearLayoutManager llm;
    private RVAdapter mAdapter;
    private TextView emptyItemList;
    private int mItemsLeft;
    private RecyclerView itemListRV;
    private Chapter chapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_chapter);

        //set the action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        //enable the back button
        ImageView btnBack = (ImageView) findViewById(R.id.back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManageChapterActivity.this.finish();
            }
        });

        //hide unused action bar icons
        findViewById(R.id.settings).setVisibility(View.GONE);
        findViewById(R.id.search).setVisibility(View.GONE);
        findViewById(R.id.next).setVisibility(View.GONE);


        //find views from xml
        mChapterNameView = (TextView) findViewById(R.id.chapter_name_txt);

        //get the uid of the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        //get a reference to the db
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //set ValueEventListener to listen for changes in the current user's branch
        mDatabase.child("users").orderByKey().equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    //create User object from the information read from the database
                    User user = userSnapshot.getValue(User.class);
                    //get the chapterKey of the user
                    chapterKey = user.getChapterKey();

                    //set a ValueEventListener to listen for changes in the chapter that the user created
                    mDatabase.child("chapters").orderByKey().equalTo(chapterKey).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot chapterSnapshot : dataSnapshot.getChildren()) {
                                //create Chapter from data read from the database
                                chapter = chapterSnapshot.getValue(Chapter.class);

                                //get the name of the chapter
                                mChapterName = chapter.getName();
                                //get the latitude of the chapter
                                mChapterLat = chapter.getLatitude();
                                //get the longitude of the chapter
                                mChapterLong = chapter.getLongitude();
                            }

                            //display the chapter name in mChapterNameView
                            mChapterNameView.setText(mChapterName);

                            //find map from xml
                            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                    .findFragmentById(map);
                            mapFragment.getMapAsync(ManageChapterActivity.this);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //in case of fail to access database do nothing
                        }
                    });

                    //add value event listener to listen for changes to the itemList of the current chapter
                    mDatabase.child("chapters").child(chapterKey).child("itemList").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //create a new arrayList to store the keys of the items that are in the chapter's itemList
                            mItemListKeys = new ArrayList<>();
                            for (DataSnapshot keySnapshot : dataSnapshot.getChildren()) {
                                //get each key from the database
                                String key = keySnapshot.getValue(String.class);
                                //add each key to the arrayList
                                mItemListKeys.add(key);
                            }

                            //update the view to display the updated itemList
                            checkItemListEmpty();

                            //create the itemList from the arrayList of itemListKeys
                            getItemList(new OnDataReceivedListener() {
                                @Override
                                public void onStart(int listSize) {
                                    mItemsLeft = listSize;
                                }

                                @Override
                                public void onNext() {
                                    //iterate through itemListKeys and update the adapter when finished
                                    mItemsLeft--;
                                    if (mItemsLeft == 0) {
                                        mAdapter.swapDataSet(mItemList, true);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //in case of fail to access database do nothing
            }
        });

        //create new arrayLists
        mItemList = new ArrayList<>();
        mItemListKeys = new ArrayList<>();

        //find emptyItemList from xml
        emptyItemList = (TextView) findViewById(R.id.empty_item_list);

        //find itemListRV from xml
        itemListRV = (RecyclerView) findViewById(R.id.chapter_items_rv);
        //attach a WrapContentLinearLayoutManager to the itemListRV
        llm = new WrapContentLinearLayoutManager(this);
        itemListRV.setLayoutManager(llm);

        //hook up RVAdapter to itemListRV
        mAdapter = new RVAdapter(mItemList);
        itemListRV.setAdapter(mAdapter);
        mAdapter.setItemClickCallback(this);

        ((SimpleItemAnimator) itemListRV.getItemAnimator()).setSupportsChangeAnimations(false);

        //update the view to display the itemList or the emptyItemList screen if the itemList is empty
        checkItemListEmpty();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //disable gestures for the map
        mMap.getUiSettings().setAllGesturesEnabled(false);

        //create LatLng from the chapter's latitude and longitude
        mChapterLoc = new LatLng(mChapterLat, mChapterLong);

        //add marker a marker to the map
        mMap.addMarker(new MarkerOptions()
                .position(mChapterLoc)                               //position the marker at the Chapter's LatLng
                .icon(BitmapDescriptorFactory.defaultMarker(211)));  //set icon color to app's accent color

        CameraPosition cameraOriginPosition = new CameraPosition.Builder()
                .target(mChapterLoc)         // Sets the center of the map to the location of the chapter
                .zoom(13)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder

        //move the camera to the cameraOriginPosition in 1 seconds
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraOriginPosition), 1000, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                //when the animation is finished...
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(mChapterLoc)        // Sets the center of the map to the location of the chapter
                        .zoom(16)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                //move the camera to cameraPosition in 4 seconds
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 4000, null);
            }

            @Override
            public void onCancel() {
                //on cancel do nothing
            }
        });


    }

    /**
     * If the itemList is empty hide the itemListRV and display the emptyItemList view
     * if not hide the emptyItemList view, and display the itemListRV
     */
    private void checkItemListEmpty() {
        if (mItemListKeys == null || mItemListKeys.isEmpty()) {
            //if the itemList is empty, hide ItemList recyclerView and display emptyItemList
            itemListRV.setVisibility(View.GONE);
            emptyItemList.setVisibility(View.VISIBLE);
        } else {
            //else display itemList recyclerView and hide emptyItemList
            itemListRV.setVisibility(View.VISIBLE);
            emptyItemList.setVisibility(View.GONE);
        }
    }

    /**
     * Construct the itemList of items from the itemListKeys arrayList
     *
     * @param listener is the dataReceivedListener
     */
    private void getItemList(final OnDataReceivedListener listener) {
        //create new itemList
        mItemList = new ArrayList<>();
        //pass is the size of the itemListKeys array to the listener.onStart method
        listener.onStart(mItemListKeys.size());
        for (String key : mItemListKeys) {
            //for every item key, add an event listener on the item that it is referencing
            mDatabase.child("items").orderByKey().equalTo(key)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                                //create item from data from the database
                                Item item = itemSnapshot.getValue(Item.class);
                                //add the item to the itemList
                                mItemList.add(item);
                                //move on to the the next key in the itemListKeys arrayList
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
     * When an item card is clicked, launch ItemActivity
     *
     * @param p is the position of the wishListRV
     */
    @Override
    public void onItemClick(int p) {
        //get the item at the current position of the wishListRV
        Item item = mItemList.get(p);

        //launch ItemActivity and pass in the item id
        Intent i = new Intent(ManageChapterActivity.this, ItemActivity.class);
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
        //get the id of the item at the current position of the itemListRV
        String itemId = mItemList.get(p).getId();
        //add or remove the item from the user's wishList
        mItemList.get(p).onAddedToWishList(uid, itemId);
    }

    /**
     * When the offer button is clicked, delete the item
     *
     * @param p is the position of the wishListRV
     */
    @Override
    public void onOfferBtnClick(int p) {
        //get the id of the item at the current position of the itemListRV
        final String itemId = mItemList.get(p).getId();
        //delete the item from the database
        mDatabase.child("items").child(itemId).setValue(null);
        //remove comments on the deleted item
        mDatabase.child("comments").child(itemId).setValue(null);
        //remove the item from the chapter itemList
        chapter.onItemDeleted(chapterKey, itemId);
        //remove the item from user's wishLists

        //update the view if the itemList is empty
        checkItemListEmpty();
    }


}



