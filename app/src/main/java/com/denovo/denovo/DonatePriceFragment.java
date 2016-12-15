package com.denovo.denovo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class DonatePriceFragment extends Fragment {

    private EditText mItemPriceEditText;
    private double mItemPrice;
    private DonateActivity mActivity;

    boolean mFieldsFilled;

    OnPriceSubmittedListener mCallback = (OnPriceSubmittedListener) getActivity();

    public DonatePriceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_donate_price, container, false);

        mActivity = (DonateActivity) getActivity();

        mItemPriceEditText = (EditText) rootView.findViewById(R.id.item_price_edit_text);
        mItemPriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() != 0) {
                    mItemPrice = Double.parseDouble(s.toString());
                    onFieldsFilled();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Button confirmPriceButton = (Button) rootView.findViewById(R.id.btn_confirm_price);
        confirmPriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onPriceSubmitted(mItemPrice);
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
            mCallback = (OnPriceSubmittedListener) context;
            Log.v("DonatePriceFragment", "Callback initialized");
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnPriceSubmittedListener");
        }
    }

    public void onFieldsFilled() {
        mFieldsFilled = true;
        DonateActivity activity = (DonateActivity) getActivity();
        activity.enableButton();
    }

    public void getItemPrice() {
        mActivity.mItemPrice = mItemPrice;
    }

    public interface OnPriceSubmittedListener {
        void onPriceSubmitted(double price);
    }
}
