package com.denovo.denovo.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;

import com.denovo.denovo.R;
import com.denovo.denovo.activities.DonateActivity;


public class DonateConditionFragment extends Fragment {

    boolean mFieldsFilled;
    OnConditionSubmittedListener mCallback = (OnConditionSubmittedListener) getActivity();
    private RatingBar mRatingBar;
    private int mRating;
    private DonateActivity mActivity;

    public DonateConditionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_donate_condition, container, false);

        mActivity = (DonateActivity) getActivity();

        //find ratingBar from xml
        mRatingBar = (RatingBar) rootView.findViewById(R.id.donate_ratingBar);
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //get the rating
                mRating = (int) rating;
                //enable the button
                onFieldsFilled();
            }
        });

        //find confirmRatingButton from xml
        Button confirmRatingButton = (Button) rootView.findViewById(R.id.btn_confirm_rating);
        confirmRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onConditionSubmitted(mRating);
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnConditionSubmittedListener) context;
            Log.v("DonateConditionFragment", "Callback initialized");
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnConditionSubmittedListener");
        }
    }

    /**
     * Enable the button since all fields are filled
     */
    public void onFieldsFilled() {
        mFieldsFilled = true;
        DonateActivity activity = (DonateActivity) getActivity();
        //enable the button
        activity.enableButton();
    }

    public void getItemCondition() {
        mActivity.mItemRating = mRating;
    }

    public interface OnConditionSubmittedListener {
        void onConditionSubmitted(int rating);
    }
}
