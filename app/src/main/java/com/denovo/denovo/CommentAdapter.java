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

public class CommentAdapter extends ArrayAdapter<Comment> {
    public CommentAdapter(Activity context, ArrayList<Comment> commentList, String uid) {
        super(context, 0, commentList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.comment_item, parent, false);
        }

        Comment currentComment = getItem(position);

        TextView commentTextView = (TextView) listItemView.findViewById(R.id.comment_text);
        commentTextView.setText(currentComment.getComment());

        //TextView dateTextView = (TextView) listItemView.findViewById(R.id.comment_date);
        //DateFormat dateFormat = new DateFormat();

        return listItemView;
    }
}
