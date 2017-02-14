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
            //finish PreferencesActivity and start CreateChapterActivity
            Intent intent = new Intent(PreferencesActivity.this, CreateChapterActivity.class);
            startActivity(intent);
            finish();
        }
    };

    private View.OnClickListener manageChapterClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //finish PreferencesActivity and start ManageChapterActivity
            Intent intent = new Intent(PreferencesActivity.this, ManageChapterActivity.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        //set the action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        //hook up back button
        ImageView btnBack = (ImageView) findViewById(R.id.back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesActivity.this.finish();
            }
        });

        //hide unused action bar icons
        findViewById(R.id.settings).setVisibility(View.GONE);
        findViewById(R.id.search).setVisibility(View.GONE);
        findViewById(R.id.next).setVisibility(View.GONE);

        //get the unique id and email of the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email = user.getEmail();
            uid = user.getUid();
        }

        //set the usernameTextView
        TextView usernameTextView = (TextView) findViewById(R.id.username_text_view);
        usernameTextView.setText(email);

        Button signOutButton = (Button) findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sign the user out
                FirebaseAuth.getInstance().signOut();
                //finish PreferencesActivity and start MainActivity
                Intent homeIntent = new Intent(PreferencesActivity.this, MainActivity.class);
                startActivity(homeIntent);
            }
        });

        final Button chapterBtn = (Button) findViewById(R.id.chapter_btn);

        //instantiate the database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //add valueEventListener to the current user in the users branch of the database
        mDatabase.child("users").orderByKey().equalTo(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    //create new User from data read from the database
                    User user = userSnapshot.getValue(User.class);
                    ownsChapter = user.getOwnsChapter();
                }
                if (ownsChapter) {
                    //if the user owns a chapter change the text and wire the button to the ManageChapterActivity
                    chapterBtn.setText("Manage Your Chapter");
                    chapterBtn.setOnClickListener(manageChapterClickListener);
                } else {
                    //else wire the button to the CreateChapterActivity
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
