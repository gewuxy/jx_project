package jx.doctor.ui.activity.meeting.play.presenter;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.CallSuper;

import com.pili.pldroid.player.widget.PLVideoTextureView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import jx.doctor.model.meet.ppt.Course;
import jx.doctor.model.meet.ppt.CourseInfo;
import jx.doctor.model.meet.ppt.PPT;
import jx.doctor.network.JsonParser;
import jx.doctor.network.NetworkApiDescriptor;
import jx.doctor.ui.activity.meeting.play.contract.BasePlayContract;
import jx.doctor.util.NetPlayer;
import jx.doctor.util.Time;
import lib.jx.contract.BasePresenterImpl;
import lib.network.model.NetworkError;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.ConstantsEx;
import lib.ys.ui.decor.DecorViewEx;

/**
 * @auther : GuoXuan
 * @since : 2018/3/1
 */
abstract public class BasePlayPresenterImpl<V extends BasePlayContract.View> extends BasePresenterImpl<V> implements
        BasePlayContract.Presenter<V> {

    private final int KWhatPlay = 0;
    private final int KWhatNext = 1;
    private final int KWhatCount = 2;

    private final int KDuration = 3;

    protected Handler mHandler;
    protected PPT mPpt;

    private String mMeetId;
    private String mModuleId;

    private int mPosition; // 上一个position

    protected long mMediaTime;

    protected boolean mPlay; // playState

    public List<Course> getCourses() {
        if (mPpt == null) {
            return null;
        }
        CourseInfo courseInfo = mPpt.get(PPT.TPPT.course);
        if (courseInfo == null) {
            return null;
        }
        return courseInfo.getList(CourseInfo.TCourseInfo.details);
    }

    public BasePlayPresenterImpl(V view) {
        super(view);

        mPosition = ConstantsEx.KInvalidValue;
        NetPlayer.inst().setListener(new NetPlayerListener());
        mHandler = new Handler(new HandlerImpl());
    }

    @Override
    public void setData(String meetId, String moduleId) {
        mMeetId = meetId;
        mModuleId = moduleId;
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetworkApiDescriptor.MeetAPI.toCourse(mMeetId, mModuleId).build());
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
            getView().onNetworkSuccess(mPpt);
        } else {
            onNetworkError(id, r.getError());
        }
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        getView().setViewState(DecorViewEx.ViewState.error);
    }

    @Override
    public void startCount() {
        mHandler.removeMessages(KWhatCount);
        mHandler.sendEmptyMessageDelayed(KWhatCount, TimeUnit.SECONDS.toMillis(KDuration));
    }

    @Override
    public void stopCount() {
        mHandler.removeMessages(KWhatCount);
    }

    @Override
    public void toggle(int index) {
        mPlay = !mPlay;
        getView().onPlayState(mPlay);
    }

    @Override
    public void playMedia(int position) {
        mHandler.removeMessages(KWhatPlay);
        mHandler.removeMessages(KWhatNext);
        mPlay = true;
        Message message = Message.obtain();
        message.obj = position;
        message.what = KWhatPlay;
        mHandler.sendMessageDelayed(message, 300);
    }

    @Override
    public void stopMedia() {
        mHandler.removeMessages(KWhatPlay);
        mHandler.removeMessages(KWhatNext);
        mPlay = false;
        NetPlayer.inst().stop();
    }

    @Override
    public void imgNext() {
        mHandler.removeMessages(KWhatNext);
        mHandler.sendEmptyMessageDelayed(KWhatNext, TimeUnit.SECONDS.toMillis(KDuration));
    }

    @Override
    public void setLiveMedia(PLVideoTextureView textureView) {
        NetPlayer.inst().setVideo(textureView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mHandler.removeCallbacksAndMessages(null);
        NetPlayer.inst().recycle();
    }

    protected void playProgress(String time, int progress) {
        // do nothing
    }

    private void nativePlay(int position) {
        List<Course> courses = getCourses();
        if (courses == null || position >= courses.size() || position < 0) {
            return;
        }

        mPosition = position;
        NetPlayer.inst().stop();
        Course course = courses.get(position);
        switch (course.getType()) {
            case Course.CourseType.pic_audio:
            case Course.CourseType.audio: {
                String url = course.getString(Course.TCourse.audioUrl);
                NetPlayer.inst().setAudio();
                NetPlayer.inst().prepare(mPpt.getString(PPT.TPPT.meetId), url);
            }
            break;
            case Course.CourseType.pic: {
                imgNext();
            }
            break;
            case Course.CourseType.video: {
                String url = course.getString(Course.TCourse.videoUrl);
                getView().setLiveVideo();
                NetPlayer.inst().prepare(mPpt.getString(PPT.TPPT.meetId), url);
            }
            break;
        }
    }

    private class HandlerImpl implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
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
                case KWhatCount: {
                    getView().countFinish();
                }
                break;
            }
            return true;
        }
    }

    private class NetPlayerListener implements NetPlayer.OnPlayerListener {

        @Override
        public void onDownProgress(int progress) {
            // do nothing
        }

        @CallSuper
        @Override
        public void onPreparedSuccess(long allMillisecond) {
            mMediaTime = allMillisecond;
        }

        @Override
        public void onPreparedError() {
            // do nothing
        }

        @CallSuper
        @Override
        public void onProgress(long currMilliseconds, int progress) {
            if (mPosition != ConstantsEx.KInvalidValue) {
                String time = Time.getTime(mMediaTime - currMilliseconds);
                playProgress(time, progress);
            }
        }

        @Override
        public void onPlayState(boolean state) {
            // do nothing
        }

        @CallSuper
        @Override
        public void onCompletion() {
            getView().setNextItem();
        }
    }
}
