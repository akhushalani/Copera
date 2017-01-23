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

public class CreateChapter extends AppCompatActivity {

    private EditText editName;
    private EditText editLat;
    private EditText editLong;
    private EditText editLocation;
    private CustomButton createChapterButton;
    private Address location;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chapter);

        editName = (EditText) findViewById(R.id.edit_chapter_name);
        editLocation = (EditText) findViewById(R.id.edit_location);
        createChapterButton = (CustomButton) findViewById(R.id.btn_create_chapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();


        createChapterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChapter();
            }
        });
    }


    public void createChapter() {

        String name = editName.getText().toString();

        GeoPoint geoPoint = convertAddressToLatLong(editLocation.getText().toString());
        double latitude = geoPoint.getLatitude();
        double longitude = geoPoint.getLongitude();

        DatabaseReference childRef = mDatabase.child("chapters").push();

        Chapter chapter = new Chapter(name, latitude, longitude);
        childRef.setValue(chapter);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public GeoPoint convertAddressToLatLong(String streetAddress) {

        Geocoder coder = new Geocoder(this, Locale.getDefault());
        List<Address> address;
        GeoPoint geoPoint;

        try {
            address = coder.getFromLocationName(streetAddress, 5);
            if (address == null) {
                return null;
            }
            location = address.get(0);


        } catch (IOException e) {

        }

        geoPoint = new GeoPoint();
        geoPoint.setLatitude(location.getLatitude());
        geoPoint.setLongitude(location.getLongitude());

        return geoPoint;
    }

}


