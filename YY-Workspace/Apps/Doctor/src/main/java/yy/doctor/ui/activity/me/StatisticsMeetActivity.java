package yy.doctor.ui.activity.me;

import android.support.annotation.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.util.Util;
import yy.doctor.view.StatisticsView;

/**
 * 参会统计
 *
 * @auther : GuoXuan
 * @since : 2017/7/25
 */
public class StatisticsMeetActivity extends BaseActivity {

    private StatisticsView mLayoutUnitNum;
    private StatisticsView mLayoutMeet;

    Map<String, Integer> mStatics;

    @Override
    public void initData() {
        mStatics = new LinkedHashMap<>();
        mStatics.put("2月1号", 1000);
        mStatics.put("2月2号", 6);
        mStatics.put("2月3号", 5);
        mStatics.put("2月4号", 4);
        mStatics.put("2月5号", 3);
        mStatics.put("2月6号", 0);
        mStatics.put("2月7号", 1);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_statistics_meet;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "参会统计", this);
    }

    @Override
    public void findViews() {
        mLayoutUnitNum = findView(R.id.statistics_meet_layout_unit_num);
        mLayoutMeet = findView(R.id.statistics_meet_layout_meet);
    }

    @Override
    public void setViews() {
        mLayoutUnitNum.setMeets(mStatics);
        mLayoutMeet.setMeets(mStatics);
    }

}
