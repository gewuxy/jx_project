package yy.doctor.ui.activity.meeting.play;

import java.util.List;

import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.util.TextUtil;
import lib.ys.util.TimeFormatter;
import lib.yy.network.Result;
import lib.yy.util.CountDown;
import yy.doctor.model.meet.Course;
import yy.doctor.model.meet.Course.CourseType;
import yy.doctor.model.meet.Course.TCourse;
import yy.doctor.model.meet.CourseInfo;
import yy.doctor.model.meet.CourseInfo.TCourseInfo;
import yy.doctor.model.meet.PPT;
import yy.doctor.model.meet.PPT.TPPT;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkAPISetter.MeetAPI;
import yy.doctor.util.NetPlayer;

/**
 * @auther : GuoXuan
 * @since : 2017/9/26
 */

public class MeetingRebPresenterImpl extends BasePresenter implements
        MeetingRebContract.Presenter,
        NetPlayer.OnPlayerListener,
        CountDown.OnCountDownListener {

    private final int KDuration = 3;

    private int mPosition; // 上一个position
    private PPT mPpt;
    private List<Course> mCourses;
    private CountDown mCountDown;

    private MeetingRebContract.View mView;
    private long mAllMillisecond;

    public MeetingRebPresenterImpl(MeetingRebContract.View view) {
        mView = view;
        mCountDown = new CountDown();
        mCountDown.setListener(this);
        NetPlayer.inst().setListener(this);
    }

    @Override
    public void getDataFromNet(String meetId, String moduleId) {
        exeNetworkReq(MeetAPI.toCourse(meetId, moduleId).build());
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), PPT.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<PPT> r = (Result<PPT>) result;
        if (r.isSucceed()) {
            mPpt = r.getData();
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
            mView.portraitInit(mPpt, mCourses);
        } else {
            mView.showToast(r.getMessage());
        }
    }

    @Override
    public void landscapeScreen() {
        if (mCourses == null) {
            return;
        }
        mView.landscapeInit(mCourses);
    }

    @Override
    public void playMedia(int position) {
        Course c = mCourses.get(mPosition);
        c.put(TCourse.time, "音频"); // 清空时间
        c.put(TCourse.select, false); // 上一个取消选择
        c.put(TCourse.play, false); // 上一个取消播放
        mView.invalidate(mPosition);

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
                NetPlayer.inst().setVideo(mView.getTextureView());
                NetPlayer.inst().prepare(mPpt.getString(TPPT.meetId), url);
            }
            break;
        }

        course.put(TCourse.select, true); // 选中
        mView.invalidate(mPosition);
    }

    @Override
    public void stopMedia() {
        NetPlayer.inst().stop();
        mCourses.get(mPosition).put(TCourse.play, false);
        mView.invalidate(mPosition);
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
        NetPlayer.inst().toggle(url);
        mView.invalidate(mPosition);
    }

    @Override
    public void starCount() {
        mCountDown.start(KDuration);
    }

    private String getTime(long millisecond) {
        int second = Math.round(millisecond / 1000.0f); // 四舍五入
        YSLog.d(TAG, "getTime:" + second);
        return TimeFormatter.second(second, TimeFormatter.TimeFormat.from_m);
    }

    @Override
    public void onDownProgress(int progress) {

    }

    @Override
    public void onPreparedSuccess(long allMillisecond) {
        mAllMillisecond = allMillisecond;
        mView.onPlayState(true);
        Course course = mCourses.get(mPosition);
        course.put(TCourse.play, true); // 播放
        course.put(TCourse.time, getTime(mAllMillisecond));
        mView.invalidate(mPosition);
    }

    @Override
    public void onPreparedError() {

    }

    @Override
    public void onProgress(long currMilliseconds, int progress) {
        Course course = mCourses.get(mPosition);
        course.put(TCourse.time, getTime(mAllMillisecond - currMilliseconds));
        mView.invalidate(mPosition);
    }

    @Override
    public void onPlayState(boolean state) {
        mView.onPlayState(state);
    }

    @Override
    public void onCompletion() {
        mCourses.get(mPosition).put(TCourse.play, false);
        mView.invalidate(mPosition);
        mView.setNextItem();
    }

    @Override
    public void onCountDown(long remainCount) {
        if (remainCount == 0) {
            mView.finishCount();
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
