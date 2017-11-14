package yy.doctor.ui.activity.meeting.play.presenter;

import android.support.annotation.CallSuper;

import java.util.List;

import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.ys.util.TextUtil;
import lib.yy.contract.BasePresenterImpl;
import lib.yy.util.CountDown;
import yy.doctor.model.meet.ppt.Course;
import yy.doctor.model.meet.ppt.Course.CourseType;
import yy.doctor.model.meet.ppt.Course.TCourse;
import yy.doctor.model.meet.ppt.CourseInfo;
import yy.doctor.model.meet.ppt.CourseInfo.TCourseInfo;
import yy.doctor.model.meet.ppt.PPT;
import yy.doctor.model.meet.ppt.PPT.TPPT;
import yy.doctor.network.JsonParser;
import yy.doctor.ui.activity.meeting.play.contract.MeetingPptContract;
import yy.doctor.util.NetPlayer;
import yy.doctor.util.Time;

/**
 * @auther : GuoXuan
 * @since : 2017/9/26
 */

public class MeetingPptPresenterImpl extends BasePresenterImpl<MeetingPptContract.View> implements
        MeetingPptContract.Presenter,
        NetPlayer.OnPlayerListener,
        CountDown.OnCountDownListener {

    private final int KDuration = 3;

    private int mPosition; // 上一个position
    protected PPT mPpt;
    protected List<Course> mCourses;
    private CountDown mCountDown;

    private long mAllMillisecond;

    public MeetingPptPresenterImpl(MeetingPptContract.View view) {
        super(view);
        mCountDown = new CountDown();
        mCountDown.setListener(this);
        NetPlayer.inst().setListener(this);
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), PPT.class);
    }

    @CallSuper
    @Override
    public void onNetworkSuccess(int id, IResult r) {
        getView().onStopRefresh();
        if (r.isSucceed()) {
            mPpt = (PPT) r.getData();
            if (mPpt == null) {
                return;
            }
            CourseInfo courseInfo = mPpt.get(TPPT.course);
            if (courseInfo == null) {
                return;
            }

            mCourses = courseInfo.getList(TCourseInfo.details);
            if (mCourses == null || mCourses.size() == 0) {
                return;
            }
            getView().portraitInit(mPpt, mCourses);
        } else {
            onNetworkError(id, r.getError());
            getView().showToast(r.getMessage());
        }
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        getView().onStopRefresh();
        getView().setViewState(ViewState.error);
    }

    @Override
    public void playMedia(int position) {
        Course c = mCourses.get(mPosition);
        c.put(TCourse.time, "音频"); // 清空时间
        c.put(TCourse.select, false); // 上一个取消选择
        c.put(TCourse.play, false); // 上一个取消播放
        getView().invalidate(mPosition);

        mPosition = position;
        NetPlayer.inst().stop();
        Course course = mCourses.get(position);
        switch (course.getType()) {
            case CourseType.pic_audio:
            case CourseType.audio: {
                String url = course.getString(TCourse.audioUrl);
                NetPlayer.inst().setAudio();
                NetPlayer.inst().prepare(mPpt.getString(TPPT.meetId), url);
            }
            break;
            case CourseType.pic: {
            }
            break;
            case CourseType.video: {
                String url = course.getString(TCourse.videoUrl);
                NetPlayer.inst().setVideo(getView().getTextureView());
                NetPlayer.inst().prepare(mPpt.getString(TPPT.meetId), url);
            }
            break;
        }

        course.put(TCourse.select, true); // 选中
        getView().invalidate(mPosition);
    }

    @Override
    public void stopMedia() {
        NetPlayer.inst().pause();
        if (mCourses != null) {
            mCourses.get(mPosition).put(TCourse.play, false);
            getView().invalidate(mPosition);
        }
    }

    @Override
    public void toggle() {
        Course course = mCourses.get(mPosition);
        String url = course.getString(TCourse.videoUrl);
        if (TextUtil.isEmpty(url)) {
            url = course.getString(TCourse.audioUrl);
        }
        if (NetPlayer.inst().isPlaying()) {
            mCourses.get(mPosition).put(TCourse.play, false);
        } else {
            mCourses.get(mPosition).put(TCourse.play, true);
        }
        pptToggle(url);
        getView().invalidate(mPosition);
    }

    @Override
    public void starCount() {
         mCountDown.start(KDuration);
    }

    @Override
    public void onDownProgress(int progress) {
        // 下载中
    }

    @CallSuper
    @Override
    public void onPreparedSuccess(long allMillisecond) {
        mAllMillisecond = allMillisecond;
        getView().onPlayState(true);
        Course course = mCourses.get(mPosition);
        course.put(TCourse.play, true); // 播放
        course.put(TCourse.time, Time.getTime(mAllMillisecond));
        getView().invalidate(mPosition);
    }

    @Override
    public void onPreparedError() {

    }

    @CallSuper
    @Override
    public void onProgress(long currMilliseconds, int progress) {
        Course course = mCourses.get(mPosition);
        course.put(TCourse.time, Time.getTime(mAllMillisecond - currMilliseconds));
        getView().invalidate(mPosition);
    }

    @Override
    public void onPlayState(boolean state) {
        getView().onPlayState(state);
    }

    @CallSuper
    @Override
    public void onCompletion() {
        mCourses.get(mPosition).put(TCourse.play, false);
        getView().invalidate(mPosition);
        getView().setNextItem();
        pptCompletion();
    }

    protected void pptToggle(String url) {
        // 区别于ppt直播
        NetPlayer.inst().toggle(url);
    }

    protected void pptCompletion() {
        getView().onPlayState(false);
    }

    @Override
    public void onCountDown(long remainCount) {
        if (remainCount == 0) {
            getView().finishCount();
        }
    }

    @Override
    public void onCountDownErr() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mCountDown.recycle();
        NetPlayer.inst().recycle();
    }
}
