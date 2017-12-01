package jx.doctor.ui.activity.search;

import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import org.json.JSONException;

import inject.annotation.router.Route;
import lib.network.model.interfaces.IResult;
import lib.ys.util.KeyboardUtil;
import lib.ys.util.TextUtil;
import jx.doctor.R;
import jx.doctor.model.meet.Meeting;
import jx.doctor.network.JsonParser;
import jx.doctor.network.NetworkApiDescriptor.MeetAPI;

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
            addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    getSearchView().requestFocus();
                    KeyboardUtil.showFromView(getSearchView());
                    removeOnGlobalLayoutListener(this);
                }

            });
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
    public IResult parseNetworkResponse(int id, String text) throws JSONException {
        return JsonParser.evs(text, Meeting.class);
    }

    @Override
    public void onNetRefreshSuccess() {
        super.onNetRefreshSuccess();

        // 单独管理空界面
        if (isEmpty()) {
            showView(mLayoutEmpty);
        } else {
            goneView(mLayoutEmpty);
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
