package com.denovo.denovo;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by abhinavkhushalani on 12/1/16.
 */

public class CommentAdapter extends ArrayAdapter<ItemComment> {
    public CommentAdapter(Activity context, ArrayList<ItemComment> commentList, String uid) {
        super(context, 0, commentList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.question_list_item, parent, false);
        }

        ItemComment currentComment = getItem(position);

        TextView commentTextView = (TextView) listItemView.findViewById(R.id.comments);
        commentTextView.setText(currentComment.getComment());


        return listItemView;
    }
}
