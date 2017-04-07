package yy.doctor.frag;

import android.support.annotation.NonNull;

import lib.ys.frag.FragEx;
import yy.doctor.R;
import yy.doctor.activity.MainActivity;
import yy.doctor.util.Util;

/**
 * @author Administrator
 * @since 2017/4/5
 */
public class HomeFrag extends FragEx {

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.frag_home;
    }

    @Override
    public void initTitleBar() {
        Util.addMenuIcon(getTitleBar(), (MainActivity) getActivity());
    }

    @Override
    public void findViews() {

    }

    @Override
    public void setViewsValue() {

    }
}
