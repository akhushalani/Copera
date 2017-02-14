package com.denovo.denovo;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CreateChapterActivity extends AppCompatActivity {

    private static final String TAG = "CreateChapterActivity";
    private EditText editName;
    private CustomButton createChapterButton;
    private LatLng chapterLatLng;
    private Locale chapterLocale;
    private PlaceAutocompleteFragment mAutocompleteFragment;
    private DatabaseReference mDatabase;
    private String uid;
    private String chapterCity;
    private String chapterState;
    private String chapterAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chapter);

        //Get uid of the current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        //Set the action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        //enable the back button
        ImageView btnBack = (ImageView) findViewById(R.id.back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateChapterActivity.this.finish();
            }
        });

        //hide unused action bar icons
        findViewById(R.id.settings).setVisibility(View.GONE);
        findViewById(R.id.search).setVisibility(View.GONE);
        findViewById(R.id.next).setVisibility(View.GONE);

        //find views from xml
        editName = (EditText) findViewById(R.id.edit_chapter_name);
        createChapterButton = (CustomButton) findViewById(R.id.btn_create_chapter);
        mAutocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        //get reference to db
        mDatabase = FirebaseDatabase.getInstance().getReference();

        createChapterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //disable button if a field is empty
                if (!validateForm()) {
                    createChapterButton.setEnabled(false);
                    return;
                }
                createChapter();
            }
        });


        mAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                //get the latitude and longitude of the selected place
                chapterLatLng = place.getLatLng();
                //get the locale of the selected chapter
                chapterLocale = place.getLocale();
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        //filter results for only places in the US
        AutocompleteFilter countryFilter = new AutocompleteFilter.Builder().setCountry("US").build();
        mAutocompleteFragment.setFilter(countryFilter);

    }

    /**
     * Create a new Chapter and upload it to the db
     */
    public void createChapter() {

        //get string from edit Text
        String name = editName.getText().toString();

        //get reference to chapter branch of database and generate a unique key for the chapter
        DatabaseReference childRef = mDatabase.child("chapters").push();

        reverseGeocode();

        //create a new Chapter object with the inputted data and write to the db
        Chapter chapter = new Chapter(name, chapterAddress, chapterLatLng.latitude, chapterLatLng.longitude);
        childRef.setValue(chapter);

        //get reference to the current user
        DatabaseReference userRef = mDatabase.child("users").child(uid);
        userRef.child("ownsChapter").setValue(true);

        //get the generated key and assign it to the user's chapterKey
        userRef.child("chapterKey").setValue(childRef.getKey());

        //finish CreateChapterActivity and open ManageChapterActivity
        Intent intent = new Intent(this, ManageChapterActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Method to check if all fields are filled and valid
     *
     * @return valid
     */
    private boolean validateForm() {
        boolean valid = true;

        //if the editName field is empty set valid to false
        if (TextUtils.isEmpty(editName.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Enter Chapter Name!", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        //if no place is selected from the PlaceAutCompleteFragment set valid to false;
        if (chapterLatLng == null) {
            Toast.makeText(getApplicationContext(), "Enter a location for your chapter!", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    /**
     * Translate the chapter's LatLng to address String formatted as city, state
     *
     * @return the formatted address String
     */
    private String reverseGeocode() {
        List<Address> addresses = null;
        //create new Geocoder to translate LatLng to address
        Geocoder geocoder = new Geocoder(this, chapterLocale);
        try {
            addresses = geocoder.getFromLocation(chapterLatLng.latitude, chapterLatLng.longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                //get the city of the address
                chapterCity = address.getLocality();
                //get the state of the address
                chapterState = address.getAdminArea();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //concatenate the parts of the address into a full address
        chapterAddress = chapterCity + ", " + chapterState;
        return chapterAddress;
    }

}


