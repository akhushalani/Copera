package com.denovo.denovo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by abhinavkhushalani on 12/1/16.
 */

public class ItemComment implements Parcelable {
    public static final Parcelable.Creator<ItemComment> CREATOR = new Parcelable.Creator<ItemComment>() {
        @Override
        public ItemComment createFromParcel(Parcel in) {
            return new ItemComment(in);
        }

        @Override
        public ItemComment[] newArray(int size) {
            return new ItemComment[size];
        }
    };
    private String mComment;

    public ItemComment() {
    }

    public ItemComment(String question) {
        mComment = question;
    }

    private ItemComment(Parcel in) {
        mComment = in.readString();
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
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
