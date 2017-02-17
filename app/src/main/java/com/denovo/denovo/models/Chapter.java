package com.denovo.denovo.models;


import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;

import static android.R.attr.data;

public class Chapter {

    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private ArrayList<String> itemList;
    private ArrayList<String> officerList;
    private String key;
    private DatabaseReference mDatabase;

    public Chapter() {
        //required default constructor
    }

    /**
     * Class Constructor
     *
     * @param name      is the chapter name
     * @param latitude  is the latitude coordinate of the chapter's location
     * @param longitude is the longitude coordinate of the chapter's location
     */
    public Chapter(String name, String address, double latitude, double longitude,
                   ArrayList<String> itemList, ArrayList<String> officerList, String key) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.itemList = itemList;
        this.officerList = officerList;
        this.key = key;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    //Setters and Getters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public ArrayList<String> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<String> itemList) {
        this.itemList = itemList;
    }

    public ArrayList<String> getOfficerList() {
        return officerList;
    }

    public void setOfficerList(ArrayList<String> officerList) {
        this.officerList = officerList;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    /**
     * Removes the item from the chapter's itemList
     *
     * @param chapterKey is the id of the chapter that the item belongs to
     * @param itemId     is the id of the deleted item
     */
    public void onItemDeleted(final String chapterKey, final String itemId) {

        DatabaseReference chapterRef = mDatabase.child("chapters").child(chapterKey);

        chapterRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                //create an chapter from the data
                final Chapter c = mutableData.getValue(Chapter.class);

                if (c.getItemList() == null) {
                    //if itemList is null, set itemList to an empty ArrayList
                    c.setItemList(new ArrayList<String>());
                }

                if (c.getItemList().contains(itemId)) {
                    //if the chapter contains the item, remove the item from the chapterItems arrayList
                    ArrayList<String> tempList = c.getItemList();
                    tempList.remove(itemId);
                    c.setItemList(tempList);
                    setItemList(tempList);
                }

                //update the chapter with the changes made
                mutableData.setValue(c);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    /**
     * Add a user to the officerList of a chapter and write changes to the database
     *
     * @param uid        is the unique id of the current user
     * @param chapterKey is the unique id of the chapter
     */
    public void onOfficerAdded(final String uid, final String chapterKey) {

        //get a reference to the chapter in the database
        DatabaseReference chapterRef = mDatabase.child("chapters").child(chapterKey);

        chapterRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                //create an chapter from the data
                final Chapter c = mutableData.getValue(Chapter.class);

                if (c.getOfficerList() == null) {
                    //if officerList is null, set officerList to an empty ArrayList
                    c.setOfficerList(new ArrayList<String>());
                }

                if (c.getOfficerList().contains(uid)) {
                    //if the user is already in the array, do nothing
                    return Transaction.success(mutableData);
                } else {
                    //else add the user to the officerList
                    ArrayList<String> tempList = c.getOfficerList();
                    tempList.add(uid);
                    c.setOfficerList(tempList);
                    setOfficerList(tempList);

                    //set the user's isOfficer to true and chapterKey to the chapter
                    DatabaseReference userRef = mDatabase.child("users").child(uid);
                    userRef.child("isOfficer").setValue("true");
                    userRef.child("chapterKey").setValue(chapterKey);
                }

                //update the item with the changes made
                mutableData.setValue(c);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
            }
        });
    }
}
