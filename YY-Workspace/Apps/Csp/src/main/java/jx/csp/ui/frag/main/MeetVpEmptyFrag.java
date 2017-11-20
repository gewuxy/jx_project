package jx.csp.ui.frag.main;

import android.os.Bundle;
import android.support.annotation.NonNull;

import jx.csp.R;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseFrag;

/**
 * 首页左右滑动列表没有数据时的frag
 *
 * @auther WangLan
 * @since 2017/10/25
 */

public class MeetVpEmptyFrag extends BaseFrag {

    @Override
    public void initData(Bundle state) {}

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.frag_main_vp_empty;
    }

    @Override
    public void initNavBar(NavBar bar) {}

    @Override
    public void findViews() {}

    @Override
    public void setViews() {}
}
