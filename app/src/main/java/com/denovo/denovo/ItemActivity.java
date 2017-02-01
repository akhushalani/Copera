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

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.denovo.denovo.R.id.comment;
import static com.denovo.denovo.R.layout.comment_item;


public class ItemActivity extends AppCompatActivity {

    private static final String TAG = "ItemActivity";
    private Item item;
    private String itemId;
    private String uid;
    private int feedPosition;
    private ListView mMessageListView;
    private Comment mCommentAdapter;
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

        final ImageView itemPhoto = (ImageView) findViewById(R.id.item_photo);
        final TextView itemName = (TextView) findViewById(R.id.item_name);
        final TextView itemYardSale = (TextView) findViewById(R.id.item_yard_sale);
        final TextView itemPrice = (TextView) findViewById(R.id.item_price);
        final RatingBar itemRating = (RatingBar) findViewById(R.id.item_rating);
        final TextView description = (TextView) findViewById(R.id.description);
        final CustomButton wantItBtn = (CustomButton) findViewById(R.id.btn_item_want);

        final LinearLayout commentList = (LinearLayout) findViewById(R.id.comments_list);
        final TextView noComments = (TextView) findViewById(R.id.no_comments);
        final EditText commentEntry = (EditText) findViewById(R.id.comment_entry);
        final ImageView sendCommentBtn = (ImageView) findViewById(R.id.send_comment_btn);

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
                int previewCount;
                if (item.getComments() == null) {
                    previewCount = 0;
                } else {
                    previewCount = item.getComments().size();
                }
                if (previewCount > 0) {
                    noComments.setVisibility(View.GONE);
                }
                if (previewCount > 3) {
                    previewCount = 3;
                }
                for (int i = 0; i < previewCount; i++) {
                    Comment currentComment = item.getComments().get(i);
                    View view = inflater.inflate(comment_item, commentList, false);

                    TextView CommentTextView = (TextView) view.findViewById(comment);
                    CommentTextView.setText(currentComment.getComment());

                    commentList.addView(view);
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
                //Intent intent = new Intent(ItemActivity.this, BargainActivity.class);
                Intent intent = new Intent(ItemActivity.this, CreateChapter.class);
                startActivity(intent);
                //intent.putExtra("item_key", "-KYLXskpzmhDq5citlod");
                //startActivity(intent);
                finish();
            }
        });

        // Enable Send button when there's text to send
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
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String currentTime = sdf.format(new Date());

                //create new comment variable and add it to realtime database
                Comment newComment = new Comment(commentEntry.getText().toString(), user.getUid(), currentTime);
                DatabaseReference commentRef = mDatabase.child("comments").child(itemId).push();
                commentRef.setValue(newComment);

                //clear comment field
                commentEntry.setText("");
            }
        });

        NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.scroll);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


}
