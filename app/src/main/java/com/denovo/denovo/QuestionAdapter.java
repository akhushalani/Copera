package com.denovo.denovo;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by abhinavkhushalani on 12/1/16.
 */

public class QuestionAdapter extends ArrayAdapter<Question> {
    public QuestionAdapter(Activity context, ArrayList<Question> questionList) {
        super(context, 0, questionList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.question_list_item, parent, false);
        }

        Question currentQuestion = getItem(position);

        TextView questionTextView = (TextView) listItemView.findViewById(R.id.question);
        questionTextView.setText(currentQuestion.getQuestion());

        TextView answerTextView = (TextView) listItemView.findViewById(R.id.answer);
        answerTextView.setText(currentQuestion.getAnswer());

        return listItemView;
    }
}
