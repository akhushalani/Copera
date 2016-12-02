package com.denovo.denovo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by abhinavkhushalani on 12/1/16.
 */

public class Question implements Parcelable {
    private String mQuestion;
    private String mAnswer;

    public Question(String question) {
        mQuestion = question;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public String getAnswer() {
        return mAnswer;
    }

    public void setAnswer(String answer) {
        mAnswer = answer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mQuestion);
        out.writeString(mAnswer);
    }

    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    private Question(Parcel in) {
        mQuestion = in.readString();
        mAnswer = in.readString();
    }
}
