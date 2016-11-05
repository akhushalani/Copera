package com.denovo.denovo;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by abhinavkhushalani on 11/2/16.
 */

public class PageAdapter extends FragmentPagerAdapter{
    private Context mContext;

    public PageAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FeedFragment();
            case 1:
                return new AccountFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
