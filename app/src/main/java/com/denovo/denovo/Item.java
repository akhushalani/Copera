package com.denovo.denovo;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by abhinavkhushalani on 11/4/16.
 */

public class Item implements Parcelable {
    private String mName;
    private String mImageFileName;
    private String mYardSale;
    private String mDonor;
    private double mPrice;
    private int mRating;
    private String mDescription;
    private int mWantIt;
    private ArrayList<Question> mQuestions;
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private StorageReference mStorageRef;

    public Item() {

    }

    public Item(String name, String imageFileName, String yardSale, String donor, double price, int
            rating, String description, ArrayList<Question> questions) {
        mName = name;
        mImageFileName = imageFileName;
        mYardSale = yardSale;
        mDonor = donor;
        mPrice = price;
        mRating = rating;
        mDescription = description;
        mWantIt = 0;
        mQuestions = questions;
        mStorageRef = mStorage.getReferenceFromUrl("gs://denovo-4024e" +
                ".appspot.com/images/" + imageFileName);
    }

    //Getters and Setters
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageFileName() {
        return mImageFileName;
    }

    public void setImageFileName(String imageFileName) {
        mImageFileName =  imageFileName;
        mStorageRef = mStorage.getReferenceFromUrl("gs://denovo-4024e.appspot.com/images/"
                + imageFileName);
    }

    public void downloadImage(Context context, ImageView imageView) {
        Glide.with(context).using(new FirebaseImageLoader()).load(mStorageRef).into(imageView);
    }

    public String getYardSale() {
        return mYardSale;
    }

    public void setYardSale(String yardSale) {
        mYardSale = yardSale;
    }

    public double getPrice() {
        return mPrice;
    }

    public String formatPrice() {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        return format.format(mPrice);
    }

    public void setPrice(double price) {
        mPrice = price;
    }

    public int getRating() {
        return mRating;
    }

    public void setRating(int rating) {
        mRating = rating;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public int getWantIt() {
        return mWantIt;
    }

    public void setWantIt(int wantIt){
        mWantIt = wantIt;
    }

    public void setWantIt() {
        mWantIt++;
    }

    public ArrayList<Question> getQuestions() {
        return mQuestions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        mQuestions = questions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mName);
        out.writeString(mImageFileName);
        out.writeString(mYardSale);
        out.writeString(mDonor);
        out.writeDouble(mPrice);
        out.writeInt(mRating);
        out.writeString(mDescription);
        out.writeInt(mWantIt);
        out.writeTypedList(mQuestions);
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    private Item(Parcel in) {
        mName = in.readString();
        mImageFileName = in.readString();
        mYardSale = in.readString();
        mDonor = in.readString();
        mPrice = in.readDouble();
        mRating = in.readInt();
        mDescription = in.readString();
        mWantIt = in.readInt();
        mQuestions = new ArrayList<>();
        in.readTypedList(mQuestions, Question.CREATOR);
        mStorageRef = mStorage.getReferenceFromUrl("gs://denovo-4024e" +
                ".appspot.com/images/" + mImageFileName);
    }
}
