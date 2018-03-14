package jx.csp.ui.activity.contribution;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.R;
import jx.csp.adapter.contribution.ContributeHistoryHotUnitNumAdapter;
import jx.csp.model.contribution.ContributeHistories;
import jx.csp.model.contribution.ContributeHistory;
import jx.csp.model.contribution.ContributeHistory.TContributeHistory;
import jx.csp.model.contribution.ContributeHistoryEmpty;
import jx.csp.model.contribution.HotUnitNum;
import jx.csp.model.contribution.HotUnitNum.THotUnitNum;
import jx.csp.model.contribution.IContributeHistoryHotUnitNum;
import jx.csp.model.contribution.LargeDivider;
import jx.csp.model.contribution.ListTitle;
import jx.csp.model.contribution.ListTitle.TListTitle;
import jx.csp.model.contribution.SelectPlatform;
import jx.csp.model.contribution.SelectPlatform.TSelectPlatform;
import jx.csp.model.main.Meet;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.DeliveryAPI;
import jx.csp.util.Util;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.activity.base.BaseListActivity;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.res.ResLoader;

/**
 * 投稿历史、热门单位号
 *
 * @author CaiXiang
 * @since 2018/3/9
 */

@Route
public class ContributeHistoryHotUnitNumActivity extends BaseListActivity<IContributeHistoryHotUnitNum, ContributeHistoryHotUnitNumAdapter> {

    @Arg
    Meet mMeet;

    @Arg
    SelectPlatform mPlatform;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);
        View view = inflate(R.layout.layout_contribute_history_hot_unit_num_nav_bar);
        bar.addViewLeft(view, v -> SearchUnitNumActivityRouter.create(mMeet).route(this));
    }

    @Override
    public void setViews() {
        super.setViews();

        getAdapter().setMeetData(mMeet);
        refresh(RefreshWay.embed);
        exeNetworkReq(DeliveryAPI.contributeHotUnitNum().build());
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.evs(resp.getText(), HotUnitNum.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        stopRefresh();

        if (r.isSucceed()) {
            setViewState(ViewState.normal);
            List<HotUnitNum> list = r.getList();

            List<IContributeHistoryHotUnitNum> data = new ArrayList<>();
            List<ContributeHistory> historyList = new ArrayList<>();
            List<HotUnitNum> hotList = new ArrayList<>();
            for (HotUnitNum hotUnitNum : list) {
                if (hotUnitNum.getInt(THotUnitNum.type) == 0) {
                    ContributeHistory item = new ContributeHistory();
                    item.put(TContributeHistory.headimg, hotUnitNum.getString(THotUnitNum.headimg));
                    item.put(TContributeHistory.acceptName, hotUnitNum.getString(THotUnitNum.acceptName));
                    item.put(TContributeHistory.acceptCount, hotUnitNum.getInt(THotUnitNum.acceptCount));
                    item.put(TContributeHistory.acceptId, hotUnitNum.getInt(THotUnitNum.acceptId));
                    historyList.add(item);
                } else {
                    hotList.add(hotUnitNum);
                }
            }

            ListTitle listTitle1 = new ListTitle();
            listTitle1.put(TListTitle.title, mPlatform.getString(TSelectPlatform.platformName));
            data.add(listTitle1);

            if (historyList.size() > 0) {
                ContributeHistories contributeHistories = new ContributeHistories();
                contributeHistories.setData(historyList);
                data.add(contributeHistories);
            } else {
                ContributeHistoryEmpty empty = new ContributeHistoryEmpty();
                data.add(empty);
            }

            LargeDivider divider = new LargeDivider();
            data.add(divider);

            ListTitle listTitle2 = new ListTitle();
            listTitle2.put(TListTitle.title, ResLoader.getString(R.string.hot_unit_num));
            data.add(listTitle2);

            data.addAll(hotList);

            addAll(data);
        } else {
            setViewState(ViewState.error);
        }
    }

    @Override
    public boolean onRetryClick() {
        super.onRetryClick();

        refresh(RefreshWay.embed);
        exeNetworkReq(DeliveryAPI.contributeHotUnitNum().build());

        return false;
    }

    @Override
    public void onNotify(int type, Object data) {
        if ( type == NotifyType.finish_contribute) {
            finish();
        }
    }
}
