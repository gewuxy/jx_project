package jx.csp.ui.activity.contribution;

import android.view.View;

import java.util.List;

import jx.csp.R;
import jx.csp.adapter.contribution.SelectPlatformAdapter;
import jx.csp.model.contribution.SelectPlatform;
import jx.csp.model.contribution.SelectPlatform.TSelectPlatform;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.DeliveryAPI;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseListActivity;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;

/**
 * 投稿  选择平台
 *
 * @author CaiXiang
 * @since 2018/3/9
 */

public class SelectPlatformActivity extends BaseListActivity<SelectPlatform, SelectPlatformAdapter> implements OnAdapterClickListener {

    @Override
    public void initData() {

    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.select_platform, this);
    }

    @Override
    public void setViews() {
        super.setViews();

        setDividerHeight(0);
        setOnAdapterClickListener(this);
        refresh(RefreshWay.embed);
        exeNetworkReq(DeliveryAPI.platform().build());
    }

    @Override
    public void onAdapterClick(int position, View v) {
        if (v.getId() == R.id.select_platform_item_tv_contribution) {
            SelectPlatform item = getItem(position);
            if (item.getString(TSelectPlatform.id).equals("1")) {
                ContributeHistoryHotUnitNumActivityRouter.create(item).route(this);
            } else {
                showToast("test");
            }
        }
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.evs(resp.getText(), SelectPlatform.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        stopRefresh();
        setViewState(ViewState.normal);
        if (r.isSucceed()) {
            setData((List<SelectPlatform>) r.getList());
        }
    }
}
