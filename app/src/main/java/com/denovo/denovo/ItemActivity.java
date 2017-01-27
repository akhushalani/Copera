package com.denovo.denovo;

import android.content.Intent;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static java.security.AccessController.getContext;


public class ItemActivity extends AppCompatActivity {

    private static final String TAG = "ItemActivity";
    private Item item;
    private String itemId;
    private String uid;
    private int feedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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

        final LinearLayout questionList = (LinearLayout) findViewById(R.id.questions_list);
        final TextView noQuestions = (TextView) findViewById(R.id.no_questions);
        final LayoutInflater inflater = LayoutInflater.from(this);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef.child("items").orderByKey().equalTo(itemId)
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
                    wantItBtn.setElevation(dpToPx(4));
                } else {
                    wantItBtn.setBackgroundResource(R.drawable.mybuttonsmall_inactive);
                    wantItBtn.setElevation(dpToPx(1));
                }
                int previewCount;
                if (item.getQuestions() == null) {
                    previewCount = 0;
                } else {
                    previewCount = item.getQuestions().size();
                }
                if (previewCount > 0) {
                    noQuestions.setVisibility(View.GONE);
                }
                if (previewCount > 3) {
                    previewCount = 3;
                }
                for (int i = 0; i < previewCount; i++) {
                    Question currentQuestion = item.getQuestions().get(i);
                    View view  = inflater.inflate(R.layout.question_list_item, questionList, false);

                    TextView questionTextView = (TextView) view.findViewById(R.id.question);
                    questionTextView.setText(currentQuestion.getQuestion());

                    TextView answerTextView = (TextView) view.findViewById(R.id.answer);
                    answerTextView.setText(currentQuestion.getAnswer());

                    questionList.addView(view);
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

        NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.scroll);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
