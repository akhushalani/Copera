package com.denovo.denovo;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import java.util.List;


/**
 * Created by abhinavkhushalani on 11/4/16.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ItemViewHolder> {

    private List<Item> mFeed;

    private ItemClickCallback itemClickCallback;

    public interface ItemClickCallback {
        void onItemClick(int p);

        void onWantItBtnClick(int p);

        void onBargainBtnClick(int p);
    }

    public void setItemClickCallback(final ItemClickCallback itemClickCallback) {
        this.itemClickCallback = itemClickCallback;
    }


    RVAdapter(List<Item> feed) {
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
        itemViewHolder.itemPrice.setText(mFeed.get(i).getPrice());
        itemViewHolder.itemRating.setRating(mFeed.get(i).getRating());
        itemViewHolder.description.setText(mFeed.get(i).getDescription());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cv;
        ImageView itemPhoto;
        TextView itemName;
        TextView itemPrice;
        RatingBar itemRating;
        TextView description;
        TextView yardSale;
        Button wantItBtn;
        Button bargainBtn;

        ItemViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            itemPhoto = (ImageView) itemView.findViewById(R.id.item_photo);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
            itemPrice = (TextView) itemView.findViewById(R.id.item_price);
            itemRating = (RatingBar) itemView.findViewById(R.id.item_rating);
            description = (TextView) itemView.findViewById(R.id.description);
            yardSale = (TextView) itemView.findViewById(R.id.item_yard_sale);
            wantItBtn = (Button) itemView.findViewById(R.id.btn_item_want);
            bargainBtn = (Button) itemView.findViewById(R.id.btn_item_bargain);

            cv.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.cv) {
                itemClickCallback.onItemClick(getAdapterPosition());
            } else if (v.getId() == R.id.btn_item_want) {
                itemClickCallback.onWantItBtnClick(getAdapterPosition());
            } else if (v.getId() == R.id.btn_item_bargain) {
                itemClickCallback.onBargainBtnClick(getAdapterPosition());
            } else {

            }
        }
    }

}