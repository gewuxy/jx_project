package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import java.util.List;

import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.activity.base.BaseSRListActivity;
import lib.yy.network.ListResult;
import lib.yy.network.Result;
import yy.doctor.Extra;
import yy.doctor.adapter.meeting.VideoCategoryAdapter;
import yy.doctor.model.meet.video.Course;
import yy.doctor.model.meet.video.Course.TCourse;
import yy.doctor.model.meet.video.Detail;
import yy.doctor.model.meet.video.Detail.TDetail;
import yy.doctor.model.meet.video.Intro;
import yy.doctor.model.meet.video.Intro.TIntro;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 视频列表(大分类)界面
 *
 * @author : GuoXuan
 * @since : 2017/5/24
 */

public class VideoCategoryActivity extends BaseSRListActivity<Detail, VideoCategoryAdapter> implements OnAdapterClickListener {

    private String mMeetId;
    private String mModuleId;
    private List<Detail> mDetails;

    public static void nav(Context context, String meetId, String moduleId) {
        Intent i = new Intent(context, VideoCategoryActivity.class)
                .putExtra(Extra.KMeetId, meetId)
                .putExtra(Extra.KModuleId, moduleId);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mMeetId = getIntent().getStringExtra(Extra.KMeetId);
        mModuleId = getIntent().getStringExtra(Extra.KModuleId);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "视频", this);
    }

    @Override
    public void setViews() {
        super.setViews();
        setOnAdapterClickListener(this);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.toVideo(mMeetId, mModuleId));
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Intro.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        setViewState(ViewState.normal);
        Result<Intro> r = (Result<Intro>) result;
        if (r.isSucceed()) {
            Intro intro = r.getData();
            Course course = intro.getEv(TIntro.course);
            mDetails = course.getList(TCourse.details);
            addAll(mDetails);

            ListResult<Detail> listResult = new ListResult<>();
            listResult.setData(mDetails);
            super.onNetworkSuccess(id, listResult);
        }
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);
        setViewState(ViewState.error);
    }

    @Override
    public void onAdapterClick(int position, View v) {
        VideoDetailActivity.nav(VideoCategoryActivity.this, mDetails.get(position).getString(TDetail.id));
    }
}
