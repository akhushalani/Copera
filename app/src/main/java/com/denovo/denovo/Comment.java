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
    private String mUid;
    private long mDate;

    public Comment() {
    }

    /**
     * Class Constructor
     *
     * @param comment is the text of the comment
     * @param uid     is the unique id of the user that posted the comment
     * @param date    is time that the comment is written
     */
    public Comment(String comment, String uid, long date) {
        mComment = comment;
        mUid = uid;
        mDate = date;
    }

    /**
     * Class Constructor
     *
     * @param in is the parcel that is passed to the constructor
     */
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

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long date) {
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
