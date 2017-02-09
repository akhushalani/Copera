package com.denovo.denovo;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PreferencesActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String email;
    private String uid;
    private boolean ownsChapter;

    private View.OnClickListener createChapterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(PreferencesActivity.this, CreateChapterActivity.class);
            startActivity(intent);
            finish();
        }
    };

    private View.OnClickListener manageChapterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(PreferencesActivity.this, ManageChapterActivity.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        ImageView btnBack = (ImageView) findViewById(R.id.back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesActivity.this.finish();
            }
        });

        findViewById(R.id.settings).setVisibility(View.GONE);
        findViewById(R.id.search).setVisibility(View.GONE);
        findViewById(R.id.next).setVisibility(View.GONE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email = user.getEmail();
            uid = user.getUid();
        }

        TextView usernameTextView = (TextView) findViewById(R.id.username_text_view);
        usernameTextView.setText(email);

        Button signOutButton = (Button) findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent homeIntent = new Intent(PreferencesActivity.this, MainActivity.class);
                startActivity(homeIntent);
            }
        });

        final Button chapterBtn = (Button) findViewById(R.id.chapter_btn);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").orderByKey().equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    ownsChapter = user.getOwnsChapter();
                }
                if (ownsChapter) {
                    chapterBtn.setText("Manage Your Chapter");
                    chapterBtn.setOnClickListener(manageChapterClickListener);
                } else {
                    chapterBtn.setText("Create a Chapter");
                    chapterBtn.setOnClickListener(createChapterClickListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //in case of fail to access database do nothing
            }
        });


    }


}
