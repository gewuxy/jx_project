package jx.csp.ui.activity.contribution;

import android.view.View;

import java.util.List;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.adapter.contribution.SelectPlatformAdapter;
import jx.csp.model.contribution.SelectPlatform;
import jx.csp.model.contribution.SelectPlatform.TSelectPlatform;
import jx.csp.model.contribution.UnitNum;
import jx.csp.model.contribution.UnitNum.TUnitNum;
import jx.csp.model.main.Meet;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.DeliveryAPI;
import jx.csp.util.Util;
import lib.jx.notify.Notifier.NotifyType;
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
@Route
public class SelectPlatformActivity extends BaseListActivity<SelectPlatform, SelectPlatformAdapter> implements OnAdapterClickListener {

    @Arg
    Meet mMeet;

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

        setOnAdapterClickListener(this);
        refresh(RefreshWay.embed);
        exeNetworkReq(DeliveryAPI.platform().build());
    }

    @Override
    public void onAdapterClick(int position, View v) {
        if (v.getId() == R.id.select_platform_item_tv_contribution) {
            SelectPlatform item = getItem(position);
            if (item.getString(TSelectPlatform.id).equals("1")) {
                ContributeHistoryHotUnitNumActivityRouter.create(mMeet, item).route(this);
            } else {
                UnitNum unitNum = new UnitNum();
                unitNum.put(TUnitNum.id, item.getInt(TSelectPlatform.id));
                unitNum.put(TUnitNum.platformName, item.getString(TSelectPlatform.platformName));
                unitNum.put(TUnitNum.imgUrl, item.getString(TSelectPlatform.imgUrl));
                unitNum.put(TUnitNum.unitNumId, item.getInt(TSelectPlatform.unitId));
                ContributeActivityRouter.create(mMeet, unitNum).route(this);
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
        if (r.isSucceed()) {
            setViewState(ViewState.normal);
            setData((List<SelectPlatform>) r.getList());
        } else {
            setViewState(ViewState.error);
        }
    }

    @Override
    public boolean onRetryClick() {
        super.onRetryClick();

        refresh(RefreshWay.embed);
        exeNetworkReq(DeliveryAPI.platform().build());

        return false;
    }

    @Override
    public void onNotify(int type, Object data) {
        if ( type == NotifyType.finish_contribute) {
            finish();
        }
    }
}
