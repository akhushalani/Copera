package com.denovo.denovo;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private boolean mIsDropped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    //if user is not signed in then send them to login activity
                    Intent homeIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(homeIntent);
                }
            }
        };

        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        findViewById(R.id.back).setVisibility(View.GONE);
        findViewById(R.id.next).setVisibility(View.GONE);

        /* mIsDropped = false;
        mTransparentPanel = (TransparentPanel) findViewById(R.id.transparent_panel);
        mTransparentPanel.setVisibility(View.GONE);

        mDropDownImageView = (DropDownImageView) findViewById(R.id.drop_down_image_view);
        TextView title = (TextView) findViewById(R.id.title);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDropDownImageView.morph();
                setTransparentPanel();
            }
        }); */

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        PageAdapter adapter = new PageAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_account);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.donate_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, DonateActivity.class);
                startActivity(i);
                /* writeNewItem(new Item("Book", "book.png", "Dulaney FBLA", "Abhinav Khushalani", 1.5, 4,
                        "What did Harry" +
                                " Potter know about magic? He was stuck with the decidedly un-magical Dursleys, " +
                                "who hated him. He slept in a closet and ate their leftovers. But an owl " +
                                "messenger changes all that, with an invitation to attend the Hogwarts School for" +
                                " Wizards and Witches, where it turns out Harry is already famous.",
                        new ArrayList<Question>())); */
            }
        });

        ImageView preferencesButton = (ImageView) findViewById(R.id.settings);
        preferencesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, PreferencesActivity.class);
                startActivity(i);
            }
        });
    }

    private void writeNewItem(Item item) {
        DatabaseReference childRef = mDatabase.child("items").push();
        childRef.setValue(item);
    }

    private void setTransparentPanel() {
        if (mIsDropped) {

            mIsDropped = false;
        } else {

            mIsDropped = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
