package jx.csp.ui.activity.me;

import android.support.annotation.NonNull;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseActivity;
import pay.PayAction;

/**
 * @auther Huoxuyu
 * @since 2017/11/7
 */

abstract public class BaseFlowRateActivity extends BaseActivity{
    @Override
    public void initData() {
        PayAction.startPayPalService(this);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return 0;
    }

    @Override
    public void initNavBar(NavBar bar) {

    }

    @Override
    public void findViews() {

    }

    @Override
    public void setViews() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PayAction.stopPayPalService(this);
    }
}
