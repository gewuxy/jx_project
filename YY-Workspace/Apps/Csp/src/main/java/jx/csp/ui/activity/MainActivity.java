package jx.csp.ui.activity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jx.csp.Extra;
import jx.csp.R;
import jx.csp.constant.Constants;
import jx.csp.constant.FiltrateType;
import jx.csp.dialog.BottomDialog;
import jx.csp.dialog.CommonDialog1;
import jx.csp.dialog.CountdownDialog;
import jx.csp.dialog.FunctionGuideDialog;
import jx.csp.dialog.GuideDialog;
import jx.csp.dialog.UpdateNoticeDialog;
import jx.csp.model.CheckAppVersion;
import jx.csp.model.CheckAppVersion.TCheckAppVersion;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.RecordUnusualState;
import jx.csp.model.RecordUnusualState.TRecordUnusualState;
import jx.csp.model.VipPackage;
import jx.csp.model.VipPackage.TPackage;
import jx.csp.model.meeting.Scan;
import jx.csp.model.meeting.Scan.DuplicateType;
import jx.csp.model.meeting.Scan.TScan;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.CommonAPI;
import jx.csp.network.NetworkApiDescriptor.MeetingAPI;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.serv.CommonServ.ReqType;
import jx.csp.serv.CommonServRouter;
import jx.csp.serv.DownloadApkServ;
import jx.csp.serv.WebSocketServRouter;
import jx.csp.sp.SpApp;
import jx.csp.sp.SpUser;
import jx.csp.ui.activity.edit.ChoicePhotoActivityRouter;
import jx.csp.ui.activity.edit.PhotoActivityRouter;
import jx.csp.ui.activity.login.auth.AuthLoginActivity;
import jx.csp.ui.activity.login.auth.AuthLoginOverseaActivity;
import jx.csp.ui.activity.main.ScanActivity;
import jx.csp.ui.activity.me.GreenHandsGuideActivityRouter;
import jx.csp.ui.activity.me.MeActivity;
import jx.csp.ui.activity.record.RecordActivityRouter;
import jx.csp.ui.frag.main.MeetCardFrag;
import jx.csp.ui.frag.main.MeetFrag;
import jx.csp.ui.frag.main.MeetListFrag;
import jx.csp.util.CacheUtil;
import jx.csp.util.UISetter;
import jx.csp.util.Util;
import jx.csp.view.MainMenu;
import jx.csp.view.MainMenu.MainMenuClickListener;
import lib.jg.jpush.SpJPush;
import lib.jx.notify.LiveNotifier;
import lib.jx.notify.LiveNotifier.LiveNotifyType;
import lib.jx.notify.LiveNotifier.OnLiveNotify;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.activity.base.BaseVpActivity;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.impl.SingletonImpl;
import lib.ys.model.FileSuffix;
import lib.ys.ui.other.NavBar;
import lib.ys.util.PhotoUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.res.ResLoader;

/**
 * 首页
 *
 * @auther WangLan
 * @since 2017/9/30
 */

public class MainActivity extends BaseVpActivity implements OnLiveNotify, MainMenuClickListener {

    /**
     * 页面切换
     */
    private final int KPageCard = 0;
    private final int KPageList = 1;
    /**
     * 权限申请
     */
    private final int KPerCameraScan = 10;
    private final int KPerCameraAdd = 11;
    private final int KPerPhoto = 12;
    /**
     * 网络请求
     */
    private final int KUpdateProfileReqId = 21;
    private final int KCheckAppVersionReqId = 22;
    private final int KJoinRecordCheckReqId = 23;
    /**
     * 其他
     */
    private final int KReqCamera = 31;
    private final int KGuideCode = 100;//功能指引

    private View mMidView;
    private TextView mTvTitle;
    private TextView mTvExpireRemind; // 会员到期提醒
    private ImageView mIvShift;
    private TextView mTvPast;
    private MainMenu mMainMenu;

    private MeetCardFrag mCardFrag;
    private MeetListFrag mListFrag;

    private CountdownDialog mCountdownDialog;

    @FiltrateType
    public int mFiltrateType;

    private String mPhotoPath;

    @Override
    public void initData() {
        mCardFrag = new MeetCardFrag();
        mCardFrag.setOnMeetListener(() -> newMeetDialog());
        add(mCardFrag);

        mListFrag = new MeetListFrag();
        mListFrag.setOnMeetListener(() -> newMeetDialog());
        add(mListFrag);

        mFiltrateType = FiltrateType.all;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void initNavBar(NavBar bar) {
        //添加左边布局
        bar.addViewLeft(R.drawable.ic_default_user_header, null, v -> startActivity(MeActivity.class));

        //添加中间布局
        View view = inflate(R.layout.layout_main_text_mid);
        mMidView = view.findViewById(R.id.main_layout_text_mid);
        mTvTitle = view.findViewById(R.id.main_tv_title);
        mTvExpireRemind = view.findViewById(R.id.main_tv_remind);
        VipPackage p = Profile.inst().get(TProfile.cspPackage);
        if (p != null) {
            int day = p.getInt(TPackage.expireDays);
            if (day > 0) {
                mTvExpireRemind.setText(String.format(getString(R.string.will_reminder), day));
                showView(mTvExpireRemind);
            } else {
                goneView(mTvExpireRemind);
            }
        }
        bar.addViewMid(view);

        //添加右边布局
        ViewGroup group = bar.addViewRight(R.drawable.main_shift_selector, v -> {
            boolean flag = getCurrPosition() == KPageCard;
            if (!flag) {
                // 列表
                mIvShift.setSelected(false);
                setCurrPosition(KPageCard, false);
                mCardFrag.setAllMeets(mListFrag.getAllMeets());
                mCardFrag.setFiltrateType(mFiltrateType);
                SpUser.inst().saveMainPage(KPageCard);
            } else {
                // 卡片
                mIvShift.setSelected(true);
                setCurrPosition(KPageList, false);
                mListFrag.setAllMeets(mCardFrag.getAllMeets());
                mListFrag.setFiltrateType(mFiltrateType);
                SpUser.inst().saveMainPage(KPageList);
            }
        });
        mIvShift = Util.getBarView(group, ImageView.class);
        mIvShift.setSelected(false);

        Util.addDivider(bar);
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvPast = findView(R.id.main_tv_past);
        mMainMenu = findView(R.id.layout_main_menu);
    }

    @Override
    public void setViews() {
        super.setViews();

        if (SpApp.inst().getGuidelines()) {
            GuideDialog dialog = new GuideDialog(this);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setWatchListener(() -> {
                if (Util.checkAppCn()) {
                    GreenHandsGuideActivityRouter.create("1").route(this, KGuideCode);
                } else {
                    GreenHandsGuideActivityRouter.create("2").route(this, KGuideCode);
                }
                dialog.dismiss();
            });
            dialog.show();
            SpApp.inst().saveGuidelines();
        }

        setOnClickListener(mMidView);
        mMainMenu.setMainMenuClickListener(this);

        // 不能左右滑动
        setScrollable(false);
        // vp的缓存量
        setOffscreenPageLimit(getCount());

        // 判断是否已经绑定极光推送
        YSLog.d(TAG, " 是否重新绑定极光推送 " + SpJPush.inst().needRegisterJP());
        YSLog.d(TAG, " 保存的RegistrationId = " + SpJPush.inst().registerId());
        if (SpJPush.inst().needRegisterJP() && !TextUtil.isEmpty(SpJPush.inst().registerId())) {
            CommonServRouter.create(ReqType.j_push)
                    .jPushRegisterId(SpJPush.inst().registerId())
                    .route(this);
        }
        // 静默更新用户数据
        if (SpUser.inst().needUpdateProfile()) {
            YSLog.d(TAG, "更新个人数据");
            exeNetworkReq(KUpdateProfileReqId, UserAPI.uploadProfileInfo().build());
        }

        exeNetworkReq(KCheckAppVersionReqId, CommonAPI.checkAppVersion().build());

        pastHint(Profile.inst().get(TProfile.cspPackage));
        LiveNotifier.inst().add(this);

        addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int page = SpUser.inst().getMainAcVpPage();
                if (page != 0) {
                    setCurrPosition(page, false);
                    mIvShift.setSelected(true);
                }
                // 判断是否有录音异常退出记录
                if (RecordUnusualState.inst().getUnusualExitState()) {
                    CommonDialog1 dialog = new CommonDialog1(MainActivity.this);
                    dialog.setTitle(R.string.home_continue_record);
                    dialog.setContent(R.string.home_continue_record_content);
                    dialog.addBlackButton(R.string.save, v -> {
                        RecordUnusualState.inst().put(TRecordUnusualState.unusualExit, false);
                        RecordUnusualState.inst().saveToSp();
                    });
                    dialog.addBlackButton(R.string.home_continue_record, v -> {
                        // 先判断这个会议是否有人在录音
                        exeNetworkReq(KJoinRecordCheckReqId, MeetingAPI.joinCheck(RecordUnusualState.inst().getString(TRecordUnusualState.courseId), 0).build());
                    });
                    dialog.setCancelable(false);
                    dialog.show();
                    // 上传音频
                    CommonServRouter.create(ReqType.upload_audio).route(MainActivity.this);
                }
                removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_layout_text_mid: {
                if (mFiltrateType == FiltrateType.all) {
                    mFiltrateType = FiltrateType.ppt;
                    mTvTitle.setText("PPT");
                } else if (mFiltrateType == FiltrateType.ppt) {
                    mFiltrateType = FiltrateType.photo;
                    mTvTitle.setText(R.string.photo);
                } else {
                    mFiltrateType = FiltrateType.all;
                    mTvTitle.setText(R.string.main_title);
                }
                mCardFrag.setFiltrateType(mFiltrateType);
                mListFrag.setFiltrateType(mFiltrateType);
            }
            break;
        }
    }

    @Override
    public void switchClick() {
        if (mMidView != null) {
            mMidView.performClick();
        }
    }

    @Override
    public void newClick() {
        newMeetDialog();
    }

    @Override
    public void scanClick() {
        if (checkPermission(KPerCameraScan, Permission.camera)) {
            startActivity(ScanActivity.class);
        }
    }

    private void newMeetDialog() {
        final BottomDialog dialog = new BottomDialog(MainActivity.this, position -> {

            switch (position) {
                case 0: {
                    if (checkPermission(KPerCameraAdd, Permission.camera)) {
                        mPhotoPath = CacheUtil.getUploadCacheDir() + "photo" + System.currentTimeMillis() + FileSuffix.jpg;
                        PhotoUtil.fromCamera(MainActivity.this, mPhotoPath, KReqCamera);
                    }
                }
                break;
                case 1: {
                    if (checkPermission(KPerPhoto, Permission.storage)) {
                        PhotoActivityRouter.create()
                                .fromMain(true)
                                .maxSelect(Constants.KPhotoMax)
                                .route(MainActivity.this);
                    }
                }
                break;
            }
        });

        dialog.addItem(getString(R.string.my_message_take_photo), ResLoader.getColor(R.color.text_333));
        dialog.addItem(getString(R.string.my_message_from_album_select), ResLoader.getColor(R.color.text_333));
        dialog.addItem(getString(R.string.cancel), ResLoader.getColor(R.color.text_333));
        dialog.show();
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        if (id == KUpdateProfileReqId) {
            return JsonParser.ev(resp.getText(), Profile.class);
        } else if (id == KJoinRecordCheckReqId) {
            return JsonParser.ev(resp.getText(), Scan.class);
        } else if (id == KCheckAppVersionReqId) {
            return JsonParser.ev(resp.getText(), CheckAppVersion.class);
        } else {
            return JsonParser.error(resp.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (id == KUpdateProfileReqId) {
            if (r.isSucceed()) {
                YSLog.d(TAG, "个人数据更新成功");
                Profile profile = (Profile) r.getData();
                Profile.inst().update(profile);
                SpUser.inst().updateProfileRefreshTime();
                if (profile == null) {
                    onNetworkError(id, r.getError());
                    return;
                }
                VipPackage p = profile.get(TProfile.cspPackage);
                if (p != null) {
                    pastHint(p);
                    int day = p.getInt(TPackage.expireDays);
                    if (day > 0) {
                        mTvExpireRemind.setText(String.format(getString(R.string.will_reminder), day));
                        showView(mTvExpireRemind);
                    } else {
                        goneView(mTvExpireRemind);
                    }
                }
                notify(NotifyType.profile_change);
            } else {
                onNetworkError(id, r.getError());
            }
        } else if (id == KJoinRecordCheckReqId) {
            if (r.isSucceed()) {
                Scan scan = (Scan) r.getData();
                // 是否有人在直播或者录播
                boolean state = scan.getInt(TScan.duplicate) == DuplicateType.yes && TextUtil.isNotEmpty(scan.getString(TScan.wsUrl));
                // 先判断直播时间是否已经到了 录播不需要判断时间  再判断是否有人在录播或者直播
                // 有人情况下要弹dialog 确定后连websocket
                if (state) {
                    CommonDialog1 dialog = new CommonDialog1(this);
                    dialog.setTitle(R.string.home_continue_record);
                    dialog.setContent(R.string.main_record_dialog);
                    dialog.addBlackButton(R.string.confirm_continue, v -> {
                        WebSocketServRouter.create(scan.getString(TScan.wsUrl)).route(MainActivity.this);
                        // 倒计时结束没有收到websocket默认进入会议
                        mCountdownDialog = new CountdownDialog(MainActivity.this, 15);
                        mCountdownDialog.setOnDismissListener(dialogInterface -> {
                            WebSocketServRouter.stop(MainActivity.this);
                            mCountdownDialog.stopCountDown();
                        });
                        mCountdownDialog.show();
                    });
                    dialog.addBlackButton(R.string.cancel);
                    dialog.show();
                } else {
                    RecordActivityRouter.create(RecordUnusualState.inst().getString(TRecordUnusualState.courseId)).route(MainActivity.this);
                }
            } else {
                onNetworkError(id, r.getError());
            }
        } else {
            if (r.isSucceed()) {
                SpApp.inst().saveCheckAppVersionTime();
                CheckAppVersion checkAppVer = (CheckAppVersion) r.getData();
                if (checkAppVer != null) {
                    addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            UpdateNoticeDialog dialog = new UpdateNoticeDialog(MainActivity.this);
                            dialog.setVersion(checkAppVer.getString(TCheckAppVersion.versionStr));
                            dialog.setContent(checkAppVer.getString(TCheckAppVersion.details));
                            // 判断是否需要强制更新
                            if (checkAppVer.getBoolean(TCheckAppVersion.forced)) {
                                dialog.addButton(R.string.update_now, R.color.text_167afe, v -> {
                                    Intent intent = new Intent(MainActivity.this, DownloadApkServ.class);
                                    intent.putExtra(Extra.KData, checkAppVer.getString(TCheckAppVersion.downLoadUrl));
                                    startService(intent);
                                });
                                dialog.setCancelable(false);
                            } else {
                                dialog.addButton(R.string.remind_later, R.color.text_af, null);
                                dialog.addButton(R.string.update_now, R.color.text_167afe, v -> {
                                    Intent intent = new Intent(MainActivity.this, DownloadApkServ.class);
                                    intent.putExtra(Extra.KData, checkAppVer.getString(TCheckAppVersion.downLoadUrl));
                                    startService(intent);
                                });
                            }
                            dialog.show();
                            removeOnGlobalLayoutListener(this);
                        }
                    });
                }
            }
        }
    }

    /**
     * 过期提醒
     *
     * @param p 套餐信息
     */
    private void pastHint(VipPackage p) {
        if (p == null) {
            return;
        }
        int num = p.getInt(TPackage.hiddenMeetCount);
        if (num > 0) {
            mTvPast.setText(String.format(getString(R.string.overdue_reminder), num));
            showView(mTvPast);
        } else {
            goneView(mTvPast);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == KReqCamera) {
            ArrayList<String> p = new ArrayList<>();
            p.add(mPhotoPath);
            ChoicePhotoActivityRouter.create().paths(p).route(MainActivity.this);
        } else if (requestCode == KGuideCode) {
            FunctionGuideDialog d = new FunctionGuideDialog(this);
            d.setCancelable(false);
            d.show();
        }
    }

    @Override
    public void onLiveNotify(@LiveNotifyType int type, Object data) {
        if (!activityTop()) {
            return;
        }
        switch (type) {
            case LiveNotifyType.accept: {
                YSLog.d(TAG, "接收到同意进入指令");
                if (RecordUnusualState.inst().getUnusualExitState()) {
                    WebSocketServRouter.stop(this);
                    if (mCountdownDialog != null && mCountdownDialog.isShowing()) {
                        mCountdownDialog.dismiss();
                    }
                    RecordActivityRouter.create(RecordUnusualState.inst().getString(TRecordUnusualState.courseId)).route(MainActivity.this);
                } else {
                    if (getCurrItem() instanceof MeetFrag) {
                        ((MeetFrag) getCurrItem()).allowEnter();
                    }
                }
            }
            break;
            case LiveNotifyType.reject: {
                YSLog.d(TAG, "接收到拒绝进入指令");
                if (RecordUnusualState.inst().getUnusualExitState()) {
                    WebSocketServRouter.stop(this);
                    RecordUnusualState.inst().put(TRecordUnusualState.unusualExit, false);
                    RecordUnusualState.inst().saveToSp();
                    if (mCountdownDialog != null && mCountdownDialog.isShowing()) {
                        mCountdownDialog.dismiss();
                    }
                    RecordActivityRouter.create(RecordUnusualState.inst().getString(TRecordUnusualState.courseId)).route(MainActivity.this);
                } else {
                    if (getCurrItem() instanceof MeetFrag) {
                        ((MeetFrag) getCurrItem()).notAllowEnter();
                    }
                }
            }
            break;
        }
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        switch (type) {
            case NotifyType.logout: {
                finish();
            }
            break;
            case NotifyType.token_out_of_date: {
                Intent intent;
                //清除栈里的activity2
                if (Util.checkAppCn()) {
                    intent = new Intent(this, AuthLoginActivity.class);
                } else {
                    intent = new Intent(this, AuthLoginOverseaActivity.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                Profile.inst().clear();
                startActivity(intent);
                finish();
            }
            break;
            case NotifyType.delete_meeting_success: {
                showToast(R.string.delete_success);
            }
            break;
            case NotifyType.delete_meeting_fail: {
                showToast((String) data);
            }
            break;
            case NotifyType.profile_change: {
                VipPackage p = Profile.inst().get(TProfile.cspPackage);
                if (p != null) {
                    int day = p.getInt(TPackage.expireDays);
                    if (day > 0) {
                        mTvExpireRemind.setText(String.format(getString(R.string.will_reminder), day));
                        showView(mTvExpireRemind);
                    } else {
                        goneView(mTvExpireRemind);
                    }
                }
            }
            break;
            case NotifyType.over_live: {
                if (activityTop()) {
                    showToast(R.string.live_have_end);
                }
            }
            break;
            case NotifyType.meet_num: {
                int num = (int) data;
                VipPackage p = Profile.inst().get(TProfile.cspPackage);
                if (p != null) {
                    p.put(TPackage.hiddenMeetCount, num);
                }
                pastHint(p);
            }
            break;
            case NotifyType.main_refresh: {
                if (mCardFrag != null) {
                    mCardFrag.swipeRefresh();
                }
                if (mListFrag != null) {
                    mListFrag.swipeRefresh();
                }
            }
            break;
            case NotifyType.course_already_delete_main: {
                CommonDialog1 dialog = new CommonDialog1(this);
                dialog.setTitle(R.string.course_already_delete);
                dialog.setContent(R.string.please_edit_other_course);
                dialog.addBlackButton(R.string.ok);
                dialog.show();
            }
            break;
        }
    }

    private boolean activityTop() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            List<RunningTaskInfo> list = am.getRunningTasks(1);
            if (list != null && list.size() > 0) {
                ComponentName cpn = list.get(0).topActivity;
                if (getLocalClassName().equals(cpn.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (enableExit()) {
            super.onBackPressed();
        } else {
            showToast(R.string.click_again_exit);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mMainMenu.releaseMediaPlayer();
        LiveNotifier.inst().remove(this);
        SingletonImpl.inst().freeAll();
    }

    @Override
    public void onPermissionResult(int code, @PermissionResult int result) {
        if (code == KPerCameraAdd) {
            switch (result) {
                case PermissionResult.granted: {
                    mPhotoPath = CacheUtil.getUploadCacheDir() + "photo" + System.currentTimeMillis() + FileSuffix.jpg;
                    PhotoUtil.fromCamera(MainActivity.this, mPhotoPath, KReqCamera);
                }
                break;
                case PermissionResult.denied:
                case PermissionResult.never_ask: {
                    UISetter.cameraNoPermission(MainActivity.this);
                }
                break;
            }
        } else if (code == KPerPhoto) {
            switch (result) {
                case PermissionResult.granted: {
                    PhotoActivityRouter.create()
                            .fromMain(true)
                            .maxSelect(Constants.KPhotoMax)
                            .route(MainActivity.this);
                }
                break;
                case PermissionResult.denied:
                case PermissionResult.never_ask: {
                    UISetter.photoNoPermission(MainActivity.this);
                }
                break;
            }
        } else if (code == KPerCameraScan) {
            switch (result) {
                case PermissionResult.granted: {
                    startActivity(ScanActivity.class);
                }
                break;
                case PermissionResult.denied:
                case PermissionResult.never_ask: {
                    showToast(getString(R.string.user_photo_permission));
                }
                break;
            }
        }
    }
}
