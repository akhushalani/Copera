package com.denovo.denovo.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.denovo.denovo.R.id.map;


public class ManageChapterActivity extends AppCompatActivity implements OnMapReadyCallback {

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
                    //create User object from the information read from the db
                    User user = userSnapshot.getValue(User.class);
                    //get the chapterKey of the user
                    chapterKey = user.getChapterKey();

                    //set a ValueEventListener to listen for changes in the chapter that the user created
                    mDatabase.child("chapters").orderByKey().equalTo(chapterKey).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot chapterSnapshot : dataSnapshot.getChildren()) {
                                //create Chapter object from the information read from the db
                                Chapter chapter = chapterSnapshot.getValue(Chapter.class);

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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //in case of fail to access database do nothing
            }
        });
        if (chapterKey != null && chapterKey != "") {
            mDatabase.child("chapters").child(chapterKey).child("itemList").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mItemListKeys = new ArrayList<>();
                    for (DataSnapshot keySnapshot : dataSnapshot.getChildren()) {
                        String key = keySnapshot.getValue(String.class);
                        mItemListKeys.add(key);
                    }
                    checkItemListEmpty();
                    getItemList(new OnDataReceivedListener() {
                        @Override
                        public void onStart(int listSize) {
                            mItemsLeft = listSize;
                        }

                        @Override
                        public void onNext() {
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
        mItemList = new ArrayList<>();
        mItemListKeys = new ArrayList<>();

        emptyItemList = (TextView) findViewById(R.id.empty_item_list);

        itemListRV = (RecyclerView) findViewById(R.id.chapter_items_rv);
        llm = new WrapContentLinearLayoutManager(this);
        itemListRV.setLayoutManager(llm);

        mAdapter = new RVAdapter(mItemList);
        itemListRV.setAdapter(mAdapter);

        ((SimpleItemAnimator) itemListRV.getItemAnimator()).setSupportsChangeAnimations(false);

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

    private void checkItemListEmpty() {
        if (mItemListKeys == null || mItemListKeys.isEmpty()) {
            itemListRV.setVisibility(View.GONE);
            emptyItemList.setVisibility(View.VISIBLE);
        } else {
            itemListRV.setVisibility(View.VISIBLE);
            emptyItemList.setVisibility(View.GONE);
        }
    }

    private void getItemList(final OnDataReceivedListener listener) {
        mItemList = new ArrayList<>();
        listener.onStart(mItemListKeys.size());
        for (String key : mItemListKeys) {
            mDatabase.child("items").orderByKey().equalTo(key)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                                Item item = itemSnapshot.getValue(Item.class);
                                mItemList.add(item);
                                listener.onNext();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }


}



