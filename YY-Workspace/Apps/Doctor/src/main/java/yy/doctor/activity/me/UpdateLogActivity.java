package yy.doctor.activity.me;

import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseWebViewActivity;
import yy.doctor.R;

/**
 * 更新日志
 *
 * @author CaiXiang
 * @since 2017/4/26
 */
public class UpdateLogActivity extends BaseWebViewActivity {

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addBackIcon(R.mipmap.nav_bar_ic_back, "更新日志", this);
    }


    @Override
    protected void onLoadStart() {

    }
}
