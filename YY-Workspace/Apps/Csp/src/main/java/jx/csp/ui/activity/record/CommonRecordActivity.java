package jx.csp.ui.activity.record;

import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import jx.csp.R;
import jx.csp.dialog.HintDialog;
import jx.csp.model.meeting.Course.PlayType;
import jx.csp.model.meeting.Course.TCourse;
import jx.csp.model.meeting.CourseDetail;
import jx.csp.model.meeting.CourseDetail.TCourseDetail;
import jx.csp.model.meeting.JoinMeeting;
import jx.csp.model.meeting.JoinMeeting.TJoinMeeting;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.sp.SpUser;
import jx.csp.ui.activity.record.CommonRecordContract.CommonRecordView;
import jx.csp.ui.activity.record.RecordImgFrag.AudioType;
import jx.csp.util.CacheUtil;
import jx.csp.view.GestureView.onGestureViewListener;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.util.FileUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.yy.network.Result;

/**
 * 普通的录制
 *
 * @author CaiXiang
 * @since 2017/10/10
 */

public class CommonRecordActivity extends BaseRecordActivity implements CommonRecordView, onGestureViewListener {

    private boolean mRecordState = false; // 是否在录制中
    private boolean mShowVoiceLine = false; // 声波曲线是否显示
    private boolean mShowSkipPageDialog = false; // 跳转的dialog是否在显示
    private AnimationDrawable mAnimationRecord;
    private CommonRecordPresenterImpl mRecordPresenter;

    @IntDef({
            ScrollType.last,
            ScrollType.next,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface ScrollType {
        int last = 1;  // 上一页
        int next = 2;  // 下一页
    }

    @Override
    public void initData() {
        mCourseId = "14379";
        super.initData();

        mRecordPresenter = new CommonRecordPresenterImpl(this);
    }

    @Override
    public void setViews() {
        super.setViews();

        mTvNavBar.setText("00'00''");
        // 检查录音权限
        if (checkPermission(KMicroPermissionCode, Permission.micro_phone)) {
            havePermissionState();
        } else {
            noPermissionState();
        }
        goneView(mLayoutOnline);
        goneView(mIvVoiceState);
        mTvRecordState.setText("");
        mIvVoiceState.setImageResource(R.drawable.animation_record_record);
        mAnimationRecord = (AnimationDrawable) mIvVoiceState.getDrawable();
        setOnClickListener(R.id.record_iv_state);
        mGestureView.setGestureViewListener(this);

        //请求网络
        exeNetworkReq(KJoinMeetingReqId, MeetingAPI.join(mCourseId).build());
    }

    @Override
    public void onClick(int id) {
        if (id == R.id.record_iv_state) {
            if (mRecordState) {
                mRecordPresenter.stopRecord();
                changeRecordState(false);
            } else {
                // 在播放的时候点击录制，要先停止播放
                if (mShowVoiceLine) {
                    mRecordPresenter.stopPlay();
                    if (getItem(getCurrentItem()) instanceof RecordImgFrag) {
                        ((RecordImgFrag) getItem(getCurrentItem())).stopAnimation();
                    }
                }
                String filePath = CacheUtil.getAudioPath(mCourseId, getCurrentItem());
                // 判断这页是否已经录制过 有可能是mp3文件
                File f = new File(filePath);
                String mp3FilePath = filePath.replace(AudioType.amr, AudioType.mp3);
                File f3 = new File(mp3FilePath);
                if ((f.exists() || f3.exists()) && SpUser.inst().showRecordAgainDialog()) {
                    showRecordAgainDialog(filePath, mp3FilePath);
                } else {
                    mRecordPresenter.startRecord(filePath);
                    // 隐藏播放按钮
                    ((RecordImgFrag)getItem(getCurrentItem())).goneLayoutAudio();
                    goneView(mVoiceLine);
                    changeRecordState(true);
                }
            }
        }
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        //切换页面的时候如果在播放要停止
        mRecordPresenter.stopPlay();
        // 如果页面是视频页 要录音状态图片要变且不能点击
        if (getItem(position) instanceof RecordImgFrag && ((RecordImgFrag) getItem(position)).getFragType() == FragType.img) {
            mIvRecordState.setImageResource(R.drawable.selector_record_state);
            mIvRecordState.setClickable(true);
        } else {
            mIvRecordState.setImageResource(R.drawable.record_ic_can_not_click_state);
            mIvRecordState.setClickable(false);
        }
    }

    @Override
    public void moveLast() {
        if (mRecordState) {
            if (getCurrentItem() == 0) {
                return;
            }
            if (SpUser.inst().showSkipPageDialog() && !mShowSkipPageDialog) {
                mShowSkipPageDialog = true;
                showSkipPageDialog(ScrollType.last);
            } else {
                mRecordPresenter.stopRecord();
                changeRecordState(false);
                setCurrentItem(getCurrentItem() - 1);
            }
        } else {
            setCurrentItem(getCurrentItem()- 1);
        }
    }

    @Override
    public void moveNext() {
        if (mRecordState) {
            if (getCurrentItem() == 19) {
                return;
            }
            if (SpUser.inst().showSkipPageDialog() && !mShowSkipPageDialog) {
                mShowSkipPageDialog = true;
                showSkipPageDialog(ScrollType.next);
            } else {
                mRecordPresenter.stopRecord();
                changeRecordState(false);
                setCurrentItem(getCurrentItem() + 1);
            }
        } else {
            setCurrentItem(getCurrentItem()+ 1);
        }
    }

    @Override
    protected void onCallOffHooK() {
        if (mRecordState) {
            mRecordPresenter.stopRecord();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecordPresenter.onDestroy();
        if (mAnimationRecord.isRunning()) {
            mAnimationRecord.stop();
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == KJoinMeetingReqId) {
            return JsonParser.ev(r.getText(), JoinMeeting.class);
        } else {
            return super.onNetworkResponse(id, r);
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == KJoinMeetingReqId) {
            Result<JoinMeeting> r = (Result<JoinMeeting>) result;
            if (r.isSucceed()) {
                mJoinMeeting = r.getData();
                mWebSocketUrl = mJoinMeeting.getString(TJoinMeeting.wsUrl);
                mCourseDetailList = (ArrayList<CourseDetail>) mJoinMeeting.get(TJoinMeeting.course).getList(TCourse.details);
                mTvTotalPage.setText(String.valueOf(mCourseDetailList.size()));
                for (int i = 0; i < mCourseDetailList.size(); ++i) {
                    CourseDetail courseDetail = mCourseDetailList.get(i);
                    // 判断是视频还是图片
                    if (TextUtil.isEmpty(courseDetail.getString(TCourseDetail.videoUrl))) {
                        RecordImgFrag frag = RecordImgFragRouter
                                .create(courseDetail.getString(TCourseDetail.imgUrl))
                                .audioFilePath(CacheUtil.getAudioPath(mCourseId, i))
                                .audioUrl(courseDetail.getString(TCourseDetail.audioUrl))
                                .route();
                        frag.setPlayerListener(mRecordPresenter);
                        add(frag);
                    } else {
                        add(RecordVideoFragRouter
                                .create()
                                .videoUrl(courseDetail.getString(TCourseDetail.videoUrl))
                                .route());
                    }
                }
                // 判断第一页是不是视频
                if (TextUtil.isNotEmpty(mCourseDetailList.get(0).getString(TCourseDetail.videoUrl))) {
                    mIvRecordState.setImageResource(R.drawable.record_ic_can_not_click_state);
                    mIvRecordState.setClickable(false);
                }
                invalidate();
                // 链接websocket
                if (TextUtil.isNotEmpty(mWebSocketUrl)) {
                    mWebSocket = exeWebSocketReq(NetworkReq.newBuilder(mWebSocketUrl).build(), new WebSocketLink());
                }
            }
        } else {
            super.onNetworkSuccess(id, result);
        }
    }

    @Override
    public void setTotalRecordTimeTv(String str) {
        mTvNavBar.setText(str);
    }

    @Override
    public void startRecordState() {
        showView(mIvVoiceState);
        mAnimationRecord.start();
        mTvRecordState.setText("00'00''");
        getViewPager().setScrollable(false);
        showView(mGestureView);
    }

    @Override
    public void stopRecordState() {
        mAnimationRecord.stop();
        goneView(mIvVoiceState);
        //对应frag显示播放图标
        RecordImgFrag frag = (RecordImgFrag) getItem(getCurrentItem());
        frag.showLayoutAudio();
        mTvRecordState.setText("");
        getViewPager().setScrollable(true);
        goneView(mGestureView);
        // 停止录音的时候上传音频文件
        uploadAudioFile(mCourseId, getCurrentItem(), PlayType.reb);
    }

    @Override
    public void setRecordTimeTv(String str) {
        mTvRecordState.setText(str);
    }

    @Override
    public void showToast(int id) {
        if (mRecordState) {
            stopRecordState();
        } else {
            mRecordPresenter.stopPlay();
            ((RecordImgFrag)getItem(getCurrentItem())).stopAnimation();
        }
        super.showToast(id);
    }

    @Override
    public void setVoiceLineState(int i) {
        if (!mShowVoiceLine) {
            showView(mVoiceLine);
            mShowVoiceLine = !mShowVoiceLine;
        }
        mVoiceLine.setVolume(i);
    }

    @Override
    public void goneViceLine() {
        mShowVoiceLine = !mShowVoiceLine;
        goneView(mVoiceLine);
    }

    @Override
    public void onPermissionResult(int code, int result) {
        if (code == KMicroPermissionCode) {
            switch (result) {
                case PermissionResult.granted:
                    havePermissionState();
                    break;
                case PermissionResult.denied:
                case PermissionResult.never_ask: {
                    noPermissionState();
                }
                break;
            }
        }
    }

    protected void noPermissionState() {
        showView(mTvStartRemain);
        mTvStartRemain.setText(R.string.no_record_permission);
        mIvRecordState.setClickable(false);
    }

    protected void havePermissionState() {
        initPhoneCallingListener();
        goneView(mTvStartRemain);
        mIvRecordState.setClickable(true);
    }

    /**
     * 在录制中滑动，提示的dialog
     */
    private void showSkipPageDialog(@ScrollType int scrollType) {
        HintDialog dialog = new HintDialog(this);
        View view = inflate(R.layout.dialog_record_common);
        dialog.addHintView(view);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.dialog_record_common_cb);
        TextView tv = (TextView) view.findViewById(R.id.dialog_record_common_tv);
        if (scrollType == ScrollType.last) {
            tv.setText(R.string.skip_to_last_page);
        } else {
            tv.setText(R.string.skip_to_next_page);
        }
        dialog.addBlackButton(getString(R.string.confirm), v -> {
            if (checkBox.isChecked()) {
                SpUser.inst().neverShowSkipPageDialog();
            }
            // 停止录音 跳到下/上一页
            changeRecordState(false);
            mShowSkipPageDialog = false;
            mRecordPresenter.stopRecord();
            if (scrollType == ScrollType.last) {
                setCurrentItem(getCurrentItem() - 1);
            } else {
                setCurrentItem(getCurrentItem() + 1);
            }
        });
        dialog.addBlueButton(R.string.cancel, v -> {
            mShowSkipPageDialog = false;
        });
        dialog.show();
    }

    /**
     * 再次录制时的dialog
     */
    private void showRecordAgainDialog(String filePath, String mp3FilePath) {
        HintDialog dialog = new HintDialog(this);
        View view = inflate(R.layout.dialog_record_common);
        dialog.addHintView(view);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.dialog_record_common_cb);
        TextView tv = (TextView) view.findViewById(R.id.dialog_record_common_tv);
        tv.setText(R.string.record_again);
        dialog.addBlackButton(getString(R.string.confirm), v -> {
            if (checkBox.isChecked()) {
                SpUser.inst().neverShowRecordAgainDialog();
            }
            mRecordPresenter.startRecord(filePath);
            // 隐藏播放按钮
            ((RecordImgFrag)getItem(getCurrentItem())).goneLayoutAudio();
            goneView(mVoiceLine);
            changeRecordState(true);
            // 如果存在MP3文件，重新录制要改变播放文件  要删除MP3文件
            if ((new File(mp3FilePath)).exists()) {
                YSLog.d(TAG, "showRecordAgainDialog mp3 file path " + mp3FilePath);
                if (getItem(getCurrentItem()) instanceof RecordImgFrag) {
                    ((RecordImgFrag) getItem(getCurrentItem())).setAudioFilePath(filePath);
                }
                FileUtil.delFile(new File(mp3FilePath));
            }
        });
        dialog.addBlueButton(R.string.cancel, v -> {
            changeRecordState(false);
        });
        dialog.show();
    }

    protected void changeRecordState(boolean b) {
        mRecordState = b;
        mIvRecordState.setSelected(mRecordState);
    }
}
