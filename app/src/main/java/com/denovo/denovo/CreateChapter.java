package com.denovo.denovo;

import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
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

public class CreateChapter extends AppCompatActivity {

    private static final String TAG = "CreateChapter";
    private EditText editName;
    private CustomButton createChapterButton;
    private LatLng chapterLatLng;
    private PlaceAutocompleteFragment mAutocompleteFragment;


    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chapter);

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


        int TYPE_SCHOOL = 82;
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setTypeFilter(TYPE_SCHOOL).build();
        AutocompleteFilter countryFiter = new AutocompleteFilter.Builder().setCountry("US").build();
        // mAutocompleteFragment.setFilter(typeFilter
        // mAutocompleteFragment.setFilter(countryFilter);



    }


    public void createChapter() {

        String name = editName.getText().toString();


        DatabaseReference childRef = mDatabase.child("chapters").push();

        Chapter chapter = new Chapter(name, chapterLatLng.latitude, chapterLatLng.longitude);
        childRef.setValue(chapter);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }



}


