package com.denovo.denovo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by abhinavkhushalani on 12/1/16.
 */

public class Comment implements Parcelable {
    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    private String mComment;
    private String mName;
    private String mDate;

    public Comment() {
    }

    public Comment(String comment, String uid, String date) {
        mComment = comment;
        mName = uid;
        mDate = date;
    }

    private Comment(Parcel in) {
        mComment = in.readString();
    }


    //setters and getters
    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }

    public String getName() {
        return mName;
    }

    public void setName(String uid) {
        mName = uid;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mComment);
    }
}
