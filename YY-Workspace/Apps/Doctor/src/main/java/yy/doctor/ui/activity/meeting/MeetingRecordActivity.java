package yy.doctor.ui.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.util.List;

import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.view.ViewUtil;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseListActivity;
import yy.doctor.App;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.RecordAdapter;
import yy.doctor.model.meet.Course;
import yy.doctor.model.meet.Course.CourseType;
import yy.doctor.model.meet.Course.TCourse;
import yy.doctor.model.meet.CourseInfo;
import yy.doctor.model.meet.CourseInfo.TCourseInfo;
import yy.doctor.model.meet.PPT;
import yy.doctor.model.meet.PPT.TPPT;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.CacheUtil;

/**
 * 会议记录界面
 * <p>
 * 日期 : 2017/4/26
 * 创建人 : guoxuan
 */
public class MeetingRecordActivity extends BaseListActivity<Course, RecordAdapter> implements OnAdapterClickListener, OnCompletionListener {

    private int mCurrId; // 当前选的
    private String mMeetId;
    private MediaPlayer mPlayer;
    private PPT mPPT;
    private CourseInfo mCourseInfo;
    private List<Course> mCourses;
    private String mPath;

    private boolean mIsPlaying;
    private AnimationDrawable mLastAnimation;
    private int mLastPosition;

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
        bar.addViewLeft(R.mipmap.nav_bar_ic_back, v -> {
            // 把前面的页面关了
            notify(NotifyType.meeting_finish);
            setResult(RESULT_OK);
            finish();
        });

        String title = TextUtil.cutString(mCourseInfo.getString(TCourseInfo.title), fitDp(App.KTitleBarTextSizeDp), fitDp(200), "...");
        bar.addTextViewMid(title);

        bar.addViewRight(R.mipmap.nav_bar_ic_comment, v -> MeetingCommentActivity.nav(MeetingRecordActivity.this, mMeetId));
        bar.addViewRight(R.mipmap.nav_bar_ic_course, v -> {
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

        int itemType = getAdapter().getItemViewType(position);
        switch (itemType) {
            case CourseType.video:
            case CourseType.pic: {
                pic(position);
            }
            break;

            case CourseType.audio: {
                audio(position, (AnimationDrawable) getAdapter()
                        .getCacheVH(position).getIvAudio().getDrawable());
            }
            break;

            case CourseType.pic_audio: {
                if (v instanceof NetworkImageView) {
                    pic(position);
                } else {
                    audio(position, (AnimationDrawable) getAdapter()
                            .getCacheVH(position).getIvPicAudio().getDrawable());
                }
            }

            break;
        }
    }

    /**
     * 点击图片返回
     */
    private void pic(int position) {
        Intent intent = new Intent()
                .putExtra(Extra.KId, position);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 点击播放音乐
     */
    private void audio(int position, AnimationDrawable animation) {
        YSLog.d(TAG, "onAdapterClick:" + position);
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            mPlayer.setOnCompletionListener(this);
        }

        // 停止之前的动画
        if (mLastAnimation != null && mLastPosition != position) {
            mLastAnimation.stop();
            getItem(mLastPosition).put(TCourse.play, false);
        }
        // 改变状态
        boolean play = getItem(position).getBoolean(TCourse.play);
        // 防止同一个没有改变状态
        getItem(position).put(TCourse.play, !play);
        if (play) {
            animation.stop();
        } else {
            animation.start();
        }

        //  没有播放 / 点击不同的条目
        if (!mIsPlaying || mLastPosition != position) {
            // 播放音乐
            String audioUrl = mCourses.get(position).getString(TCourse.audioUrl); // 路径
            String filePath = CacheUtil.getMeetingCacheDir(mMeetId); // 文件夹名字
            String type = audioUrl.substring(audioUrl.lastIndexOf(".") + 1); // 文件类型
            String fileName = audioUrl.hashCode() + "." + type; // 文件名
            mPath = filePath + fileName;

            File file = CacheUtil.getMeetingCacheFile(mMeetId, fileName);
            if (!file.exists()) {
                // 不存在下载
                exeNetworkReq(position, NetFactory.newDownload(audioUrl, filePath, fileName).build());
            } else {
                // 存在播放
                mPlayer.reset();
                try {
                    mPlayer.setDataSource(mPath);
                    mPlayer.prepare();
                } catch (IOException e) {
                    YSLog.e(TAG, "onAdapterClick", e);
                }
                mPlayer.start();
            }
        } else {
            mPlayer.pause();
        }

        mIsPlaying = !mIsPlaying;
        mLastAnimation = animation;
        mLastPosition = position;
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        // mPath 不会为空(只有下载请求网络了)
        if (isFinishing()) {
            return;
        }
        if (id == mLastPosition) {
            // 没有重新点的时候才播放
            mPlayer.reset();
            try {
                mPlayer.setDataSource(mPath);
                mPlayer.prepare();
            } catch (IOException e) {
                YSLog.e(TAG, "onAdapterClick", e);
            }
            mPlayer.start();
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

        if (mLastAnimation != null && mLastAnimation.isRunning()) {
            mLastAnimation.stop();
        }
    }
}
