package yy.doctor.ui.activity.meeting;

import android.content.Intent;
import android.view.View;

import java.util.List;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.ys.ConstantsEx;
import lib.ys.adapter.MultiAdapterEx.OnAdapterClickListener;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.view.ViewUtil;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseListActivity;
import yy.doctor.App.NavBarVal;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.RecordAdapter;
import yy.doctor.model.meet.Course;
import yy.doctor.model.meet.Course.CourseType;
import yy.doctor.model.meet.Course.TCourse;
import yy.doctor.model.meet.CourseInfo.TCourseInfo;
import yy.doctor.model.meet.PPT;
import yy.doctor.model.meet.PPT.TPPT;
import yy.doctor.util.NetPlayer;
import yy.doctor.util.NetPlayer.OnPlayerListener;
import yy.doctor.util.Util;

/**
 * 会议记录界面
 * <p>
 * 日期 : 2017/4/26
 * 创建人 : guoxuan
 */
@Route
public class MeetingRecordActivity extends BaseListActivity<Course, RecordAdapter> implements OnAdapterClickListener, OnPlayerListener {

    @Arg
    int mCurrId; // 当前选的

    @Arg
    PPT mPPT;

    private List<Course> mCourses;

    private int mLastPosition;

    private boolean mNeedPlay; // 是否需要播放

    @Override
    public void initData() {
        mCourses = mPPT.getEv(TPPT.course).getList(TCourseInfo.details);
        mLastPosition = ConstantsEx.KInvalidValue;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_meeting_record;
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addViewLeft(R.drawable.nav_bar_ic_back, v -> {
            // 把前面的页面关了
            notify(NotifyType.meeting_finish);
            finish();
        });

        String title = mPPT.getEv(TPPT.course).getString(TCourseInfo.title); // 原始的标题
        String fitTitle = TextUtil.cutString(title, fitDp(NavBarVal.KLeftTextSizeDp), fitDp(200), "..."); // 修正后标题
        bar.addTextViewMid(fitTitle);

        bar.addViewRight(R.drawable.nav_bar_ic_comment, v ->
                MeetingCommentActivityRouter.create(mPPT.getString(TPPT.meetId)).route(this));
        bar.addViewRight(R.drawable.nav_bar_ic_course, v -> finish());
    }

    @Override
    public View createHeaderView() {
        return ViewUtil.inflateSpaceViewDp(18);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnAdapterClickListener(this);
        NetPlayer.inst().setListener(this);

        addAll(mCourses);
        setSelection(mCurrId);
    }

    @Override
    public void onAdapterClick(int position, View v) {
        mNeedPlay = true;
        int itemType = getAdapter().getItemViewType(position);
        switch (itemType) {
            case CourseType.video:
            case CourseType.pic: {
                pic(position);
            }
            break;
            case CourseType.audio: {
                audio(position);
            }
            break;
            case CourseType.pic_audio: {
                if (v instanceof NetworkImageView) {
                    pic(position);
                } else {
                    audio(position);
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
    private void audio(int position) {
        if (mLastPosition != position) {
            // 不同的条目

            // 播放音乐
            String audioUrl = Util.convertUrl(mCourses.get(position).getString(TCourse.audioUrl));
            NetPlayer.inst().prepare(mPPT.getString(TPPT.meetId), audioUrl);

            // 停止之前的动画启动当前动画
            if (mLastPosition != ConstantsEx.KInvalidValue) {
                getItem(mLastPosition).put(TCourse.play, false);
                invalidate(mLastPosition);
            }
            getItem(position).put(TCourse.play, true);
            invalidate(position);
        } else {
            // 相同的条目

            // 当前状态取反
            boolean state = !getItem(position).getBoolean(TCourse.play);
            getItem(position).put(TCourse.play, state);
            if (state) {
                NetPlayer.inst().play();
            } else {
                NetPlayer.inst().pause();
            }
            invalidate(position);
        }

        mLastPosition = position;
    }

    @Override
    public void onDownProgress(int progress) {
        // do nothing
    }

    @Override
    public void onPreparedSuccess(long allMilliseconds) {
        if (mNeedPlay) {
            NetPlayer.inst().play();
        }
    }

    @Override
    public void onPreparedError() {
        // do nothing
    }

    @Override
    public void onProgress(long currMilliseconds, int progress) {
        // do nothing
    }

    @Override
    public void onPlayState(boolean state) {
        // do nothing
    }

    @Override
    public void onCompletion() {
        getItem(mLastPosition).put(TCourse.play, false);
        invalidate(mLastPosition);
        mNeedPlay = false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 停止之前的动画
        if (mLastPosition != ConstantsEx.KInvalidValue) {
            getItem(mLastPosition).put(TCourse.play, false);
            invalidate(mLastPosition);
        }
        NetPlayer.inst().pause();
        NetPlayer.inst().removeListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        NetPlayer.inst().setListener(this);
    }
}
