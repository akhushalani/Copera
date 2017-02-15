package com.denovo.denovo.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.denovo.denovo.adapters.DonateAdapter;
import com.denovo.denovo.fragments.DonateConditionFragment;
import com.denovo.denovo.fragments.DonateItemInfoFragment;
import com.denovo.denovo.fragments.DonatePriceFragment;
import com.denovo.denovo.fragments.DonateSummaryFragment;
import com.denovo.denovo.R;

public class DonateActivity extends AppCompatActivity
        implements DonateItemInfoFragment.OnInfoSubmittedListener,
        DonateConditionFragment.OnConditionSubmittedListener,
        DonatePriceFragment.OnPriceSubmittedListener {

    private static final String TAG = "DonateActivity";
    public String mItemPhotoPath;
    public String mItemName;
    public String mItemYardSale;
    public String mItemDescription;
    public int mItemRating;
    public double mItemPrice;
    private TabLayout mTabLayout;
    private ImageView mBtnBack;
    private ImageView mBtnNext;
    private DonateAdapter mAdapter;
    private DonateItemInfoFragment donateItemInfoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        mBtnBack = (ImageView) findViewById(R.id.back);
        mBtnNext = (ImageView) findViewById(R.id.next);

        findViewById(R.id.settings).setVisibility(View.GONE);
        findViewById(R.id.search).setVisibility(View.GONE);
        findViewById(R.id.next).setVisibility(View.GONE);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final int targetW = metrics.widthPixels;

        final ViewPager viewPager = (ViewPager) findViewById(R.id.donate_viewpager);
        mAdapter = new DonateAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.donate_steps);
        mTabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);
        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_one_active);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_two_incomplete);
        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_three_incomplete);
        mTabLayout.getTabAt(3).setIcon(R.drawable.ic_check_incomplete);

        LinearLayout tabStrip = ((LinearLayout) mTabLayout.getChildAt(0));
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DonateActivity.this.finish();
            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        viewPager.setCurrentItem(0);
                        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_one_active);
                        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_two_incomplete);
                        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_three_incomplete);
                        mTabLayout.getTabAt(3).setIcon(R.drawable.ic_check_incomplete);
                        donateItemInfoFragment =
                                (DonateItemInfoFragment) mAdapter.getItem(0);
                        mBtnBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DonateActivity.this.finish();
                            }
                        });
                        break;
                    case 1:
                        viewPager.setCurrentItem(1);
                        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_one_complete);
                        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_two_active);
                        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_three_incomplete);
                        mTabLayout.getTabAt(3).setIcon(R.drawable.ic_check_incomplete);
                        mBtnBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTabLayout.getTabAt(0).select();
                            }
                        });
                        break;
                    case 2:
                        viewPager.setCurrentItem(2);
                        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_one_complete);
                        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_two_complete);
                        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_three_active);
                        mTabLayout.getTabAt(3).setIcon(R.drawable.ic_check_incomplete);
                        mBtnBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTabLayout.getTabAt(1).select();
                            }
                        });
                        break;
                    case 3:
                        viewPager.setCurrentItem(3);
                        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_one_complete);
                        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_two_complete);
                        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_three_complete);
                        mTabLayout.getTabAt(3).setIcon(R.drawable.ic_check_complete);
                        final DonateSummaryFragment donateSummaryFragment =
                                (DonateSummaryFragment) mAdapter.getItem(3);
                        donateSummaryFragment.populateView(targetW, mItemPhotoPath, mItemName,
                                mItemYardSale,
                                mItemDescription, mItemRating, mItemPrice);
                        mBtnBack.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mTabLayout.getTabAt(2).select();
                            }
                        });
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * Get general item information from tab one
     *
     * @param photoPath   is the path to the item photo
     * @param name        is the name of the item
     * @param yardSale    is the yard sale that the item is donated to
     * @param description is a description of the item
     */
    public void onInfoSubmitted(String photoPath, String name, String yardSale, String
            description) {
        mItemPhotoPath = photoPath;
        mItemName = name;
        mItemYardSale = yardSale;
        mItemDescription = description;
        mTabLayout.getTabAt(1).select();
    }

    /**
     * Get condition rating of item from tab 2
     *
     * @param rating is the condition rating of the object
     */
    public void onConditionSubmitted(int rating) {
        mItemRating = rating;
        mTabLayout.getTabAt(2).select();
    }

    /**
     * Get price of the object
     *
     * @param price is the price of the object
     */
    public void onPriceSubmitted(double price) {
        mItemPrice = price;
        mTabLayout.getTabAt(3).select();
    }

    /**
     * Enable the next button
     */
    public void enableButton() {
        mBtnNext.setEnabled(true);
    }
}
