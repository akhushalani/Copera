package com.denovo.denovo;

import java.util.Date;

/**
 * Created by abhinavkhushalani on 1/19/17.
 */

public class BargainMessage {
    private String mText;
    private String mUser;
    private long mTime;

    public BargainMessage(String text, String user) {
        mText = text;
        mUser = user;

        // Initialize to current time
        mTime = new Date().getTime();
    }

    public BargainMessage(){

    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getUser() {
        return mUser;
    }

    public void setUser(String user) {
        mUser = user;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }
}
