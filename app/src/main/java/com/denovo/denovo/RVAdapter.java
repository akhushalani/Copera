package com.denovo.denovo;

import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;


/**
 * Created by abhinavkhushalani on 11/4/16.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ItemViewHolder>{
    List<Item> mFeed;

    RVAdapter(List<Item> feed){
        this.mFeed = feed;
    }

    @Override
    public int getItemCount() {
        return mFeed.size();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_card, viewGroup, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder itemViewHolder, int i) {
        itemViewHolder.itemPhoto.setImageResource(mFeed.get(i).getImageResourceId());
        itemViewHolder.itemName.setText(mFeed.get(i).getName());
        itemViewHolder.userName.setText(mFeed.get(i).getUser());
        itemViewHolder.itemPrice.setText(mFeed.get(i).getPrice());
        itemViewHolder.itemRating.setRating(mFeed.get(i).getRating());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        ImageView itemPhoto;
        TextView itemName;
        TextView userName;
        TextView itemPrice;
        RatingBar itemRating;


        ItemViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            itemPhoto = (ImageView) itemView.findViewById(R.id.item_photo);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            itemPrice = (TextView) itemView.findViewById(R.id.item_price);
            itemRating = (RatingBar) itemView.findViewById(R.id.item_rating);
        }
    }

}