package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.network.model.interfaces.IListResult;
import lib.ys.YSLog;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TextUtil;
import lib.yy.activity.base.BaseSRListActivity;
import lib.yy.network.ListResult;
import lib.yy.network.Result;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.meeting.VideoCategoryAdapter;
import yy.doctor.model.meet.Submit;
import yy.doctor.model.meet.Submit.TSubmit;
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

    private static final int KIdToVideo = 0;
    private static final int KIdVideo = 1;

    private String mPreId;

    private TextView mBarTvRight; // 右边的文本
    private long mStudyTime; // 学习时间
    private int mClickPosition; // 点击第几个
    private Submit mSubmit;

    public static void nav(Context context, Submit submit, String preId) {
        Intent i = new Intent(context, VideoCategoryActivity.class)
                .putExtra(Extra.KData, submit)
                .putExtra(Extra.KId, preId);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mSubmit = (Submit) getIntent().getSerializableExtra(Extra.KData);
        mPreId = getIntent().getStringExtra(Extra.KId);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.video, this);
        mBarTvRight = bar.addTextViewRight(getString(R.string.video_studied_no_start), null);
        goneView(mBarTvRight); // 默认隐藏
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnAdapterClickListener(this);
    }

    @Override
    public void getDataFromNet() {
        if (TextUtil.isEmpty(mPreId)) {
            exeNetworkReq(KIdToVideo, NetFactory.toVideo(mSubmit.getString(TSubmit.meetId),
                    mSubmit.getString(TSubmit.moduleId)));
        } else {
            exeNetworkReq(KIdVideo, NetFactory.video(mPreId));
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == KIdToVideo) {
            // 筛选需要的数据类型
            Result<Intro> result = JsonParser.ev(r.getText(), Intro.class);
            ListResult<Detail> listResult = new ListResult<>();
            if (result.isSucceed()) {
                Intro intro = result.getData();
                Course course = intro.getEv(TIntro.course);
                mSubmit.put(TSubmit.courseId, course.getString(TCourse.id));
                List<Detail> mDetails = course.getList(TCourse.details);
                listResult.setData(mDetails);
            }
            return listResult;
        } else if (id == KIdVideo) {
            return JsonParser.evs(r.getText(), Detail.class);
        } else {
            return JsonParser.error(r.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        IListResult<Detail> r = (IListResult<Detail>) result;
        if (r.isSucceed()) {
            setViewState(ViewState.normal);
            List<Detail> mDetails = r.getData();
            if (mDetails == null || mDetails.size() <= 0) {
                return;
            }
            addAll(mDetails);

            mStudyTime = 0;
            for (Detail detail : mDetails) {
                mStudyTime += detail.getLong(TDetail.userdtime);
                if (mBarTvRight.getVisibility() == View.GONE && detail.getBoolean(TDetail.type)) {
                    // 有文件的时候
                    showView(mBarTvRight);
                }
            }
            if (mStudyTime > 0) {
                mBarTvRight.setText(getString(R.string.video_add_up_all) + VideoCategoryAdapter.format(mStudyTime));
            }
        } else {
            onNetworkError(id, new NetError(id, r.getError()));
        }
    }

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);

        setViewState(ViewState.error);
    }

    @Override
    public void onAdapterClick(int position, View v) {
        if (getItem(position).getBoolean(TDetail.type)) {
            // 1是文件
            mSubmit.put(TSubmit.detailId, getItem(position).getString(TDetail.id));
            VideoActivity.nav(VideoCategoryActivity.this, getItem(position), mSubmit);
            mClickPosition = position;
        } else {
            // 0是文件夹
            VideoCategoryActivity.nav(VideoCategoryActivity.this, mSubmit,
                    getItem(position).getString(TDetail.id));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            long duration = data.getLongExtra(Extra.KData, 0);
            mStudyTime += duration;
            if (mStudyTime > 0) {
                mBarTvRight.setText(getString(R.string.video_add_up_all) + VideoCategoryAdapter.format(mStudyTime));
            }
            YSLog.d(TAG,"onActivityResult:"+ duration);
            getItem(mClickPosition).put(TDetail.userdtime, duration + getItem(mClickPosition).getLong(TDetail.userdtime));
            invalidate();
        }
    }
}
