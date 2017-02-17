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

import com.denovo.denovo.models.Chapter;
import com.denovo.denovo.R;

import java.util.ArrayList;

/**
 * Created by abhinavkhushalani on 2/12/17.
 */

public class ChapterSearchResultAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Chapter> mSearchResults;
    private int mQueryLength;
    private ArrayList<Integer> mResultLocations;

    public ChapterSearchResultAdapter(Activity context) {
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
            listItemView = mInflater.inflate(R.layout.chapter_search_result_list_item, parent, false);
        }

        Chapter currentChapter = mSearchResults.get(position);

        TextView chapterName = (TextView) listItemView.findViewById(R.id
                .search_result_chapter_name);

        SpannableStringBuilder sb = new SpannableStringBuilder(currentChapter.getName());
        ForegroundColorSpan fcs = new ForegroundColorSpan(ContextCompat.getColor(mContext,
                R.color.colorAccent));
        int start = mResultLocations.get(position);
        sb.setSpan(fcs, start, start + mQueryLength, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        chapterName.setText(sb);

        TextView chapterAddress = (TextView) listItemView.findViewById(R.id
                .search_result_chapter_address);
        chapterAddress.setText(currentChapter.getAddress());

        return listItemView;
    }

    public void updateList(ArrayList<Chapter> searchResults, int queryLength,
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
