package yy.doctor.ui.activity.meeting.play;

import java.util.List;

import lib.network.model.NetworkResp;
import lib.ys.util.TextUtil;
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

public class MeetingRepPresenterImpl extends BasePresenter implements
        MeetingRepContract.Presenter,
        NetPlayer.OnPlayerListener,
        CountDown.OnCountDownListener {

    private MeetingRepContract.View mView;

    private final int KDuration = 3;

    private PPT mPpt;
    private List<Course> mCourses;
    private int mPosition;
    private final CountDown mCountDown;

    public MeetingRepPresenterImpl(MeetingRepContract.View view) {
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
            CourseInfo courseInfo = mPpt.getEv(TPPT.course);
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

        course.put(TCourse.select, true);
        course.put(TCourse.play, true);
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

    @Override
    public void onDownProgress(int progress) {

    }

    @Override
    public void onPreparedSuccess(long allMillisecond) {
        mView.onPlayState(true);
    }

    @Override
    public void onPreparedError() {

    }

    @Override
    public void onProgress(long currMilliseconds, int progress) {

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
