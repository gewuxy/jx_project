package yy.doctor.activity.meeting.search;

import android.view.View;

import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.util.KeyboardUtil;
import lib.yy.network.ListResult;
import yy.doctor.R;
import yy.doctor.model.meet.Meeting;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;

/**
 * 搜索会议结果
 *
 * @author : GuoXuan
 * @since : 2017/5/3
 */
public class MeetingResultActivity extends ResultActivity {

    private View mEmpty; // 第一次进来要显示的视图

    @Override
    public void findViews() {
        super.findViews();

        mEmpty = findView(R.id.meeting_result_empty);
    }

    @Override
    public void setViews() {
        super.setViews();

        mEtSearch.setHint("搜索会议");
    }

    @Override
    protected void searchEmpty() {
        showView(mEmpty);
        runOnUIThread(() -> {
            mEtSearch.requestFocus();
            KeyboardUtil.showFromView(mEtSearch);
        });
    }

    @Override
    protected void search() {
        goneView(mEmpty);
        refresh(RefreshWay.embed);
        exeNetworkReq(KMeeting, NetFactory.searchMeeting(mSearchContent));
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.evs(r.getText(), Meeting.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        ListResult r = (ListResult) result;
        if (r.isSucceed()) {
            setViewState(ViewState.normal);
            mMeets = r.getData();
            if (mMeets != null && mMeets.size() > 0) {
                addAll(mMeets);
                invalidate();
            }
        } else {
            showToast(r.getError());
        }
    }

    @Override
    public View createEmptyView() {
        return null;
    }

}
