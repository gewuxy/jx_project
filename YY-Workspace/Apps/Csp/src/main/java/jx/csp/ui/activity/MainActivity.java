package jx.csp.ui.activity;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.TextView;

import jx.csp.Extra;
import jx.csp.R;
import jx.csp.dialog.UpdateNoticeDialog;
import jx.csp.model.CheckAppVersion;
import jx.csp.model.CheckAppVersion.TCheckAppVersion;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.VipPackage;
import jx.csp.model.VipPackage.TPackage;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Copy;
import jx.csp.model.meeting.Copy.TCopy;
import jx.csp.model.meeting.Live.LiveState;
import jx.csp.model.meeting.Record.PlayState;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.CommonAPI;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.serv.CommonServ.ReqType;
import jx.csp.serv.CommonServRouter;
import jx.csp.serv.DownloadApkServ;
import jx.csp.sp.SpApp;
import jx.csp.sp.SpUser;
import jx.csp.ui.activity.login.AuthLoginActivity;
import jx.csp.ui.activity.login.AuthLoginOverseaActivity;
import jx.csp.ui.activity.main.ScanActivity;
import jx.csp.ui.activity.me.MeActivity;
import jx.csp.ui.frag.main.MeetGridFrag;
import jx.csp.ui.frag.main.MeetVpFrag;
import jx.csp.util.Util;
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
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;

/**
 * 首页
 *
 * @auther WangLan
 * @since 2017/9/30
 */

public class MainActivity extends BaseVpActivity implements OnLiveNotify {

    private final int KCameraPermissionCode = 10;
    private final int KUpdateProfileReqId = 1;
    private final int KCheckAppVersionReqId = 2;
    private final int KPageGrid = 0;
    private final int KPageVp = 1;

    private TextView mTvExpireRemind; // 会员到期提醒
    private ImageView mIvShift;

    private MeetGridFrag mGridFrag;
    private MeetVpFrag mVpFrag;

    private TextView mTvPast;

    @Override
    public void initData() {
        mGridFrag = new MeetGridFrag();
        mVpFrag = new MeetVpFrag();

        mVpFrag.setListener(flag -> {
            if (mTvPast == null) {
                return;
            }
            if (flag) {
                // 进行中显示优先
                goneView(mTvPast);
            } else {
                pastHint(Profile.inst().get(TProfile.cspPackage));
            }
        });

        add(mGridFrag);
        add(mVpFrag);
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
        View midView = inflate(R.layout.layout_main_text_mid);
        mTvExpireRemind = midView.findViewById(R.id.main_tv_remind);
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
        bar.addViewMid(midView);

        //添加右边布局
        ViewGroup group = bar.addViewRight(R.drawable.main_shift_selector, v -> {
            boolean flag = getCurrPosition() == KPageGrid;
            if (!flag) {
                // 网格
                mIvShift.setSelected(false);
                // 跳转到九宫格Item，false表示没有切换效果,true是划过去的
                setCurrPosition(KPageGrid, false);
                mGridFrag.setPosition(mVpFrag.getPosition());
                SpUser.inst().saveMainPage(KPageGrid);
                pastHint(Profile.inst().get(TProfile.cspPackage));
            } else {
                // 卡片
                mIvShift.setSelected(true);
                setCurrPosition(KPageVp, false);
                int position = mGridFrag.getPosition();
                mVpFrag.setPosition(position);
                SpUser.inst().saveMainPage(KPageVp);
                if (mVpFrag.reminder(position)) {
                    // 进行中显示优先
                    goneView(mTvPast);
                } else {
                    pastHint(Profile.inst().get(TProfile.cspPackage));
                }
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
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.main_scan);

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

        mGridFrag.setListener(data -> {
            mVpFrag.setData(data);
            mVpFrag.nativeInvalidate();
            addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    int page = SpUser.inst().getMainAcVpPage();
                    if (page != 0) {
                        setCurrPosition(page, false);
                        mVpFrag.setPosition(0);
                        mIvShift.setSelected(true);
                    }
                    removeOnGlobalLayoutListener(this);
                }
            });
        });
        // 静默更新用户数据
        if (SpUser.inst().needUpdateProfile()) {
            YSLog.d(TAG, "更新个人数据");
            exeNetworkReq(KUpdateProfileReqId, UserAPI.uploadProfileInfo().build());
        }
        // 检查是否有新版本app
//        if (SpApp.inst().needCheckAppVersion()) {
//        }
        exeNetworkReq(KCheckAppVersionReqId, CommonAPI.checkAppVersion().build());

        pastHint(Profile.inst().get(TProfile.cspPackage));
        LiveNotifier.inst().add(this);
    }

    @Override
    public void onClick(View v) {
        if (checkPermission(KCameraPermissionCode, Permission.camera)) {
            startActivity(ScanActivity.class);
        }
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        if (id == KUpdateProfileReqId) {
            return JsonParser.ev(resp.getText(), Profile.class);
        } else {
            return JsonParser.ev(resp.getText(), CheckAppVersion.class);
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
    public void onLiveNotify(@LiveNotifyType int type, Object data) {
        switch (type) {
            case LiveNotifyType.accept: {
                YSLog.d(TAG, "接收到同意进入指令");
                if (getCurrItem() instanceof MeetGridFrag) {
                    ((MeetGridFrag) getCurrItem()).allowEnter();
                }
            }
            break;
            case LiveNotifyType.reject: {
                YSLog.d(TAG, "接收到拒绝进入指令");
                if (getCurrItem() instanceof MeetGridFrag) {
                    ((MeetGridFrag) getCurrItem()).notAllowEnter();
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
                Profile.inst().clear();
                startActivity(intent);
                finish();
            }
            break;
            case NotifyType.delete_meeting_success: {
                String str = (String) data;
                YSLog.d(TAG, str + "删除接收通知");
                for (Meet meet : mGridFrag.getData()) {
                    if (Integer.valueOf(str) == meet.getInt(TMeet.id)) {
                        mGridFrag.getData().remove(meet);
                        mGridFrag.invalidate();
                        break;
                    }
                }
                showToast(R.string.delete_success);
            }
            break;
            case NotifyType.delete_meeting_fail: {
                showToast((String) data);
            }
            break;
            case NotifyType.copy_duplicate: {
                Copy copy = (Copy) data;
                for (Meet meet : mGridFrag.getData()) {
                    if (copy.getInt(TCopy.oldId) == meet.getInt(TMeet.id)) {
                        Meet m = (Meet) meet.clone();
                        m.put(TMeet.title, m.getString(TMeet.title) + getString(R.string.duplicate));
                        m.put(TMeet.id, copy.getInt(TCopy.id));
                        m.put(TMeet.title, copy.getString(TCopy.title));
                        m.put(TMeet.livePage, 0);
                        m.put(TMeet.playPage, 0);
                        // 复制的会议默认放到最前面 页面跳到最前面位置
                        mGridFrag.addItem(0, m);
                        mGridFrag.setPosition(0);
                        mGridFrag.invalidate();
                        break;
                    }
                }
                showToast(R.string.copy_duplicate_success);
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
                showToast(R.string.live_have_end);
                // 修改数据源
                String id = (String) data;
                for (Meet meet : mGridFrag.getData()) {
                    if (meet.getString(TMeet.id).equals(id)) {
                        meet.put(TMeet.liveState, LiveState.end);
                        meet.put(TMeet.playState, PlayState.end);
                        mGridFrag.invalidate();
                        break;
                    }
                }
                // 显示分享会议回放提示
//                ((IMeetOpt) getItem(KPageGrid)).showSharePlayback(id);
//                ((IMeetOpt) getItem(KPageVp)).showSharePlayback(id);
//                // 5秒后隐藏分享会议回放提示
//                Message msg = new Message();
//                msg.what = KGoneMsgWhat;
//                msg.arg1 = Integer.valueOf(id).intValue();
//                mHandler.sendMessageDelayed(msg, TimeUnit.SECONDS.toMillis(5));
            }
            break;
            case NotifyType.meet_num: {
                boolean state = getCurrPosition() == KPageVp;
                if (state) {
                    // 数据来源于网格的,不重复刷新状态
                    return;
                }
                int num = (int) data;
                VipPackage p = Profile.inst().get(TProfile.cspPackage);
                if (p != null) {
                    p.put(TPackage.hiddenMeetCount, num);
                }
                pastHint(p);
            }
            break;
        }
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

        LiveNotifier.inst().remove(this);
        SingletonImpl.inst().freeAll();
    }

    @Override
    public void onPermissionResult(int code, @PermissionResult int result) {
        switch (result) {
            case PermissionResult.granted: {
                if (code == KCameraPermissionCode) {
                    startActivity(ScanActivity.class);
                }
            }
            break;
            case PermissionResult.denied:
            case PermissionResult.never_ask: {
                if (code == KCameraPermissionCode) {
                    showToast(getString(R.string.user_photo_permission));
                }
            }
            break;
        }
    }
}
