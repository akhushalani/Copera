package com.denovo.denovo.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.denovo.denovo.views.CustomButton;
import com.denovo.denovo.models.Item;
import com.denovo.denovo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;


/**
 * Created by abhinavkhushalani on 11/4/16.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ItemViewHolder> {

    private static final String TAG = "RVAdapter";

    private List<Item> mFeed;

    private ItemClickCallback itemClickCallback;

    public interface ItemClickCallback {
        void onItemClick(int p);

        void onWantItBtnClick(int p);

        void onOfferBtnClick(int p);
    }

    public void setItemClickCallback(final ItemClickCallback itemClickCallback) {
        this.itemClickCallback = itemClickCallback;
    }

    public void set(int index, Item item) {
        mFeed.set(index, item);
    }

    public void swapDataSet(List<Item> newFeed) {
        mFeed = newFeed;
        notifyItemInserted(0);
    }

    public void swapDataSet(List<Item> newFeed, int index) {
        mFeed = newFeed;
        notifyItemChanged(index);
    }

    public void swapDataSet(List<Item> newFeed, boolean notify) {
        mFeed = newFeed;
        notifyDataSetChanged();
    }

    public RVAdapter(List<Item> feed) {
        this.mFeed = feed;
    }

    @Override
    public int getItemCount() {
        return mFeed.size();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_card,
                viewGroup, false);
        return new ItemViewHolder(v, i);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder itemViewHolder, int i) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = " ";
        if (user != null) {
            uid = user.getUid();
        }
        mFeed.get(i).downloadImage(itemViewHolder.itemPhoto.getContext(), itemViewHolder.itemPhoto);
        itemViewHolder.itemName.setText(mFeed.get(i).getName());
        itemViewHolder.itemPrice.setText(mFeed.get(i).formatPrice());
        itemViewHolder.itemRating.setRating(mFeed.get(i).getRating());
        itemViewHolder.description.setText(mFeed.get(i).getDescription());
        itemViewHolder.wantItBtn.setText("Wish List | " + mFeed.get(i).getWishListNum());
        if (mFeed.get(i).getWishListUsers() == null ||
                !mFeed.get(i).getWishListUsers().contains(uid)) {
            itemViewHolder.wantItBtn.setBackgroundResource(R.drawable.mybuttonsmall);
        } else {
            itemViewHolder.wantItBtn.setBackgroundResource(R.drawable.mybuttonsmall_inactive);
        }
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
        CustomButton wantItBtn;
        CustomButton offerBtn;
        int index;

        ItemViewHolder(View itemView, int i) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            itemPhoto = (ImageView) itemView.findViewById(R.id.item_photo);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
            itemPrice = (TextView) itemView.findViewById(R.id.item_price);
            itemRating = (RatingBar) itemView.findViewById(R.id.item_rating);
            description = (TextView) itemView.findViewById(R.id.description);
            wantItBtn = (CustomButton) itemView.findViewById(R.id.btn_item_want);
            offerBtn = (CustomButton) itemView.findViewById(R.id.btn_item_offer);
            index = i;

            cv.setOnClickListener(this);
            wantItBtn.setOnClickListener(this);
            offerBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = " ";
            if (user != null) {
                uid = user.getUid();
            }
            if (v.getId() == R.id.cv) {
                itemClickCallback.onItemClick(getAdapterPosition());
            } else if (v.getId() == R.id.btn_item_want) {
                itemClickCallback.onWantItBtnClick(getAdapterPosition());
                ((TextView) v).setText("Wish List | " + mFeed.get(index).getWishListNum());
                if (mFeed.get(index).getWishListUsers() == null ||
                        !mFeed.get(index).getWishListUsers().contains(uid)) {
                    wantItBtn.setBackgroundResource(R.drawable.mybuttonsmall);
                } else {
                    wantItBtn.setBackgroundResource(R.drawable.mybuttonsmall_inactive);
                }
            } else if (v.getId() == R.id.btn_item_offer) {
                itemClickCallback.onOfferBtnClick(getAdapterPosition());
            } else {

            }
        }
    }

}