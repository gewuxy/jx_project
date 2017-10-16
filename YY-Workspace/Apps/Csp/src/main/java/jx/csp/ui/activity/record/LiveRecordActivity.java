package jx.csp.ui.activity.record;

import jx.csp.R;
import jx.csp.ui.activity.record.RecordContract.RecordView;
import lib.ys.ui.other.NavBar;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;

/**
 * 直播录制
 *
 * @author CaiXiang
 * @since 2017/10/10
 */

public class LiveRecordActivity extends BaseRecordActivity implements RecordView{

    @Override
    public void initData() {
        super.initData();
        for (int i = 0; i < 20; ++i) {
            add(RecordFragRouter.create().route());
        }
    }

    @Override
    public void initNavBar(NavBar bar) {
        super.initNavBar(bar);
    }

    @Override
    public void setViews() {
        super.setViews();
        mTvNavBar.setText("9月30日 14：00");
        // 检查录音权限
        if (checkPermission(KMicroPermissionCode, Permission.micro_phone)) {
            havePermissionState();
        } else {
            noPermissionState();
        }
        goneView(mGestureView);
    }

    @Override
    protected void onClick(int id) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCallOffHooK() {

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

    protected void havePermissionState() {
        initPhoneCallingListener();
        goneView(mTvStartRemain);
        mIvRecordState.setClickable(true);
        mIvVoiceState.setSelected(true);
    }

    protected void noPermissionState() {
        showView(mTvStartRemain);
        mTvStartRemain.setText(R.string.no_record_permission);
        mIvRecordState.setClickable(false);
        mIvVoiceState.setSelected(false);
    }

    @Override
    public void setTotalRecordTimeTv(String str) {

    }

    @Override
    public void startRecordState() {

    }

    @Override
    public void stopRecordState() {

    }

    @Override
    public void setRecordTimeTv(String str) {

    }

    @Override
    public void setTimeRemainTv() {

    }

    @Override
    public void showToast(int id) {
    }

    @Override
    public void setVoiceLineState(int i) {
    }

    @Override
    public void goneViceLine() {
    }

    @Override
    public void changeRecordIvRes() {
        mIvRecordState.setImageResource(R.drawable.selector_record_live_state_warm);
        mIvRecordState.setSelected(true);
    }
}
