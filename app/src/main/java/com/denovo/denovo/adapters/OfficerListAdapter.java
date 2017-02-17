package com.denovo.denovo.adapters;


import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.denovo.denovo.R;
import com.denovo.denovo.models.Item;
import com.denovo.denovo.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;
import static android.R.attr.name;
import static android.R.attr.resource;

public class OfficerListAdapter extends ArrayAdapter<String> {

    private List<String> users;
    private String name;

    public OfficerListAdapter(Context context, int resource, ArrayList<String> users) {
        super(context, resource, users);
        this.users = users;
    }

    @Override
    public int getCount() {
        if (users != null) {
            return users.size();
        }
        return -1;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        String uid = getItem(position);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User u = userSnapshot.getValue(User.class);
                    name = u.getName();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.officer_list_item, parent, false);
        }
        //find view from xml
        TextView nameView = (TextView) convertView.findViewById(R.id.officer_name);
        // Populate the data into the template view using the data object
        nameView.setText(name);
        // Return the completed view
        return convertView;
    }

    public void swapDataSet(ArrayList<String> list) {
        this.users = list;
        notifyDataSetChanged();
    }

}
