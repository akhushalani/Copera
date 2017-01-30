package com.denovo.denovo;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * Created by abhinavkhushalani on 1/30/17.
 */

public interface OnDataReceivedListener {
    void onStart(int listSize);
    void onNext();
}
