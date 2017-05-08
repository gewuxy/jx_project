package lib.ys.impl;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import lib.ys.adapter.FragPagerAdapterEx;

public class FragPagerAdapterImpl extends FragPagerAdapterEx<Fragment, PagerTitleImpl> {

    public FragPagerAdapterImpl(FragmentManager fm) {
        super(fm);
    }
}
