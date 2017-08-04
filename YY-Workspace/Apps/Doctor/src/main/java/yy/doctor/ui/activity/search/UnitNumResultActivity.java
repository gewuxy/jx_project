package yy.doctor.ui.activity.search;

import java.util.List;

import lib.annotation.AutoIntent;
import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.network.model.interfaces.IListResult;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.util.TextUtil;
import lib.yy.network.ListResult;
import yy.doctor.model.search.Hot;
import yy.doctor.model.search.IRec;
import yy.doctor.model.unitnum.UnitNum;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;

/**
 * 搜索单位号的结果
 *
 * @auther yuansui
 * @since 2017/6/12
 */
@AutoIntent
public class UnitNumResultActivity extends BaseSearchResultActivity {

    private final int KRecUnitNum = 2; // 热门单位号
    private final int KUnitNum = 1; // 单位号

    @Override
    public void getDataFromNet() {
        if (TextUtil.isEmpty(mSearchContent)) {
            exeNetworkReq(KRecUnitNum, NetFactory.searchRecUnitNum());
        } else {
            exeNetworkReq(KUnitNum, NetFactory.searchUnitNum(mSearchContent, getOffset(), getLimit()));
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        ListResult result = JsonParser.evs(r.getText(), UnitNum.class);

        if (result.isSucceed()) {
            List<IRec> unitNums = result.getData();
            if (id == KRecUnitNum && unitNums != null && unitNums.size() > 0) {
                // 推荐的单位号要显示热门单位号, 有才显示
                unitNums.add(0, new Hot());
                IListResult<IRec> ret = new ListResult<>();
                ret.setCode(result.getCode());
                ret.setData(unitNums);
                return ret;
            }
        }

        return result;
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);

        if (id == KRecUnitNum) {
            // 热门错误显示正常
            setViewState(ViewState.normal);
        }
    }

    @Override
    protected CharSequence getSearchHint() {
        return "搜索单位号";
    }

    @Override
    protected String getEmptyText() {
        return "暂无相关单位号";
    }
}
