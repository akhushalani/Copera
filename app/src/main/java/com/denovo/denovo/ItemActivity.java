package com.denovo.denovo;

import android.content.Intent;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static com.denovo.denovo.R.id.questions;


public class ItemActivity extends AppCompatActivity {
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

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

        Bundle data = getIntent().getExtras();
        item = data.getParcelable("item");

        ImageView itemPhoto = (ImageView) findViewById(R.id.item_photo);
        itemPhoto.setImageResource(item.getImageResourceId());

        TextView itemName = (TextView) findViewById(R.id.item_name);
        itemName.setText(item.getName());

        TextView itemYardSale = (TextView) findViewById(R.id.item_yard_sale);
        itemYardSale.setText(item.getYardSale());

        TextView itemPrice = (TextView) findViewById(R.id.item_price);
        itemPrice.setText(item.getPrice());

        RatingBar itemRating = (RatingBar) findViewById(R.id.item_rating);
        itemRating.setRating(itemRating.getRating());

        TextView description = (TextView) findViewById(R.id.description);
        description.setText(item.getDescription());

        CustomButton wantItBtn = (CustomButton) findViewById(R.id.btn_item_want);
        wantItBtn.setText(getString(R.string.want_it, item.getWantIt()));
        wantItBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setWantIt();
                ((TextView) v).setText(getString(R.string.want_it, item.getWantIt()));
            }
        });

        LinearLayout questionList = (LinearLayout) findViewById(R.id.questions_list);
        TextView noQuestions = (TextView) findViewById(R.id.no_questions);
        LayoutInflater inflater = LayoutInflater.from(this);
        int previewCount = item.getQuestions().size();
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

        NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.scroll);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}
