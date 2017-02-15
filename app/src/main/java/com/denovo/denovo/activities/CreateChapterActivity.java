package com.denovo.denovo.activities;

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

import com.denovo.denovo.models.Chapter;
import com.denovo.denovo.views.CustomButton;
import com.denovo.denovo.R;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateChapterActivity extends AppCompatActivity {

    private static final String TAG = "CreateChapterActivity";
    private EditText editName;
    private CustomButton createChapterButton;
    private LatLng chapterLatLng;
    private Locale chapterLocale;
    private PlaceAutocompleteFragment mAutocompleteFragment;
    private DatabaseReference mDatabase;
    private String uid;


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

        //create a new Chapter object with the inputted data and write to the db
        Chapter chapter = new Chapter(name, reverseGeocode(chapterLatLng), chapterLatLng.latitude, chapterLatLng.longitude);
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

        //Check to see if field is filled, if not display a toast to the user and set valid to false
        if (TextUtils.isEmpty(editName.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Enter Chapter Name!", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (chapterLatLng == null) {
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
    private String reverseGeocode(LatLng loc) {
        //create new Geocoder to translate LatLng to address
        Geocoder geocoder = new Geocoder(this);

        List<Address> addresses = null;
        String chapterCity = "";
        String chapterState = "";

        try {
            addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1);

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

        //HashMap to convert state names to their abbreviation
        Map<String, String> states = new HashMap<String, String>();
        states.put("Alabama", "AL");
        states.put("Alaska", "AK");
        states.put("Alberta", "AB");
        states.put("American Samoa", "AS");
        states.put("Arizona", "AZ");
        states.put("Arkansas", "AR");
        states.put("Armed Forces (AE)", "AE");
        states.put("Armed Forces Americas", "AA");
        states.put("Armed Forces Pacific", "AP");
        states.put("British Columbia", "BC");
        states.put("California", "CA");
        states.put("Colorado", "CO");
        states.put("Connecticut", "CT");
        states.put("Delaware", "DE");
        states.put("District Of Columbia", "DC");
        states.put("Florida", "FL");
        states.put("Georgia", "GA");
        states.put("Guam", "GU");
        states.put("Hawaii", "HI");
        states.put("Idaho", "ID");
        states.put("Illinois", "IL");
        states.put("Indiana", "IN");
        states.put("Iowa", "IA");
        states.put("Kansas", "KS");
        states.put("Kentucky", "KY");
        states.put("Louisiana", "LA");
        states.put("Maine", "ME");
        states.put("Manitoba", "MB");
        states.put("Maryland", "MD");
        states.put("Massachusetts", "MA");
        states.put("Michigan", "MI");
        states.put("Minnesota", "MN");
        states.put("Mississippi", "MS");
        states.put("Missouri", "MO");
        states.put("Montana", "MT");
        states.put("Nebraska", "NE");
        states.put("Nevada", "NV");
        states.put("New Brunswick", "NB");
        states.put("New Hampshire", "NH");
        states.put("New Jersey", "NJ");
        states.put("New Mexico", "NM");
        states.put("New York", "NY");
        states.put("Newfoundland", "NF");
        states.put("North Carolina", "NC");
        states.put("North Dakota", "ND");
        states.put("Northwest Territories", "NT");
        states.put("Nova Scotia", "NS");
        states.put("Nunavut", "NU");
        states.put("Ohio", "OH");
        states.put("Oklahoma", "OK");
        states.put("Ontario", "ON");
        states.put("Oregon", "OR");
        states.put("Pennsylvania", "PA");
        states.put("Prince Edward Island", "PE");
        states.put("Puerto Rico", "PR");
        states.put("Quebec", "QC");
        states.put("Rhode Island", "RI");
        states.put("Saskatchewan", "SK");
        states.put("South Carolina", "SC");
        states.put("South Dakota", "SD");
        states.put("Tennessee", "TN");
        states.put("Texas", "TX");
        states.put("Utah", "UT");
        states.put("Vermont", "VT");
        states.put("Virgin Islands", "VI");
        states.put("Virginia", "VA");
        states.put("Washington", "WA");
        states.put("West Virginia", "WV");
        states.put("Wisconsin", "WI");
        states.put("Wyoming", "WY");
        states.put("Yukon Territory", "YT");

        chapterState = states.get(chapterState);
        //concatenate the parts of the address into a full address
        String chapterAddress = chapterCity + ", " + chapterState;
        return chapterAddress;
    }

}


