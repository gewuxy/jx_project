package yy.doctor.activity.meeting;

import android.support.annotation.NonNull;
import android.view.View;

import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * 搜索结果界面
 *
 * @author : GuoXuan
 * @since : 2017/5/3
 */

public class MeetingSearchResultActivity extends BaseActivity {

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_search_result;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);
        View v = inflate(R.layout.layout_meeting_nav_bar_search);
        bar.addViewLeft(v, null);
    }

    @Override
    public void findViews() {

    }

    @Override
    public void setViews() {

    }

}
