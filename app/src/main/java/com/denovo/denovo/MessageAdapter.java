package com.denovo.denovo;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by abhinavkhushalani on 1/19/17.
 */

public class MessageAdapter extends ArrayAdapter<BargainMessage> {
    private String mUid;

    public MessageAdapter(Activity context, ArrayList<BargainMessage> messages, String uid) {
        super(context, 0, messages);
        mUid = uid;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View messageView = convertView;

        BargainMessage currentMessage = getItem(position);
        String messageText = currentMessage.getText();

        if (messageView == null) {
            if (mUid.equals(currentMessage.getUser())) {
                messageView = LayoutInflater.from(getContext()).inflate(
                        R.layout.message_sent, parent, false);
                TextView sentText = (TextView) messageView.findViewById(R.id.sent_text);
                sentText.setText(messageText);
            } else {
                messageView = LayoutInflater.from(getContext()).inflate(
                        R.layout.message_received, parent, false);
                TextView receivedText = (TextView) messageView.findViewById(R.id.received_text);
                receivedText.setText(messageText);
            }
        }

        return messageView;
    }
}
