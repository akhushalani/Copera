package com.denovo.denovo.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by abhinavkhushalani on 11/4/16.
 */

public class Item implements Parcelable {

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
    private static final String TAG = "Item";
    private String mName;
    private String mImageFileName;
    private String mYardSale;
    private String mDonor;
    private double mPrice;
    private int mRating;
    private String mDescription;
    private int mWishListNum;
    private ArrayList<String> mWishListUsers;
    private FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private StorageReference mStorageRef;


    public Item() {

    }

    /**
     * Class Constructor
     *
     * @param name          is the item name
     * @param imageFileName is the item image
     * @param yardSale      is the yardsale the item is donated to
     * @param donor         is the id of the donor that donated the item
     * @param price         is the asking price of the item
     * @param rating        is the item condition rating
     * @param description   is a descfiption of the item
     * @param wishListUsers is an array of the unique user ids that have wishlisted the item
     */
    public Item(String name, String imageFileName, String yardSale, String donor, double price, int
            rating, String description, ArrayList<String>
            wishListUsers) {
        mName = name;
        mImageFileName = imageFileName;
        mYardSale = yardSale;
        mDonor = donor;
        mPrice = price;
        mRating = rating;
        mDescription = description;
        mWishListNum = 0;
        mWishListUsers = wishListUsers;
        mStorageRef = mStorage.getReferenceFromUrl("gs://denovo-4024e" +
                ".appspot.com/images/" + imageFileName);
    }

    /**
     * Class Constructor
     *
     * @param in is the parcel that is passed to the constructor
     */
    private Item(Parcel in) {
        mName = in.readString();
        mImageFileName = in.readString();
        mYardSale = in.readString();
        mDonor = in.readString();
        mPrice = in.readDouble();
        mRating = in.readInt();
        mDescription = in.readString();
        mWishListNum = in.readInt();
        mWishListUsers = new ArrayList<>();
        in.readStringList(mWishListUsers);
        mStorageRef = mStorage.getReferenceFromUrl("gs://denovo-4024e" +
                ".appspot.com/images/" + mImageFileName);
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
        mImageFileName = imageFileName;
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

    public String getDonor() {
        return mDonor;
    }

    public void setDonor(String donor) {
        mDonor = donor;
    }

    public double getPrice() {
        return mPrice;
    }

    public void setPrice(double price) {
        mPrice = price;
    }

    public String formatPrice() {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        return format.format(mPrice);
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

    public int getWishListNum() {
        return mWishListNum;
    }

    @Exclude
    public void setWishListNum(boolean value) {
        if (value) {
            mWishListNum++;
        } else {
            mWishListNum--;
        }
    }

    public void setWishListNum(int wishList) {
        mWishListNum = wishList;
    }

    public ArrayList<String> getWishListUsers() {
        return mWishListUsers;
    }

    public void setWishListUsers(ArrayList<String> wishListUsers) {
        if (wishListUsers == null) {
            mWishListUsers = new ArrayList<>();
        } else {
            mWishListUsers = wishListUsers;
        }
    }

    @Exclude
    public String getId() {
        return mImageFileName.substring(0, mImageFileName.length() - 4);
    }


    public void onAddedToWishList(final String uid, final String itemId) {
        //instantiate the database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        //get a reference to the database
        DatabaseReference itemRef = databaseRef.child("items").child(itemId);

        itemRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                final Item i = mutableData.getValue(Item.class);
                if (i == null) {
                    return Transaction.success(mutableData);
                }

                if (i.getWishListUsers() == null) {
                    i.setWishListUsers(new ArrayList<String>());
                }

                if (i.getWishListUsers().contains(uid)) {
                    i.setWishListNum(false);
                    ArrayList<String> tempList = i.getWishListUsers();
                    tempList.remove(uid);
                    i.setWishListUsers(tempList);
                    setWishListNum(false);
                    setWishListUsers(tempList);
                } else {
                    i.setWishListNum(true);
                    ArrayList<String> tempList = i.getWishListUsers();
                    tempList.add(uid);
                    i.setWishListUsers(tempList);
                    setWishListNum(true);
                    setWishListUsers(tempList);
                }

                mutableData.setValue(i);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d("Item", "itemTransaction:onComplete:" + databaseError);
            }
        });

        DatabaseReference userRef = databaseRef.child("users").child(uid);
        Log.v(TAG, uid);
        userRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User u = mutableData.getValue(User.class);
                if (u == null) {
                    return Transaction.success(mutableData);
                }

                if (u.getWishList() == null) {
                    u.setWishList (new ArrayList<String>());
                }
                if (u.getWishList().contains(itemId)) {
                    ArrayList<String> tempList = u.getWishList();
                    tempList.remove(itemId);
                    u.setWishList(tempList);
                } else {
                    ArrayList<String> tempList = u.getWishList();
                    tempList.add(itemId);
                    u.setWishList(tempList);
                }

                mutableData.setValue(u);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d("Item", "userTransaction:onComplete:" + databaseError);
            }
        });
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
        out.writeInt(mWishListNum);
        out.writeStringList(mWishListUsers);
    }
}
