package yy.doctor.ui.activity.search;

import android.view.View;

import java.util.List;

import inject.annotation.router.Route;
import lib.network.model.NetworkResp;
import lib.ys.util.KeyboardUtil;
import lib.ys.util.TextUtil;
import lib.yy.network.ListResult;
import yy.doctor.R;
import yy.doctor.model.meet.Meeting;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkAPISetter.MeetAPI;

/**
 * 搜索会议结果
 *
 * @author : GuoXuan
 * @since : 2017/5/3
 */
@Route
public class MeetingResultActivity extends BaseSearchResultActivity {

    private View mLayoutEmpty; // 空视图

    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_result;
    }

    @Override
    public void findViews() {
        super.findViews();

        mLayoutEmpty = findView(R.id.meeting_result_layout_empty);
    }

    @Override
    public void setViews() {
        super.setViews();

        if (TextUtil.isEmpty(mSearchContent)) {
            getSearchView().requestFocus();
            KeyboardUtil.showFromView(getSearchView());
        } else {
            showView(mLayoutEmpty);
        }
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(MeetAPI.searchMeet(mSearchContent, getOffset(), getLimit()).build());
    }

    @Override
    public boolean enableInitRefresh() {
        return TextUtil.isNotEmpty(mSearchContent);
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        ListResult result = JsonParser.evs(r.getText(), Meeting.class);
        return result;
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);

        ListResult<Meeting> r = (ListResult) result;
        if (r.isSucceed()) {
            List meets = r.getData();
            // 单独管理空界面
            if (meets != null && meets.size() > 0) {
                goneView(mLayoutEmpty);
            } else {
                showView(mLayoutEmpty);
            }
        }
    }

    @Override
    public View createEmptyFooterView() {
        return null;
    }

    @Override
    protected String getEmptyText() {
        return "暂无相关会议";
    }

    @Override
    protected CharSequence getSearchHint() {
        return "搜索会议";
    }
}
