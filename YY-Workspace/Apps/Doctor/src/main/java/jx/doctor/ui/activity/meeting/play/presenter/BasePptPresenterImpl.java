package jx.doctor.ui.activity.meeting.play.presenter;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.CallSuper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.ui.decor.DecorViewEx.ViewState;
import lib.yy.contract.BasePresenterImpl;
import lib.yy.util.CountDown;
import jx.doctor.model.meet.ppt.Course;
import jx.doctor.model.meet.ppt.Course.CourseType;
import jx.doctor.model.meet.ppt.Course.TCourse;
import jx.doctor.model.meet.ppt.CourseInfo;
import jx.doctor.model.meet.ppt.CourseInfo.TCourseInfo;
import jx.doctor.model.meet.ppt.PPT;
import jx.doctor.model.meet.ppt.PPT.TPPT;
import jx.doctor.network.JsonParser;
import jx.doctor.ui.activity.meeting.play.contract.BasePptContract;
import jx.doctor.util.NetPlayer;
import jx.doctor.util.Time;

/**
 * @auther : GuoXuan
 * @since : 2017/9/26
 */

public class BasePptPresenterImpl<V extends BasePptContract.View> extends BasePresenterImpl<V> implements
        BasePptContract.Presenter<V>,
        NetPlayer.OnPlayerListener,
        CountDown.OnCountDownListener {

    private final int KDuration = 3;

    private final int KWhatPlay = 0;
    private final int KWhatNext = 1;

    private int mPosition; // 上一个position
    protected PPT mPpt;
    protected List<Course> mCourses;
    private CountDown mCountDown;

    private long mAllMillisecond;
    private Handler mHandler;

    public BasePptPresenterImpl(V view) {
        super(view);

        mCountDown = new CountDown();
        mCountDown.setListener(this);
        NetPlayer.inst().setListener(this);
        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                int what = msg.what;
                switch (what) {
                    case KWhatPlay: {
                        int position = (int) msg.obj;
                        nativePlay(position);
                    }
                    break;
                    case KWhatNext: {
                        getView().setNextItem();
                    }
                    break;
                }
            }

        };
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
        removeMessages();
        Message message = Message.obtain();
        message.obj = position;
        message.what = KWhatPlay;
        mHandler.sendMessageDelayed(message, 300);
    }

    private void nativePlay(int position) {
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
                imgNext();
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

    protected void removeMessages() {
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void stopMedia() {
        removeMessages();
        NetPlayer.inst().stop();
        if (mCourses != null) {
            mCourses.get(mPosition).put(TCourse.play, false);
            getView().invalidate(mPosition);
        }
    }

    @Override
    public void toggle(int index) {
        Course course = mCourses.get(index);
        if (NetPlayer.inst().isPlaying()) {
            course.put(TCourse.play, false);
        } else {
            course.put(TCourse.play, true);
        }
        getView().invalidate(index);
    }

    @Override
    public void starCount() {
        mCountDown.start(KDuration);
    }

    @Override
    public void imgNext() {
        removeMessages();
        mHandler.sendEmptyMessageDelayed(KWhatNext, TimeUnit.SECONDS.toMillis(3));
    }

    @Override
    public void onDownProgress(int progress) {
        // do nothing
    }

    @CallSuper
    @Override
    public void onPreparedSuccess(long allMillisecond) {
        mAllMillisecond = allMillisecond;
        Course course = mCourses.get(mPosition);
        course.put(TCourse.play, true); // 播放
        course.put(TCourse.time, Time.getTime(mAllMillisecond));
        getView().invalidate(mPosition);
    }

    @Override
    public void onPreparedError() {
        // do nothing
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
        // do nothing
    }

    @CallSuper
    @Override
    public void onCompletion() {
        mCourses.get(mPosition).put(TCourse.play, false);
        getView().invalidate(mPosition);
        getView().setNextItem();
    }

    @Override
    public void onCountDown(long remainCount) {
        if (remainCount == 0) {
            getView().finishCount();
        }
    }

    @Override
    public void onCountDownErr() {
        // do nothing
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        removeMessages();
        mCountDown.recycle();
        NetPlayer.inst().recycle();
    }
}
