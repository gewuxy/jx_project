package yy.doctor.activity.search;

import android.content.Context;
import android.content.Intent;

import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.util.LaunchUtil;
import lib.yy.network.ListResult;
import yy.doctor.Extra;
import yy.doctor.model.search.Hot;
import yy.doctor.model.unitnum.UnitNum;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;

/**
 * 搜索单位号的结果
 *
 * @auther yuansui
 * @since 2017/6/12
 */
public class UnitNumResultActivity extends ResultActivity {

    private final int KRecUnitNum = 2; // 热门单位号

    public static void nav(Context context, String searchContent) {
        Intent i = new Intent(context, UnitNumResultActivity.class)
                .putExtra(Extra.KData, searchContent);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void setViews() {
        super.setViews();

        mEtSearch.setHint("搜索单位号");
    }

    @Override
    protected void searchEmpty() {
        refresh(RefreshWay.embed);
        exeNetworkReq(KRecUnitNum, NetFactory.searchRecUnitNum());
    }

    @Override
    protected void search() {
        refresh(RefreshWay.embed);
        // 把原来的remove
        removeAll();
        exeNetworkReq(KUnitNum, NetFactory.searchUnitNum(mSearchContent));
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.evs(r.getText(), UnitNum.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        ListResult r = (ListResult) result;
        if (r.isSucceed()) {
            mUnitNums = r.getData();
            if (id == KRecUnitNum) {
                // 推荐的单位号要显示热门单位号
                addItem(new Hot());
            }
            if (mUnitNums != null && mUnitNums.size() > 0) {
                addAll(mUnitNums);
            }
            setViewState(ViewState.normal);
            invalidate();
        } else {
            onNetworkError(id, new NetError(id, r.getError()));
            showToast(r.getError());
        }
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);

        if (id == KRecUnitNum) {
            setViewState(ViewState.normal);
        }
    }

    @Override
    protected String getEmptyText() {
        return "暂无相关单位号";
    }
}
