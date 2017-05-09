package yy.doctor.activity.meeting;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import lib.network.error.NetError;
import lib.network.model.NetworkResponse;
import lib.ys.LogMgr;
import lib.ys.adapter.MultiAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.view.ViewUtil;
import lib.yy.activity.base.BaseListActivity;
import lib.yy.network.ListResponse;
import yy.doctor.BuildConfig;
import yy.doctor.R;
import yy.doctor.adapter.CommentAdapter;
import yy.doctor.model.meet.Histories;
import yy.doctor.model.meet.Histories.THistories;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 会议评论界面
 *
 * @author : GuoXuan
 * @since : 2017/5/2
 */

public class MeetingCommentActivity extends BaseListActivity<String> {

    private TextView mTvSend;
    private EditText mEtSend;
    private static final int KHistories = 0;
    private static final int KSend = 1;
    private String mMeetId;

    @Override
    public void initData() {
        for (int i = 0; i < 14; ++i) {
            addItem("");
        }

        if (BuildConfig.TEST) {
            mMeetId = "17042512131640894904";
        }
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_comment;
    }

    @Override
    public View createHeaderView() {
        return ViewUtil.inflateSpaceViewDp(18);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "评论", this);
    }

    @Override
    public void findViews() {
        super.findViews();
        mTvSend = findView(R.id.meeting_comment_tv_send);
        mEtSend = findView(R.id.meeting_comment_et_send);
    }

    @Override
    public void setViews() {
        super.setViews();
        mTvSend.setOnClickListener(this);
        refresh(RefreshWay.embed);
        exeNetworkRequest(KHistories, NetFactory.histories(mMeetId));
    }

    @Override
    public MultiAdapterEx<String, ? extends ViewHolderEx> createAdapter() {
        return new CommentAdapter();
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResponse nr) throws Exception {
        if (id == KHistories) {
            return JsonParser.evs(nr.getText(), Histories.class);
        }
        return JsonParser.error(nr.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        setViewState(ViewState.normal);
        if (id == KHistories) {
            ListResponse<Histories> r = (ListResponse<Histories>) result;
            List<Histories> data = r.getData();
            for (Histories histories : data) {
                LogMgr.d(TAG, histories.getString(THistories.message));
            }
        } else if (id == KSend) {
            showToast("发送成功");
        }
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);
        setViewState(ViewState.error);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.meeting_comment_tv_send:
                String meesage = mEtSend.getText().toString().trim();
                refresh(RefreshWay.dialog);
                exeNetworkRequest(KSend , NetFactory.send(mMeetId,meesage,"0"));
                break;
            default:
                break;
        }
    }
}
