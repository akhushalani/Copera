package com.denovo.denovo;

import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import static android.R.attr.id;

public class BargainActivity extends AppCompatActivity {

    private String uid;
    private String mUid;
    private String itemKey;
    private String chatName;
    private DatabaseReference mDatabase;
    private ImageView sendBtn;
    private ListView mChatView;
    private EditText msgEntry;
    private ArrayList<BargainMessage> mChat;
    private MessageAdapter mAdapter;
    private boolean msgValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bargain);

        mChat = new ArrayList<BargainMessage>();
        mChatView = (ListView) findViewById(R.id.chat_view);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        ImageView mBtnBack = (ImageView) findViewById(R.id.back);

        findViewById(R.id.settings).setVisibility(View.GONE);
        findViewById(R.id.search).setVisibility(View.GONE);
        findViewById(R.id.next).setVisibility(View.GONE);

        sendBtn = (ImageView) findViewById(R.id.send_button);
        sendBtn.setEnabled(false);

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BargainActivity.this.finish();
            }
        });

        itemKey = getIntent().getExtras().getString("item_key");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mUid = user.getUid();
        }

        uid = "BELpNBzmANVXGdzklxth5YkJMn92";

        chatName = uid + itemKey;

        mAdapter = new MessageAdapter(this, mChat, uid);

        mChatView.setAdapter(mAdapter);

        msgEntry = (EditText) findViewById(R.id.msg_entry);
        msgEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                msgValid = s.toString().trim().length() != 0;
                if (msgValid) {
                    sendBtn.setEnabled(true);
                } else {
                    sendBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BargainMessage message = new BargainMessage(msgEntry.getText().toString(), mUid);
                DatabaseReference childRef = mDatabase.child("items").child(itemKey)
                        .child("bargains").child(uid).push();
                childRef.setValue(message);
                msgEntry.setText("");
            }
        });

        Query chatQuery = mDatabase.child("items").child(itemKey).child("bargains").child(uid)
                .orderByChild("time");
        chatQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                BargainMessage message = dataSnapshot.getValue(BargainMessage.class);
                mChat.add(message);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
