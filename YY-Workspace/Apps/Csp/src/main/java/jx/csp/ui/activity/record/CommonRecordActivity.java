package jx.csp.ui.activity.record;

import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.R;
import jx.csp.dialog.HintDialog;
import jx.csp.sp.SpUser;
import jx.csp.ui.activity.record.RecordContract.RecordView;
import jx.csp.ui.activity.record.RecordFrag.onMediaPlayerListener;
import jx.csp.util.CacheUtil;
import jx.csp.view.GestureView.onGestureViewListener;
import lib.ys.ui.other.NavBar;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;

/**
 * 普通的录制
 *
 * @author CaiXiang
 * @since 2017/10/10
 */

public class CommonRecordActivity extends BaseRecordActivity implements RecordView, onGestureViewListener {

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
        super.initData();
        mRecordPresenter = new CommonRecordPresenterImpl(this);
        // 测试数据
        for (int i = 0; i < 20; ++i) {
            RecordFrag frag = RecordFragRouter
                    .create()
                    .audioFilePath(CacheUtil.getAudioCacheDir() + "/" + mMeetingId + "/" + (i + 1) + KAudioSuffix)
                    .route();
            frag.setPlayerListener((onMediaPlayerListener) mRecordPresenter);
            add(frag);
        }
    }

    @Override
    public void initNavBar(NavBar bar) {
        super.initNavBar(bar);
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
    }

    @Override
    public void onClick(int id) {
        if (id == R.id.record_iv_state) {
            if (mRecordState) {
                mRecordPresenter.stopRecord();
                changeRecordState(false);
            } else {
                String filePath = CacheUtil.getAudioCacheDir() + "/" + mMeetingId + "/" + (mCurrentPage + 1) + KAudioSuffix;
                // 判断这页是否已经录制过
                if ((new File(filePath)).exists() && SpUser.inst().showRecordAgainDialog()) {
                    showRecordAgainDialog(filePath);
                } else {
                    mRecordPresenter.startRecord(filePath);
                    // 隐藏播放按钮
                    ((RecordFrag)getItem(getCurrentItem())).goneLayoutAudio();
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
                setCurrentItem(mCurrentPage - 1);
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
                setCurrentItem(mCurrentPage + 1);
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
        RecordFrag frag = (RecordFrag) getItem(getCurrentItem());
        frag.showLayoutAudio();
        mTvRecordState.setText("");
        getViewPager().setScrollable(true);
        goneView(mGestureView);
    }

    @Override
    public void setRecordTimeTv(String str) {
        mTvRecordState.setText(str);
    }

    @Override
    public void setTimeRemainTv() {
    }

    @Override
    public void showToast(int id) {
        showToast(id);
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
    public void changeRecordIvRes() {

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
                setCurrentItem(mCurrentPage - 1);
            } else {
                setCurrentItem(mCurrentPage + 1);
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
    private void showRecordAgainDialog(String filePath) {
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
            ((RecordFrag)getItem(getCurrentItem())).goneLayoutAudio();
            goneView(mVoiceLine);
            changeRecordState(true);
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
