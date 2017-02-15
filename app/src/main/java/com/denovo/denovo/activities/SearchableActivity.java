package com.denovo.denovo.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.denovo.denovo.models.Chapter;
import com.denovo.denovo.R;
import com.denovo.denovo.adapters.SearchResultAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SearchableActivity extends AppCompatActivity {

    private static final String TAG = "SearchableActivity";

    private ProgressBar loadingProgressBar;
    private ArrayList<Chapter> chapterArray;
    private ArrayList<String> nameArray;
    private ArrayList<Integer> searchArray;
    private ArrayList<Integer> resultLocations;
    private ArrayList<Chapter> searchResults;
    private DatabaseReference mDatabase;
    private ListView mSearchResultListView;
    private SearchResultAdapter mSearchResultAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        loadingProgressBar = (ProgressBar) findViewById(R.id.search_loading_progress_bar);
        loadingProgressBar.setIndeterminate(true);
        loadingProgressBar.setVisibility(View.GONE);

        mSearchResultListView = (ListView) findViewById(R.id.search_result_list_view);

        final TextView noResults = (TextView) findViewById(R.id.no_results);

        chapterArray = new ArrayList<>();
        nameArray = new ArrayList<>();
        searchArray = new ArrayList<>();
        resultLocations = new ArrayList<>();
        searchResults = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("chapters").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chapter newChapter =  dataSnapshot.getValue(Chapter.class);
                chapterArray.add(newChapter);
                String chapterName = newChapter.getName().toLowerCase();
                nameArray.add(chapterName);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) findViewById(R.id.chapter_search_view);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        TextView searchText = (TextView)
                searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        Typeface myCustomFont =
                Typeface.createFromAsset(getAssets(),"fonts/JosefinSlab-Regular.ttf");
        searchText.setTypeface(myCustomFont);

        mSearchResultAdapter = new SearchResultAdapter(this);
        mSearchResultListView.setAdapter(mSearchResultAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                noResults.setVisibility(View.GONE);

                if (newText.length() == 0) {
                    mSearchResultAdapter.clearList();
                    loadingProgressBar.setVisibility(View.GONE);
                    return true;
                } else if (newText.length() < 2) {
                    loadingProgressBar.setVisibility(View.GONE);
                    return false;
                }

                searchArray = new ArrayList<>();
                resultLocations = new ArrayList<>();
                searchResults = new ArrayList<>();
                doSearch(newText.toLowerCase());

                if (mSearchResultAdapter.isEmpty()) {
                    noResults.setVisibility(View.VISIBLE);
                } else {
                    noResults.setVisibility(View.GONE);
                }

                return true;
            }
        });


        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);

        searchArray = new ArrayList<>();
        resultLocations = new ArrayList<>();
        searchResults = new ArrayList<>();

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY).toLowerCase();
            loadingProgressBar.setVisibility(View.VISIBLE);

            doSearch(query);
        }
    }

    private void doSearch(String query) {
        for (String searchString : nameArray) {
            searchArray.add(kmpSearch(searchString.toCharArray(), query.toCharArray()));
        }

        for (int i = 0; i < searchArray.size(); i++) {
            if (searchArray.get(i) >= 0) {
                resultLocations.add(searchArray.get(i));
                searchResults.add(chapterArray.get(i));
            }
        }

        loadingProgressBar.setVisibility(View.GONE);
        mSearchResultAdapter.updateList(searchResults, query.length(), resultLocations);
    }

    private int kmpSearch(char[] s, char[] w) {
        int m = 0;
        int i = 0;
        int[] t = kmpTable(w);

        while (m + i < s.length) {
            if (w[i] == s[m + i]) {
                if (i == w.length - 1) {
                    return m;
                }
                i++;
            } else {
                if (t[i] > -1) {
                    m = m + i - t[i];
                    i = t[i];
                } else {
                    m++;
                    i = 0;
                }
            }
        }
        return -1;
    }

    private int[] kmpTable(char[] w) {
        int[] t = new int[w.length];
        int pos = 2;
        int cnd = 0;

        t[0] = -1;
        t[1] = 0;

        while (pos < w.length) {
            if (w[pos - 1] == w[cnd]) {
                t[pos] = cnd + 1;
                pos++;
                cnd++;
            } else if (cnd > 0) {
                cnd = t[cnd];
            } else {
                t[pos] = 0;
                pos++;
            }
        }

        return t;
    }
}
