package com.denovo.denovo;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by abhinavkhushalani on 12/2/16.
 */

public class DonateAdapter extends SmartFragmentPagerAdapter {
    private Context mContext;

    public DonateAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (getRegisteredFragment(0) == null) {
                    return new DonateItemInfoFragment();
                } else {
                    return getRegisteredFragment(0);
                }
            case 1:
                if (getRegisteredFragment(1) == null) {
                    return new DonateConditionFragment();
                } else {
                    return getRegisteredFragment(1);
                }
            case 2:
                if (getRegisteredFragment(2) == null) {
                    return new DonatePriceFragment();
                } else {
                    return getRegisteredFragment(2);
                }
            case 3:
                if (getRegisteredFragment(3) == null) {
                    return new DonateSummaryFragment();
                } else {
                    return getRegisteredFragment(3);
                }
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
