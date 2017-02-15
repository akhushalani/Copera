package com.denovo.denovo;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import static com.denovo.denovo.R.layout.comment_item;

public class CommentActivity extends AppCompatActivity {

    private String uid;
    private String donorId;
    private String itemId;
    private ArrayList<Comment> mCommentList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        //get uid of current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        //set action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        //hook up back button
        ImageView btnBack = (ImageView) findViewById(R.id.back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentActivity.this.finish();
            }
        });

        //hide unused action bar icons
        findViewById(R.id.settings).setVisibility(View.GONE);
        findViewById(R.id.search).setVisibility(View.GONE);
        findViewById(R.id.next).setVisibility(View.GONE);

        //get information from the intent from ItemActivity
        Bundle data = getIntent().getExtras();
        itemId = data.getString("item");
        donorId = data.getString("donor_id");


        mCommentList = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        //find views from xml
        final LinearLayout commentFeed = (LinearLayout) findViewById(R.id.activity_comment_feed);
        final EditText commentEntry = (EditText) findViewById(R.id.activity_comment_entry);
        final ImageView sendCommentBtn = (ImageView) findViewById(R.id.activity_send_comment_btn);
        final LayoutInflater inflater = LayoutInflater.from(this);

        //Set childEventListener on the item to listen for changes
        mDatabase.child("comments").child(itemId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //get Comment object from db and add it to commentList
                mCommentList.add(dataSnapshot.getValue(Comment.class));

                //clear the feed
                commentFeed.removeAllViews();

                //inflate the feed with comment objects from commentList
                for (int i = 0; i < mCommentList.size(); i++) {
                    Comment currentComment = mCommentList.get(i);
                    View view = inflater.inflate(comment_item, commentFeed, false);

                    //if the comment is the first in the list, hide the divider
                    if (i == 0) {
                        view.findViewById(R.id.comment_divider).setVisibility(View.GONE);
                    }

                    //set the text of the current comment
                    TextView commentTextView = (TextView) view.findViewById(R.id.comment_text);
                    commentTextView.setText(currentComment.getComment());

                    //create userInfo arrayList and pass it along with the users uid to retrieveUserInfo.
                    ArrayList<String> userInfo = new ArrayList<>();
                    retrieveUserInfo(view, userInfo, currentComment.getUid());

                    //set the date of the current comment
                    TextView commentDate = (TextView) view.findViewById(R.id.comment_date);
                    commentDate.setText(createDateString(currentComment.getDate()));

                    //add comment to feed
                    commentFeed.addView(view);
                }
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

        //add TextChangedListener to listen for changes to the commentEntry EditText
        commentEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    //if the commentEntry edit text is not empty set enable the sendComment button
                    sendCommentBtn.setEnabled(true);
                } else {
                    //else disable the sendComment Button
                    sendCommentBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        sendCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get current time
                Date currentDate = new Date();
                long currentTime = currentDate.getTime();

                //create new comment variable from inputted text, the current user's uid, and the current time
                Comment newComment = new Comment(commentEntry.getText().toString(), uid,
                        currentTime);

                //get a reference to comment branch of the database
                DatabaseReference commentRef = mDatabase.child("comments").child(itemId).push();

                //write the new comment to the database
                commentRef.setValue(newComment);

                //clear commentEntry EditText field
                commentEntry.setText("");
            }
        });
    }

    /**
     * Gets the current user's profile information
     *
     * @param view     is the current comment view
     * @param userInfo is an ArrayList of user information
     * @param uid      is the uid of the current user
     */
    public void retrieveUserInfo(final View view, final ArrayList<String> userInfo, String uid) {
        //get reference to the current user in the users branch of the database and listen for changes
        mDatabase.child("users").orderByKey().equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            //create user with information from the database
                            User user = userSnapshot.getValue(User.class);
                            //add user information to userInfo arrayList
                            userInfo.add(user.getName());
                            userInfo.add(user.getInitials());
                            userInfo.add(user.getColor());
                            userInfo.add(user.getUid());
                            //pass view and userInfo arrayList to populateUSerFields
                            populateUserFields(view, userInfo);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Populate the current comment view with information about the current user
     *
     * @param view is the current comment view
     * @param userInfo is an ArrayList of Strings containing the current user's profile information
     */
    public void populateUserFields(View view, ArrayList<String> userInfo) {

        //set the display name of the current comment to the display name of the current user
        TextView displayName = (TextView) view.findViewById(R.id.comment_display_name);
        displayName.setText(userInfo.get(0));

        //get the initials of the current user and display them in the profilePic TextView
        TextView profilePic = (TextView) view.findViewById(R.id.comment_prof_pic);
        profilePic.setText(userInfo.get(1));

        //get the color of the current user's profile pic and set it as the background color of the comment's profilePic View
        switch (userInfo.get(2)) {
            case "red":
                profilePic.setBackgroundResource(R.drawable.profile_red);
                break;
            case "pink":
                profilePic.setBackgroundResource(R.drawable.profile_pink);
                break;
            case "purple":
                profilePic.setBackgroundResource(R.drawable.profile_purple);
                break;
            case "blue":
                profilePic.setBackgroundResource(R.drawable.profile_blue);
                break;
            case "teal":
                profilePic.setBackgroundResource(R.drawable.profile_teal);
                break;
            case "green":
                profilePic.setBackgroundResource(R.drawable.profile_green);
                break;
            case "yellow":
                profilePic.setBackgroundResource(R.drawable.profile_yellow);
                break;
            case "orange":
                profilePic.setBackgroundResource(R.drawable.profile_orange);
                break;
            case "gray":
                profilePic.setBackgroundResource(R.drawable.profile_gray);
                break;
        }

        //hide the donorTag
        TextView donorTag = (TextView) view.findViewById(R.id.donor_tag);
        donorTag.setVisibility(View.GONE);

        //hide the officerTag
        TextView officerTag = (TextView) view.findViewById(R.id.officer_tag);
        officerTag.setVisibility(View.GONE);

        //if the current user is the donor then display the donorTag
        if (userInfo.get(3).equals(donorId)) {
            donorTag.setVisibility(View.VISIBLE);
        } else if (userInfo.get(0).equals("FBLA Officer")) {
            officerTag.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Format the time so that it is displayed as the length of time from when the comment was posted. i.e. this comment was posted six days ago
     *
     * @param commentTime is the literal time the comment is written
     * @return a time Sting that represents how long ago the comment was posted
     */
    public String createDateString(long commentTime) {
        //get current time
        Date currentDate = new Date();
        long currentTime = currentDate.getTime();

        //find the time difference between the current time and when the comment was posted.
        long timeDiff = currentTime - commentTime;

        String time;

        //Convert the time difference into sensible units
        if (timeDiff < 60000L) {
            int seconds = (int) (timeDiff / 1000L);
            time = seconds + "s";
        } else if (timeDiff < 3600000L) {
            int minutes = (int) (timeDiff / 60000L);
            time = minutes + "m";
        } else if (timeDiff < 86400000L) {
            int hours = (int) (timeDiff / 3600000L);
            time = hours + "h";
        } else if (timeDiff < 604800000L) {
            int days = (int) (timeDiff / 86400000L);
            time = days + "d";
        } else {
            int weeks = (int) (timeDiff / 604800000L);
            time = weeks + "w";
        }

        time += " ago";

        return time;
    }
}
