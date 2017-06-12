package yy.doctor.activity;

import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseSRListActivity;
import yy.doctor.adapter.NoticeAdapter;
import yy.doctor.util.Util;

/**
 * 通知页面
 *
 * @author CaiXiang
 * @since 2017/5/3
 */
public class NoticeActivity extends BaseSRListActivity<String, NoticeAdapter> {

    @Override
    public void initData() {

        for (int i = 0; i < 12; ++i) {
            addItem(i + "");
        }
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, "通知", this);
    }

    @Override
    public void setViews() {
        super.setViews();

    }

    @Override
    public void getDataFromNet() {

    }

    @Override
    protected String getEmptyText() {
        return "暂时没有相关通知";
    }
}
