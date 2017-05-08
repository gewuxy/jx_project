package yy.doctor.activity.me;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseListActivity;
import yy.doctor.R;
import yy.doctor.adapter.UpdateLogAdapter;

/**
 * 更新日志
 *
 * @author CaiXiang
 * @since 2017/4/26
 */
public class UpdateLogActivity extends BaseListActivity<String> {

    @Override
    public void initData() {

        for (int i = 0; i < 8; ++i) {
            addItem(" " + i);
        }

    }

    @Override
    public void initNavBar(NavBar bar) {

        bar.addBackIcon(R.mipmap.nav_bar_ic_back, "更新日志", this);

    }

    @Override
    public MultiAdapterEx<String, ? extends ViewHolderEx> createAdapter() {
        return new UpdateLogAdapter();
    }

}
