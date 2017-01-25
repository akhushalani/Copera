package com.denovo.denovo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class AccountFragment extends Fragment {

    private String name;
    private ArrayList<String> wishlist;
    private RVAdapter mAdapter;
    private WrapContentLinearLayoutManager llm;

    private DatabaseReference mDatabase;


    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.wishlist_rv);
        llm = new WrapContentLinearLayoutManager(getContext());
        rv.setLayoutManager(llm);
        wishlist = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            name = user.getDisplayName();
        }

        TextView userNameTextView = (TextView) rootView.findViewById(R.id.user_name_text_view);
        userNameTextView.setText(name);

        mDatabase = FirebaseDatabase.getInstance().getReference();



        return rootView;
    }


}
