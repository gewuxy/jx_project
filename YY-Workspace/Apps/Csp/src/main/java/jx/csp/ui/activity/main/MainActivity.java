package jx.csp.ui.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

import jx.csp.App;
import jx.csp.R;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Copy;
import jx.csp.model.meeting.Copy.TCopy;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.serv.CommonServ.ReqType;
import jx.csp.serv.CommonServRouter;
import jx.csp.sp.SpUser;
import jx.csp.ui.activity.login.AuthLoginActivity;
import jx.csp.ui.activity.login.AuthLoginOverseaActivity;
import jx.csp.ui.activity.me.MeActivity;
import jx.csp.ui.frag.main.IMeetOpt;
import jx.csp.ui.frag.main.MeetGridFrag;
import jx.csp.ui.frag.main.MeetVpFrag;
import jx.csp.util.Util;
import lib.jg.jpush.SpJPush;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.impl.SingletonImpl;
import lib.ys.network.image.NetworkImageView;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.view.LayoutUtil;
import lib.yy.notify.LiveNotifier;
import lib.yy.notify.LiveNotifier.LiveNotifyType;
import lib.yy.notify.LiveNotifier.OnLiveNotify;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseVpActivity;

/**
 * 首页
 *
 * @auther WangLan
 * @since 2017/9/30
 */

public class MainActivity extends BaseVpActivity implements OnLiveNotify {

    private final int KPageGrid = 0;
    private final int KPageVp = 1;

    private ImageView mIvShift;
    private NetworkImageView mIvAvatar;

    private MeetGridFrag mGridFrag;
    private MeetVpFrag mVpFrag;

    @Override
    public void initData(Bundle state) {
        mGridFrag = new MeetGridFrag();
        mVpFrag = new MeetVpFrag();

        add(mGridFrag);
        add(mVpFrag);
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void initNavBar(NavBar bar) {
        View view = inflate(R.layout.layout_main_user);
        View layout = view.findViewById(R.id.main_layout_user);
        ViewGroup.LayoutParams params = LayoutUtil.getLinearParams(fit(App.NavBarVal.KHeightDp), fit(App.NavBarVal.KHeightDp));
        layout.setLayoutParams(params);
        mIvAvatar = view.findViewById(R.id.main_iv_user);
        mIvAvatar.placeHolder(R.drawable.ic_default_user_header)
                .url(Profile.inst().getString(TProfile.avatar))
                .load();
        bar.addViewLeft(view, v -> startActivity(MeActivity.class));

        bar.addTextViewMid(getString(R.string.app_name));

        ViewGroup group = bar.addViewRight(R.drawable.main_shift_selector, v -> {
            boolean flag = getCurrPosition() == KPageGrid;
            if (!flag) {
                // 网格
                mIvShift.setSelected(false);
                // 跳转到九宫格Item，false表示没有切换效果,true是划过去的
                setCurrPosition(KPageGrid, false);
                mGridFrag.setPosition(mVpFrag.getPosition());
                SpUser.inst().saveMainPage(KPageGrid);
            } else {
                // 列表
                mIvShift.setSelected(true);
                setCurrPosition(KPageVp, false);
                mVpFrag.setPosition(mGridFrag.getPosition());
                SpUser.inst().saveMainPage(KPageVp);
            }
        });
        mIvShift = Util.getBarView(group, ImageView.class);
        mIvShift.setSelected(false);
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
            exeNetworkReq(UserAPI.uploadProfileInfo().build());
        }
        LiveNotifier.inst().add(this);
    }

    @Override
    public void onClick(View v) {
        if (checkPermission(0, Permission.camera)) {
            startActivity(ScanActivity.class);
        }
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), Profile.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            YSLog.d(TAG, "个人数据更新成功");
            SpUser.inst().updateProfileRefreshTime();
            Profile.inst().update((Profile) r.getData());
            notify(NotifyType.profile_change);
        }
    }

    @Override
    public void onLiveNotify(@LiveNotifyType int type, Object data) {
        switch (type) {
            case LiveNotifyType.accept: {
                YSLog.d(TAG, "接收到同意进入指令");
                ((IMeetOpt) getCurrItem()).allowEnter();
            }
            break;
            case LiveNotifyType.reject: {
                YSLog.d(TAG, "接收到拒绝进入指令");
                ((IMeetOpt) getCurrItem()).notAllowEnter();
            }
            break;
        }
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.logout) {
            finish();
        } else if (type == NotifyType.token_out_of_date) {
            Intent intent;
            //清除栈里的activity2
            if (Util.checkAppCn()) {
                intent = new Intent(this, AuthLoginActivity.class);
            } else {
                intent = new Intent(this, AuthLoginOverseaActivity.class);
            }
            startActivity(intent);
            finish();
        } else if (type == NotifyType.delete_meeting) {
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
        } else if (type == NotifyType.copy_duplicate) {
            Copy copy = (Copy) data;
            for (Meet meet : mGridFrag.getData()) {
                if (copy.getInt(TCopy.oldId) == meet.getInt(TMeet.id)) {
                    Meet m = (Meet) meet.clone();
                    m.put(TMeet.title, m.getString(TMeet.title) + getString(R.string.duplicate));
                    m.put(TMeet.id, copy.getInt(TCopy.id));
                    mGridFrag.addItem(m);
                    mGridFrag.invalidate();
                    break;
                }
            }
            showToast(R.string.copy_duplicate_success);
        } else if (type == NotifyType.profile_change) {
            mIvAvatar.placeHolder(R.drawable.ic_default_user_header)
                    .url(Profile.inst().getString(TProfile.avatar))
                    .load();
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
