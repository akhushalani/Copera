package com.denovo.denovo;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class DonateActivity extends AppCompatActivity {

    private static final String TAG = "DonateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        ImageView btnBack = (ImageView) findViewById(R.id.back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DonateActivity.this.finish();
            }
        });

        findViewById(R.id.settings).setVisibility(View.GONE);
        findViewById(R.id.search).setVisibility(View.GONE);

        ViewPager viewPager = (ViewPager) findViewById(R.id.donate_viewpager);
        DonateAdapter adapter = new DonateAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.donate_steps);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_one_active);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_two_incomplete);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_three_incomplete);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_check_incomplete);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        tabLayout.getTabAt(0).setIcon(R.drawable.ic_one_active);
                        tabLayout.getTabAt(1).setIcon(R.drawable.ic_two_incomplete);
                        tabLayout.getTabAt(2).setIcon(R.drawable.ic_three_incomplete);
                        tabLayout.getTabAt(3).setIcon(R.drawable.ic_check_incomplete);
                        break;
                    case 1:
                        tabLayout.getTabAt(0).setIcon(R.drawable.ic_one_complete);
                        tabLayout.getTabAt(1).setIcon(R.drawable.ic_two_active);
                        tabLayout.getTabAt(2).setIcon(R.drawable.ic_three_incomplete);
                        tabLayout.getTabAt(3).setIcon(R.drawable.ic_check_incomplete);
                        break;
                    case 2:
                        tabLayout.getTabAt(0).setIcon(R.drawable.ic_one_complete);
                        tabLayout.getTabAt(1).setIcon(R.drawable.ic_two_complete);
                        tabLayout.getTabAt(2).setIcon(R.drawable.ic_three_active);
                        tabLayout.getTabAt(3).setIcon(R.drawable.ic_check_incomplete);
                        break;
                    case 3:
                        tabLayout.getTabAt(0).setIcon(R.drawable.ic_one_complete);
                        tabLayout.getTabAt(1).setIcon(R.drawable.ic_two_complete);
                        tabLayout.getTabAt(2).setIcon(R.drawable.ic_three_complete);
                        tabLayout.getTabAt(3).setIcon(R.drawable.ic_check_complete);
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
}
