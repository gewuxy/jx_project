package lib.ys.inst;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import lib.ys.adapter.FragPagerAdapterEx;

public class FragPagerAdapterInst extends FragPagerAdapterEx<Fragment, PagerTitle> {

    public FragPagerAdapterInst(FragmentManager fm) {
        super(fm);
    }
}
