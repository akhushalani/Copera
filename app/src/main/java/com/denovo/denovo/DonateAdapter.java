package com.denovo.denovo;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by abhinavkhushalani on 12/2/16.
 */

public class DonateAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public DonateAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AccountFragment();
            case 1:
                return new AccountFragment();
            case 2:
                return new AccountFragment();
            case 3:
                return new AccountFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
