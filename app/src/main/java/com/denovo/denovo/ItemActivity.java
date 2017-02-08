package com.denovo.denovo;

import android.content.Intent;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.denovo.denovo.R.id.comment;
import static com.denovo.denovo.R.id.comment_text;
import static com.denovo.denovo.R.layout.comment_item;


public class ItemActivity extends AppCompatActivity {

    private static final String TAG = "ItemActivity";
    private Item item;
    private String itemId;
    private String uid;
    private int feedPosition;
    private ArrayList<Comment> mCommentList;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

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
                ItemActivity.this.finish();
            }
        });

        findViewById(R.id.settings).setVisibility(View.GONE);
        findViewById(R.id.search).setVisibility(View.GONE);
        findViewById(R.id.next).setVisibility(View.GONE);

        Bundle data = getIntent().getExtras();
        itemId = data.getString("item");

        mCommentList = new ArrayList<>();

        final ImageView itemPhoto = (ImageView) findViewById(R.id.item_photo);
        final TextView itemName = (TextView) findViewById(R.id.item_name);
        final TextView itemYardSale = (TextView) findViewById(R.id.item_yard_sale);
        final TextView itemPrice = (TextView) findViewById(R.id.item_price);
        final RatingBar itemRating = (RatingBar) findViewById(R.id.item_rating);
        final TextView description = (TextView) findViewById(R.id.description);
        final CustomButton wantItBtn = (CustomButton) findViewById(R.id.btn_item_want);

        final LinearLayout commentFeed = (LinearLayout) findViewById(R.id.comments_list);
        final TextView noComments = (TextView) findViewById(R.id.no_comments);
        final TextView allCommentBtn = (TextView) findViewById(R.id.btn_all_comments);

        final LayoutInflater inflater = LayoutInflater.from(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("items").orderByKey().equalTo(itemId)
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                item = dataSnapshot.getValue(Item.class);
                Log.v(TAG, item.getName());
                item.downloadImage(getApplicationContext(), itemPhoto);
                itemName.setText(item.getName());
                itemYardSale.setText(item.getYardSale());
                itemPrice.setText(item.formatPrice());
                itemRating.setRating(item.getRating());
                description.setText(item.getDescription());
                wantItBtn.setText(getString(R.string.wish_list, item.getWishListNum()));
                if (item.getWishListUsers() == null || !item.getWishListUsers().contains(uid)) {
                    wantItBtn.setBackgroundResource(R.drawable.mybuttonsmall);
                    //wantItBtn.setElevation(dpToPx(4));
                } else {
                    wantItBtn.setBackgroundResource(R.drawable.mybuttonsmall_inactive);
                    //wantItBtn.setElevation(dpToPx(1));
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                item = dataSnapshot.getValue(Item.class);
                wantItBtn.setText(getString(R.string.wish_list, item.getWishListNum()));
                if (item.getWishListUsers() == null || !item.getWishListUsers().contains(uid)) {
                    wantItBtn.setBackgroundResource(R.drawable.mybuttonsmall);
                } else {
                    wantItBtn.setBackgroundResource(R.drawable.mybuttonsmall_inactive);
                }
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

        final Intent intent = getIntent();

        wantItBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.onAddedToWishList(uid, itemId);
                Bundle b = new Bundle();
                b.putInt("position", feedPosition);
                b.putParcelable("item", item);
                intent.putExtras(b);
                setResult(1, intent);
            }
        });

        CustomButton bargainBtn = (CustomButton) findViewById(R.id.btn_item_bargain);
        bargainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemActivity.this, BargainActivity.class);
                intent.putExtra("item_key", "-KYLXskpzmhDq5citlod");
                startActivity(intent);
                finish();
            }
        });

        mDatabase.child("comments").child(itemId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mCommentList.add(dataSnapshot.getValue(Comment.class));

                commentFeed.removeAllViews();

                int previewCount;
                if (mCommentList == null) {
                    previewCount = 0;
                } else {
                    previewCount = mCommentList.size();
                }
                if (previewCount > 0) {
                    noComments.setVisibility(View.GONE);
                }
                if (previewCount >= 3) {
                    previewCount = mCommentList.size() - 3;
                } else {
                    previewCount = 0;
                }
                for (int i = previewCount; i < mCommentList.size(); i++) {
                    Comment currentComment = mCommentList.get(i);
                    View view = inflater.inflate(comment_item, commentFeed, false);

                    if (i == previewCount) {
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

        allCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ItemActivity.this, CommentActivity.class);
                i.putExtra("item", itemId);
                i.putExtra("donor_id", item.getDonor());
                startActivity(i);
            }
        });

        NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.scroll);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
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

        if (userInfo.get(3).equals(item.getDonor())) {
            donorTag.setVisibility(View.VISIBLE);
        } else if (userInfo.get(0).equals("FBLA Officer")) {
            officerTag.setVisibility(View.VISIBLE);
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
