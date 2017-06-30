package yy.doctor.ui.activity.search;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.util.KeyboardUtil;
import lib.ys.util.LaunchUtil;
import lib.yy.network.ListResult;
import yy.doctor.Extra;
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

    private LinearLayout mEmpty; // 第一次进来要显示的视图
    private View mLayoutEmpty;

    public static void nav(Context context, String searchContent) {
        Intent i = new Intent(context, MeetingResultActivity.class)
                .putExtra(Extra.KData, searchContent);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void findViews() {
        super.findViews();

        mEmpty = findView(R.id.meeting_result_layout_empty);
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
        refresh(RefreshWay.embed);
        removeAll();
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
            mMeets = r.getData();
            if (mMeets != null && mMeets.size() > 0) {
                addAll(mMeets);
                goneView(mEmpty);
            } else {
                // 单独管理空界面
                if (mLayoutEmpty == null) {
                    mLayoutEmpty = inflate(R.layout.layout_empty_footer);
                    TextView tv = (TextView) mLayoutEmpty.findViewById(R.id.empty_footer_tv);
                    tv.setText("暂无相关会议");
                    mEmpty.addView(mLayoutEmpty);
                }
                showView(mEmpty);
            }
            setViewState(ViewState.normal);
            invalidate();
        } else {
            onNetworkError(id, new NetError(id, r.getError()));
            showToast(r.getError());
        }
    }

    @Override
    public View createEmptyView() {
        return null;
    }

}
