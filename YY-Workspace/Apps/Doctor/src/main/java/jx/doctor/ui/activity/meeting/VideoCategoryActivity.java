package jx.doctor.ui.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;

import java.util.List;

import lib.network.model.interfaces.IResult;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseSRListActivity;
import jx.doctor.Extra;
import jx.doctor.R;
import jx.doctor.adapter.meeting.VideoCategoryAdapter;
import jx.doctor.model.meet.Submit;
import jx.doctor.model.meet.Submit.TSubmit;
import jx.doctor.model.meet.video.Course;
import jx.doctor.model.meet.video.Course.TCourse;
import jx.doctor.model.meet.video.Detail;
import jx.doctor.model.meet.video.Detail.TDetail;
import jx.doctor.model.meet.video.Intro;
import jx.doctor.model.meet.video.Intro.TIntro;
import jx.doctor.network.JsonParser;
import jx.doctor.network.NetworkApiDescriptor.MeetAPI;
import jx.doctor.util.Time;

/**
 * 视频列表(分类)界面
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
    private boolean mNeedShow;

    public static void nav(Context context, Submit submit, String preId) {
        Intent i = new Intent(context, VideoCategoryActivity.class)
                .putExtra(Extra.KData, submit)
                .putExtra(Extra.KId, preId);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData(Bundle state) {
        notify(NotifyType.study_start);

        mSubmit = (Submit) getIntent().getSerializableExtra(Extra.KData);
        mPreId = getIntent().getStringExtra(Extra.KId);
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addViewLeft(R.drawable.nav_bar_ic_back, v -> {
            if (TextUtil.isEmpty(mPreId)) {
                notify(NotifyType.study_end);
            }
            finish();
        });
        bar.addTextViewMid(R.string.video);
        mBarTvRight = bar.addTextViewRight(R.string.video_studied_no_start, null);
        goneView(mBarTvRight); // 默认隐藏
        mNeedShow = false;
    }

    @Override
    public void setViews() {
        super.setViews();

        // 后台没有分页返回
        setAutoLoadMoreEnabled(false);
        setOnAdapterClickListener(this);
    }

    @Override
    public void getDataFromNet() {
        if (TextUtil.isEmpty(mPreId)) {
            exeNetworkReq(KIdToVideo, MeetAPI.toVideo(mSubmit.getString(TSubmit.meetId),
                    mSubmit.getString(TSubmit.moduleId)).build());
        } else {
            exeNetworkReq(KIdVideo, MeetAPI.video(mPreId).build());
        }
    }

    @Override
    public IResult<Detail> parseNetworkResponse(int id, String text) throws JSONException {
        // FIXME: 起名
        Result<Detail> Result = null;
        List<Detail> details = null;
        if (id == KIdToVideo) {
            // 筛选需要的数据类型
            Result<Intro> result = JsonParser.ev(text, Intro.class);
            Result = new Result<>();
            if (result.isSucceed()) {
                Intro intro = result.getData();
                Course course = intro.get(TIntro.course);
                mSubmit.put(TSubmit.courseId, course.getString(TCourse.id));
                details = course.getList(TCourse.details);
                Result.setData(details);
            }
        } else if (id == KIdVideo) {
            Result = JsonParser.evs(text, Detail.class);
            details = Result.getList();
        }
        mStudyTime = 0;
        for (Detail detail : details) {
            mStudyTime += detail.getLong(TDetail.userdtime, 0);
            if (isFile(detail.getInt(TDetail.type))) {
                // 有文件的时候就要显示
                mNeedShow = true;
            }
        }
        runOnUIThread(() -> {
            if (mNeedShow) {
                showView(mBarTvRight);
            }
            if (mStudyTime > 0) {
                mBarTvRight.setText(getString(R.string.video_add_up_all) + Time.secondFormat(mStudyTime));
            }
        });
        return Result;
    }

    @Override
    public void onAdapterClick(int position, View v) {
        if (isFile(getItem(position).getInt(TDetail.type))) {
            // 文件
            mSubmit.put(TSubmit.detailId, getItem(position).getString(TDetail.id));
            VideoActivity.nav(VideoCategoryActivity.this, getItem(position), mSubmit);
            mClickPosition = position;
        } else {
            // 文件夹
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
                mBarTvRight.setText(getString(R.string.video_add_up_all) + Time.secondFormat(mStudyTime));
            }

            Detail item = getItem(mClickPosition);
            long useredTime = getItem(mClickPosition).getLong(TDetail.userdtime);
            item.put(TDetail.userdtime, duration + useredTime);
            invalidate();
        }
    }

    /**
     * 0 是文件夹
     * 1 是文件
     *
     * @param type
     * @return false 文件夹,true 文件
     */
    private boolean isFile(int type) {
        if (type == 0) {
            return false;
        }
        return true;
    }

    @Override
    protected String getEmptyText() {
        return "暂时没有相关" + getString(R.string.video);
    }
}
