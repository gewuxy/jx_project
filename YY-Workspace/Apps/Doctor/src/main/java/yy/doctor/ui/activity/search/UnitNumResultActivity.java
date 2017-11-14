package yy.doctor.ui.activity.search;

import org.json.JSONException;

import java.util.List;

import inject.annotation.router.Route;
import lib.network.model.NetworkError;
import lib.network.model.interfaces.IResult;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;
import yy.doctor.model.search.Hot;
import yy.doctor.model.search.IRec;
import yy.doctor.model.unitnum.UnitNum;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkApiDescriptor.UnitNumAPI;

/**
 * 搜索单位号的结果
 *
 * @auther yuansui
 * @since 2017/6/12
 */
@Route
public class UnitNumResultActivity extends BaseSearchResultActivity {

    private final int KRecUnitNum = 2; // 热门单位号
    private final int KUnitNum = 1; // 单位号

    @Override
    public void getDataFromNet() {
        if (TextUtil.isEmpty(mSearchContent)) {
            exeNetworkReq(KRecUnitNum, UnitNumAPI.recommendUnitNum().build());
        } else {
            exeNetworkReq(KUnitNum, UnitNumAPI.searchUnitNum(mSearchContent, getOffset(), getLimit()).build());
        }
    }

    @Override
    public IResult parseNetworkResponse(int id, String text) throws JSONException {
        Result result = JsonParser.evs(text, UnitNum.class);

        if (result.isSucceed()) {
            List<IRec> unitNums = result.getList();
            if (id == KRecUnitNum && unitNums != null && unitNums.size() > 0) {
                // 推荐的单位号要显示热门单位号, 有才显示
                unitNums.add(0, new Hot());
                IResult<IRec> ret = new Result<>();
                ret.setCode(result.getCode());
                ret.setData(unitNums);
                return ret;
            }
        }

        return result;
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
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
