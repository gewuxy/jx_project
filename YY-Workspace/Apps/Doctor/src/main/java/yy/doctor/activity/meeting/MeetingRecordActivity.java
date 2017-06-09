package yy.doctor.activity.meeting;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.view.ViewUtil;
import lib.yy.Notifier.NotifyType;
import lib.yy.activity.base.BaseListActivity;
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
 * <p>
 * 日期 : 2017/4/26
 * 创建人 : guoxuan
 */

public class MeetingRecordActivity extends BaseListActivity<Course, RecordAdapter> implements OnAdapterClickListener, OnCompletionListener {
    // TODO: 2017/6/6 未完
    private int mCurrent; // 当前选的
    private String mMeetId;
    private MediaPlayer mPlayer;
    private PPT mPPT;
    private CourseInfo mCourseInfo;
    private List<Course> mCourses;

    public static void nav(Context context, PPT ppt, int current) {
        Intent i = new Intent(context, MeetingRecordActivity.class)
                .putExtra(Extra.KData, ppt)
                .putExtra(Extra.KId, current);
        LaunchUtil.startActivityForResult(context, i, 0);
    }

    @Override
    public void initData() {
        mPPT = (PPT) getIntent().getSerializableExtra(Extra.KData);
        mCurrent = getIntent().getIntExtra(Extra.KId, 0);
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
            finish();
        });

        bar.addViewRight(R.mipmap.nav_bar_ic_comment, v -> MeetingCommentActivity.nav(MeetingRecordActivity.this, mMeetId));
        bar.addViewRight(R.mipmap.nav_bar_ic_course, v -> finish());
    }

    @Override
    public View createHeaderView() {
        return ViewUtil.inflateSpaceViewDp(18);
    }

    @Override
    public void setViews() {
        super.setViews();
        setOnAdapterClickListener(this);
        getLv().setSelection(mCurrent);
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

            // 播放音乐
            if (mPlayer == null) {
                mPlayer = new MediaPlayer();
                mPlayer.setOnCompletionListener(this);
            }

            String audioUrl = mCourses.get(position).getString(TCourse.audioUrl); // 路径
            String filePath = CacheUtil.getMeetingCacheDir(mMeetId); // 文件夹名字
            String type = audioUrl.substring(audioUrl.lastIndexOf(".") + 1); // 文件类型
            String fileName = audioUrl.hashCode() + "." + type; // 文件名

            // 优化存到MapList中
            File file = CacheUtil.getMeetingCacheFile(mMeetId, fileName);
            if (!file.exists()) {
                // 不存在下载
                exeNetworkReq(NetFactory.newDownload(audioUrl, filePath, fileName).build());
            } else {
                // 存在播放
                mPlayer.reset();
                try {
                    mPlayer.setDataSource(filePath + fileName);
                    mPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mPlayer.start();
            }
        }

    }

    @Override
    public void onNetworkSuccess(int id, Object result) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }
}
