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

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        ImageView btnBack = (ImageView) findViewById(R.id.back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentActivity.this.finish();
            }
        });

        findViewById(R.id.settings).setVisibility(View.GONE);
        findViewById(R.id.search).setVisibility(View.GONE);
        findViewById(R.id.next).setVisibility(View.GONE);

        Bundle data = getIntent().getExtras();
        itemId = data.getString("item");
        donorId = data.getString("donor_id");

        mCommentList = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        final LinearLayout commentFeed = (LinearLayout) findViewById(R.id.activity_comment_feed);
        final EditText commentEntry = (EditText) findViewById(R.id.activity_comment_entry);
        final ImageView sendCommentBtn = (ImageView) findViewById(R.id.activity_send_comment_btn);
        final LayoutInflater inflater = LayoutInflater.from(this);

        mDatabase.child("comments").child(itemId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mCommentList.add(dataSnapshot.getValue(Comment.class));
                commentFeed.removeAllViews();

                for (int i = 0; i < mCommentList.size(); i++) {
                    Comment currentComment = mCommentList.get(i);
                    View view = inflater.inflate(comment_item, commentFeed, false);

                    if (i == 0) {
                        view.findViewById(R.id.comment_divider).setVisibility(View.GONE);
                    }

                    TextView commentTextView = (TextView) view.findViewById(R.id.comment_text);
                    commentTextView.setText(currentComment.getComment());

                    ArrayList<String> userInfo = new ArrayList<>();
                    retrieveUserInfo(view, userInfo, currentComment.getUid());

                    TextView commentDate = (TextView) view.findViewById(R.id.comment_date);
                    commentDate.setText(createDateString(currentComment.getDate()));

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

        commentEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    sendCommentBtn.setEnabled(true);
                } else {
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

                //create new comment variable and add it to realtime database
                Comment newComment = new Comment(commentEntry.getText().toString(), uid,
                        currentTime);
                DatabaseReference commentRef = mDatabase.child("comments").child(itemId).push();
                commentRef.setValue(newComment);

                //clear comment field
                commentEntry.setText("");
            }
        });
    }

    public void retrieveUserInfo(final View view, final ArrayList<String> userInfo, String uid) {
        mDatabase.child("users").orderByKey().equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            User user = userSnapshot.getValue(User.class);
                            userInfo.add(user.getName());
                            userInfo.add(user.getInitials());
                            userInfo.add(user.getColor());
                            userInfo.add(user.getUid());
                            populateUserFields(view, userInfo);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void populateUserFields(View view, ArrayList<String> userInfo) {
        TextView displayName = (TextView) view.findViewById(R.id.comment_display_name);
        displayName.setText(userInfo.get(0));

        TextView profilePic = (TextView) view.findViewById(R.id.comment_prof_pic);
        profilePic.setText(userInfo.get(1));

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

        TextView donorTag = (TextView) view.findViewById(R.id.donor_tag);
        donorTag.setVisibility(View.GONE);

        TextView officerTag = (TextView) view.findViewById(R.id.officer_tag);
        officerTag.setVisibility(View.GONE);

        if (userInfo.get(3).equals(donorId)) {
            officerTag.setVisibility(View.VISIBLE);
        } else if (userInfo.get(0).equals("Item Donor")) {
            donorTag.setVisibility(View.VISIBLE);
        }
    }

    public String createDateString(long commentTime) {
        Date currentDate = new Date();
        long currentTime = currentDate.getTime();
        long timeDiff = currentTime - commentTime;

        String time;

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
