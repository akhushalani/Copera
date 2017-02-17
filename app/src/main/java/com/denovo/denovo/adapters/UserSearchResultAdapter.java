package com.denovo.denovo.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.denovo.denovo.R;
import com.denovo.denovo.models.User;

import java.util.ArrayList;

/**
 * Created by abhinavkhushalani on 2/12/17.
 */

public class UserSearchResultAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<User> mSearchResults;
    private int mQueryLength;
    private ArrayList<Integer> mResultLocations;

    public UserSearchResultAdapter(Activity context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mSearchResults = new ArrayList<>();
        mResultLocations = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mSearchResults.size();
    }

    @Override
    public Object getItem(int position) {
        return mSearchResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = mInflater.inflate(R.layout.user_search_result_list_item, parent, false);
        }

        User currentUser = mSearchResults.get(position);

        TextView userName = (TextView) listItemView.findViewById(R.id
                .search_result_user_name);

        SpannableStringBuilder sb = new SpannableStringBuilder(currentUser.getName());
        ForegroundColorSpan fcs = new ForegroundColorSpan(ContextCompat.getColor(mContext,
                R.color.colorAccent));
        int start = mResultLocations.get(position);
        sb.setSpan(fcs, start, start + mQueryLength, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        userName.setText(sb);

        TextView profilePic = (TextView) listItemView.findViewById(R.id.user_result_prof_pic);
        profilePic.setText(currentUser.getInitials());

        //get the color of the current user's profile pic and set it as the background color of the comment's profilePic View
        switch (currentUser.getColor()) {
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

        return listItemView;
    }

    public void updateList(ArrayList<User> searchResults, int queryLength,
                           ArrayList<Integer> resultLocations) {
        mSearchResults = searchResults;
        mQueryLength = queryLength;
        mResultLocations = resultLocations;
        notifyDataSetChanged();
    }

    public void clearList() {
        mSearchResults = new ArrayList<>();
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return mSearchResults.isEmpty();
    }
}
