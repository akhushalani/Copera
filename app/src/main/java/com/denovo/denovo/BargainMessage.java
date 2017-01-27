package com.denovo.denovo;

import java.util.Date;

/**
 * Created by abhinavkhushalani on 1/19/17.
 */

public class BargainMessage {
    private String mText;
    private String mUser;
    private long mTime;

    /**
     * Class Constructor
     *
     * @param text contains the text content of the message
     * @param user represents the user that sent the message
     */
    public BargainMessage(String text, String user) {
        mText = text;
        mUser = user;

        // Initialize to current time
        mTime = new Date().getTime();
    }

    public BargainMessage(){
        //required blank constructor
    }


    //getters and setters
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
