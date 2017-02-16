package com.denovo.denovo.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.denovo.denovo.models.Comment;
import com.denovo.denovo.views.CustomButton;
import com.denovo.denovo.models.Item;
import com.denovo.denovo.helpers.MoneyValueFilter;
import com.denovo.denovo.R;
import com.denovo.denovo.models.User;
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

        //get the unique id of the current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        }

        //set the action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        //hook up back button
        ImageView btnBack = (ImageView) findViewById(R.id.back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemActivity.this.finish();
            }
        });

        //hide unused action bar icons
        findViewById(R.id.settings).setVisibility(View.GONE);
        findViewById(R.id.search).setVisibility(View.GONE);
        findViewById(R.id.next).setVisibility(View.GONE);

        //get item id from FeedFragment
        Bundle data = getIntent().getExtras();
        itemId = data.getString("item");

        mCommentList = new ArrayList<>();

        //find views from xml
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

        //instantiate the database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //add child event listener on the item to listen for changes in the database
        mDatabase.child("items").orderByKey().equalTo(itemId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        //create Item from data read from the database
                        item = dataSnapshot.getValue(Item.class);
                        Log.v(TAG, item.getName());
                        item.downloadImage(getApplicationContext(), itemPhoto);
                        //populate the views
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
                            //if the item is not wish listed by the current user, display a grayed-out button
                            wantItBtn.setBackgroundResource(R.drawable.mybuttonsmall_inactive);
                            //wantItBtn.setElevation(dpToPx(1));
                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        //create Item from data read from the database
                        item = dataSnapshot.getValue(Item.class);
                        //if any user wish lists or de-wish lists the item, update the number displayed on the wishlist button
                        wantItBtn.setText(getString(R.string.wish_list, item.getWishListNum()));
                        if (item.getWishListUsers() == null || !item.getWishListUsers().contains(uid)) {
                            wantItBtn.setBackgroundResource(R.drawable.mybuttonsmall);
                        } else {
                            //if the item is not wish listed by the current user, display a grayed-out button
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

        CustomButton bargainBtn = (CustomButton) findViewById(R.id.btn_item_offer);
        bargainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ItemActivity.this);
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_make_offer);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                Button cancelButton = (Button) dialog.findViewById(R.id.cancel_offer_btn);
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                final TextView offerAmountEditText = (EditText) dialog.findViewById(R.id
                        .offer_amount_edit_text);
                final Button submitButton = (Button) dialog.findViewById(R.id.submit_offer_btn);
                submitButton.setEnabled(false);

                offerAmountEditText.setFilters(new InputFilter[] {new MoneyValueFilter()});
                offerAmountEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() > 0) {
                            submitButton.setEnabled(true);
                        } else {
                            submitButton.setEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //get current time
                        Date currentDate = new Date();
                        long currentTime = currentDate.getTime();

                        //create new comment variable from inputted text, the current user's uid, and the current time
                        Comment newComment = new Comment(offerAmountEditText.getText().toString(),
                                uid, currentTime, "offer");

                        //get a reference to comment branch of the database
                        DatabaseReference commentRef = mDatabase.child("comments").child(itemId).push();

                        //write the new comment to the database
                        commentRef.setValue(newComment);

                        //dismiss the offer dialog
                        dialog.dismiss();
                    }
                });
            }
        });

        //add child event listener to listen for changes in the item's branch of the the comment tree
        mDatabase.child("comments").child(itemId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //add comments to the comment list
                mCommentList.add(dataSnapshot.getValue(Comment.class));

                //clear the feed
                commentFeed.removeAllViews();

                int previewCount;
                if (mCommentList == null) {
                    //if there are no comments, do not display a preview
                    previewCount = 0;
                } else {
                    //else display a preview
                    previewCount = mCommentList.size();
                }
                if (previewCount > 0) {
                    //if there are comments hide the noComments view
                    noComments.setVisibility(View.GONE);
                }
                if (previewCount >= 3) {
                    previewCount = mCommentList.size() - 3;
                } else {
                    previewCount = 0;
                }
                for (int i = previewCount; i < mCommentList.size(); i++) {
                    Comment currentComment = mCommentList.get(i);

                    View view;

                    if (currentComment.getType().equals("comment")) {
                        view = inflater.inflate(R.layout.comment_item, commentFeed, false);

                        //set the text of the current comment
                        TextView commentTextView = (TextView) view.findViewById(R.id.comment_text);
                        commentTextView.setText(currentComment.getComment());

                        //create userInfo arrayList and pass it along with the users uid to retrieveUserInfo.
                        ArrayList<String> userInfo = new ArrayList<>();
                        retrieveUserInfo(view, userInfo, currentComment.getUid(), "comment");

                        //set the date of the current comment
                        TextView commentDate = (TextView) view.findViewById(R.id.comment_date);
                        commentDate.setText(createDateString(currentComment.getDate()));
                    } else {
                        view = inflater.inflate(R.layout.offer_item, commentFeed, false);

                        //set the text of the current comment
                        TextView offerAmountTextView = (TextView) view.findViewById(R.id.offer_amount);
                        offerAmountTextView.setText("$" + currentComment.getComment());

                        //create userInfo arrayList and pass it along with the users uid to retrieveUserInfo.
                        ArrayList<String> userInfo = new ArrayList<>();
                        retrieveUserInfo(view, userInfo, currentComment.getUid(), "offer");
                    }

                    //if the comment is the first in the list, hide the divider
                    if (i == 0) {
                        view.findViewById(R.id.comment_divider).setVisibility(View.GONE);
                    }


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

    /**
     * Gets the current user's profile information
     *
     * @param view     is the current comment view
     * @param userInfo is an ArrayList of user information
     * @param uid      is the uid of the current user
     * @param type     is the type of the comment (comment or offer)
     */
    public void retrieveUserInfo(final View view, final ArrayList<String> userInfo, String uid,
                                 final String type) {
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

                            //pass view and userInfo arrayList to populateUserFields
                            populateUserFields(view, userInfo, type);
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
     * @param view     is the current comment view
     * @param userInfo is an ArrayList of Strings containing the current user's profile information
     * @param type     is the type of the comment (comment or offer)
     */
    public void populateUserFields(View view, ArrayList<String> userInfo, String type) {

        if (type.equals("comment")) {
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
            if (userInfo.get(3).equals(item.getDonor())) {
                donorTag.setVisibility(View.VISIBLE);
            } else if (userInfo.get(0).equals("FBLA Officer")) {
                officerTag.setVisibility(View.VISIBLE);
            }
        } else {
            TextView offerUserTextView = (TextView) view.findViewById(R.id.offer_user);
            offerUserTextView.setText(userInfo.get(0));
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
