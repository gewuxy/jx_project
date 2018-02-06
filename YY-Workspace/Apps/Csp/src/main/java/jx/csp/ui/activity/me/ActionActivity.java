package jx.csp.ui.activity.me;

import android.view.View;

import jx.csp.R;
import jx.csp.adapter.me.ActionAdapter;
import jx.csp.dialog.ShareDialog;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.me.Action;
import jx.csp.model.me.Action.TAction;
import jx.csp.network.NetworkApiDescriptor.CommonAPI;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseSRListActivity;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.ui.other.NavBar;

/**
 * 活动页面  目前是进入新手指导页面
 *
 * @author CaiXiang
 * @since 2018/2/3
 */

public class ActionActivity extends BaseSRListActivity<Action, ActionAdapter> implements OnAdapterClickListener {

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.join_green_hands_guide, this);
    }

    @Override
    public void setViews() {
        super.setViews();
        setOnAdapterClickListener(this);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(CommonAPI.action().build());
    }

    @Override
    public void onItemClick(View v, int position) {
        Action item = getItem(position);
        GreenHandsGuideActivityRouter.create(item.getString(TAction.id)).route(this);
    }

    @Override
    public void onAdapterClick(int position, View v) {
        Action action = getItem(position);
        Meet meet = new Meet();
        meet.put(TMeet.id, action.getString(TAction.id));
        meet.put(TMeet.coverUrl, action.getString(TAction.coverUrl));
        meet.put(TMeet.title, action.getString(TAction.title));
        ShareDialog dialog = new ShareDialog(this, meet, false);
        dialog.show();
    }
}
