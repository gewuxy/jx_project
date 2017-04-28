package yy.doctor.activity.me;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.ex.NavBar;
import lib.yy.activity.base.BaseListActivity;
import yy.doctor.adapter.CollectionMeetingAdapter;
import yy.doctor.util.Util;

/**
 * 收藏会议
 *
 * @author CaiXiang
 * @since 2017/4/12
 */
public class CollectionMeetingActivity extends BaseListActivity<String> {

    @Override
    public void initData() {

        for (int i = 0; i < 6; ++i) {
            addItem(i + "");
        }

    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, "收藏的会议", this);

    }

    @Override
    public void setViews() {
        super.setViews();

        getLv().setDivider(null);
    }

    @Override
    public MultiAdapterEx<String, ? extends ViewHolderEx> createAdapter() {
        return new CollectionMeetingAdapter();
    }


}
