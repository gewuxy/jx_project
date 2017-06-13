package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import lib.ys.LogMgr;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.view.ViewUtil;
import lib.yy.Notifier.NotifyType;
import lib.yy.activity.base.BaseListActivity;
import lib.yy.network.Result;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.RecordAdapter;
import yy.doctor.model.meet.Course;
import yy.doctor.model.meet.Course.TCourse;
import yy.doctor.model.meet.CourseInfo;
import yy.doctor.model.meet.CourseInfo.TCourseInfo;
import yy.doctor.model.meet.PPT;
import yy.doctor.model.meet.PPT.TPPT;
import yy.doctor.network.NetFactory;
import yy.doctor.util.CacheUtil;

/**
 * 会议记录界面
 *
 * 日期 : 2017/4/26
 * 创建人 : guoxuan
 */

public class MeetingRecordActivity extends BaseListActivity<Course, RecordAdapter> implements OnAdapterClickListener, OnCompletionListener {
    // TODO: 2017/6/6 未完
    private int mCurrId; // 当前选的
    private String mMeetId;
    private MediaPlayer mPlayer;
    private PPT mPPT;
    private CourseInfo mCourseInfo;
    private List<Course> mCourses;
    private String mPath;

    private boolean mIsPlaying;
    private AnimationDrawable mLastAnimation;

    public static void nav(Context context, PPT ppt, int current) {
        Intent i = new Intent(context, MeetingRecordActivity.class)
                .putExtra(Extra.KData, ppt)
                .putExtra(Extra.KId, current);
        LaunchUtil.startActivityForResult(context, i, 0);
    }

    @Override
    public void initData() {
        mIsPlaying = false;
        mPPT = (PPT) getIntent().getSerializableExtra(Extra.KData);
        mCurrId = getIntent().getIntExtra(Extra.KId, 0);
        mMeetId = mPPT.getString(TPPT.meetId);
        mCourseInfo = mPPT.getEv(TPPT.course);
        mCourses = mCourseInfo.getList(TCourseInfo.details);
        addAll(mCourses);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_record;
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addViewLeft(R.mipmap.nav_bar_ic_back, mCourseInfo.getString(TCourseInfo.title), v -> {
            // 把前面的页面关了
            notify(NotifyType.finish);
            setResult(RESULT_OK, null);
            finish();
        });

        bar.addViewRight(R.mipmap.nav_bar_ic_comment, v -> MeetingCommentActivity.nav(MeetingRecordActivity.this, mMeetId));
        bar.addViewRight(R.mipmap.nav_bar_ic_course, v -> {
            setResult(RESULT_OK, null);
            finish();
        });
    }

    @Override
    public View createHeaderView() {
        return ViewUtil.inflateSpaceViewDp(18);
    }

    @Override
    public void setViews() {
        super.setViews();
        setOnAdapterClickListener(this);
        setSelection(mCurrId);
    }

    @Override
    public void onAdapterClick(int position, View v) {
        if (v instanceof NetworkImageView) {
            // 点击整个图片
            Intent intent = new Intent()
                    .putExtra(Extra.KId, position);
            setResult(RESULT_OK, intent);
            finish();

        } else if (v instanceof ImageView) {
            // 点击右下角的音频图片

            if (mPlayer == null) {
                mPlayer = new MediaPlayer();
                mPlayer.setOnCompletionListener(this);
            }

            // 改变状态
            boolean play = getItem(position).getBoolean(TCourse.play);
            getItem(position).put(TCourse.play, !play);
            mIsPlaying = !mIsPlaying;

            // 停止上面的动画,点击的动画对应播放/ 停止
            if (mLastAnimation != null) {
                mLastAnimation.stop();
            }
            AnimationDrawable animation = (AnimationDrawable) getAdapter().getCacheVH(position).getIvPicAudio().getDrawable();
            if (!play) {
                animation.start();
            } else {
                animation.stop();
            }
            mLastAnimation = animation;

            if (mIsPlaying) {
                // 播放音乐
                String audioUrl = mCourses.get(position).getString(TCourse.audioUrl); // 路径
                String filePath = CacheUtil.getMeetingCacheDir(mMeetId); // 文件夹名字
                String type = audioUrl.substring(audioUrl.lastIndexOf(".") + 1); // 文件类型
                String fileName = audioUrl.hashCode() + "." + type; // 文件名
                mPath = filePath + fileName;

                File file = CacheUtil.getMeetingCacheFile(mMeetId, fileName);
                if (!file.exists()) {
                    // 不存在下载
                    exeNetworkReq(NetFactory.newDownload(audioUrl, filePath, fileName).build());
                } else {
                    // 存在播放
                    mPlayer.reset();
                    try {
                        mPlayer.setDataSource(mPath);
                        mPlayer.prepare();
                    } catch (IOException e) {
                        LogMgr.e(TAG, "onAdapterClick", e);
                    }
                    mPlayer.start();
                }
            } else {
                mPlayer.pause();
            }

        }

    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        // mPath 不会为空(只有下载请求网络了)
        Result r = (Result) result;
        if (r.isSucceed()) {
            mPlayer.reset();
            try {
                mPlayer.setDataSource(mPath);
                mPlayer.prepare();
            } catch (IOException e) {
                LogMgr.e(TAG, "onAdapterClick", e);
            }
            mPlayer.start();
        } else {
            showToast(r.getError());
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPlayer != null) {
            mPlayer.release();
        }
    }
}
