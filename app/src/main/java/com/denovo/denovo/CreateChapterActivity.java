package com.denovo.denovo;

import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import static android.R.attr.name;
import static android.R.id.edit;
import static android.media.CamcorderProfile.get;
import static com.google.android.gms.location.places.Place.TYPE_SCHOOL;

public class CreateChapterActivity extends AppCompatActivity {

    private static final String TAG = "CreateChapterActivity";
    private EditText editName;
    private CustomButton createChapterButton;
    private LatLng chapterLatLng;
    private PlaceAutocompleteFragment mAutocompleteFragment;


    private DatabaseReference mDatabase;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chapter);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        ImageView btnBack = (ImageView) findViewById(R.id.back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateChapterActivity.this.finish();
            }
        });

        findViewById(R.id.settings).setVisibility(View.GONE);
        findViewById(R.id.search).setVisibility(View.GONE);
        findViewById(R.id.next).setVisibility(View.GONE);

        editName = (EditText) findViewById(R.id.edit_chapter_name);

        createChapterButton = (CustomButton) findViewById(R.id.btn_create_chapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        createChapterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChapter();
            }
        });

        mAutocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        mAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                chapterLatLng = place.getLatLng();
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        AutocompleteFilter countryFilter = new AutocompleteFilter.Builder().setCountry("US").build();
        mAutocompleteFragment.setFilter(countryFilter);



    }


    public void createChapter() {

        String name = editName.getText().toString();


        DatabaseReference childRef = mDatabase.child("chapters").push();

        Chapter chapter = new Chapter(name, chapterLatLng.latitude, chapterLatLng.longitude);
        childRef.setValue(chapter);

        DatabaseReference userRef = mDatabase.child("users").child(uid);
        userRef.child("ownsChapter").setValue(true);
        userRef.child("chapterKey").setValue(childRef.getKey());

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}


