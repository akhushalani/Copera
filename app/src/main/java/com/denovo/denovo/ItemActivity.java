package com.denovo.denovo;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


public class ItemActivity extends AppCompatActivity {
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

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
    }
}
