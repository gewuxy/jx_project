package jx.csp.ui.activity.me;

import jx.csp.R;
import jx.csp.adapter.me.ActionAdapter;
import jx.csp.model.me.Action;
import jx.csp.network.NetworkApiDescriptor.CommonAPI;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseSRListActivity;
import lib.ys.ui.other.NavBar;

/**
 * 活动页面  目前是进入新手指导页面
 *
 * @author CaiXiang
 * @since 2018/2/3
 */

public class ActionActivity extends BaseSRListActivity<Action, ActionAdapter> {

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.join_green_hands_guide, this);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(CommonAPI.action().build());
    }

}
