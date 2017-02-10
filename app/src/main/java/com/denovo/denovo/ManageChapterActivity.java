package com.denovo.denovo;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.R.attr.id;
import static com.denovo.denovo.R.id.map;


public class ManageChapterActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "ManageChapterActivity:";
    String mChapterName;
    private GoogleMap mMap;
    private TextView mChapterNameView;
    private double mChapterLat;
    private double mChapterLong;
    private LatLng mChapterLoc;
    private String uid;
    private String chapterKey;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_chapter);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        ImageView btnBack = (ImageView) findViewById(R.id.back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManageChapterActivity.this.finish();
            }
        });

        findViewById(R.id.settings).setVisibility(View.GONE);
        findViewById(R.id.search).setVisibility(View.GONE);
        findViewById(R.id.next).setVisibility(View.GONE);
        mChapterNameView = (TextView) findViewById(R.id.chapter_name_txt);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").orderByKey().equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    chapterKey = user.getChapterKey();

                    mDatabase.child("chapters").orderByKey().equalTo(chapterKey).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot chapterSnapshot : dataSnapshot.getChildren()) {
                                Chapter chapter = chapterSnapshot.getValue(Chapter.class);
                                mChapterName = chapter.getName();
                                mChapterLat = chapter.getLatitude();
                                mChapterLong = chapter.getLongitude();
                            }

                            mChapterNameView.setText(mChapterName);
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
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mChapterLoc = new LatLng(mChapterLat, mChapterLong);
        mMap.addMarker(new MarkerOptions()
                .position(mChapterLoc)
                .icon(BitmapDescriptorFactory.defaultMarker(211)));

        CameraPosition cameraOriginPosition = new CameraPosition.Builder()
                .target(mChapterLoc)         // Sets the center of the map to the location of the chapter
                .zoom(13)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraOriginPosition), 2000, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(mChapterLoc)         // Sets the center of the map to the location of the chapter
                        .zoom(16)                   // Sets the zoom
                        .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 4000, null);
            }

            @Override
            public void onCancel() {

            }
        });


    }


}



