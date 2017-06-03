package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import lib.network.model.err.NetError;
import lib.network.model.NetworkResp;
import lib.ys.LogMgr;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.decor.DecorViewEx.TNavBarState;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.ToastUtil;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Result;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.meet.Course;
import yy.doctor.model.meet.Course.TCourse;
import yy.doctor.model.meet.Ppt;
import yy.doctor.model.meet.Ppt.TPpt;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;
import yy.doctor.view.CircleProgressView;

/**
 * 观看会议界面
 * <p>
 * 日期 : 2017/4/24
 * 创建人 : guoxuan
 */

public class MeetingPPTActivity extends BaseActivity {

    private NetworkImageView mNivPlay;

    private CircleProgressView mView;

    private String mMeetId;
    private String mModuleId;

    public static void nav(Context context, String meetId, String moduleId) {
        Intent i = new Intent(context, MeetingPPTActivity.class)
                .putExtra(Extra.KMeetId, meetId)
                .putExtra(Extra.KModuleId, moduleId);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mMeetId = getIntent().getStringExtra(Extra.KMeetId);
        mModuleId = getIntent().getStringExtra(Extra.KModuleId);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_ppt;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, this);
        bar.addViewRight(R.mipmap.nav_bar_ic_details, v -> ToastUtil.makeToast("响应了"));
        bar.setBackgroundAlpha(0);
    }

    @Override
    public void findViews() {
        mNivPlay = findView(R.id.meeting_ppt_iv_play_niv);
        mView = findView(R.id.meeting_ppt_v_progress);

    }

    @Override
    public void setViews() {
        mNivPlay.placeHolder(R.mipmap.ic_default_meeting_single_detail).load();

        mView.setProgress(100);
        refresh(RefreshWay.embed);
        exeNetworkReq(0, NetFactory.toPpt(mMeetId, mModuleId));
    }

    @Override
    protected TNavBarState getNavBarState() {
        return TNavBarState.above;
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Ppt.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        setViewState(ViewState.normal);
        Result<Ppt> r = (Result<Ppt>) result;
        if (r.isSucceed()) {
            Ppt data = r.getData();
            Course course = data.getEv(TPpt.course);
            LogMgr.d("标题", course.getString(TCourse.title));
        }
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);
        setViewState(ViewState.error);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }

    }
}
