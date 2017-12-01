package jx.doctor.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.jg.jpush.SpJPush;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.impl.SingletonImpl;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.view.LayoutUtil;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.activity.base.BaseVpActivity;
import jx.doctor.R;
import jx.doctor.dialog.HintDialog;
import jx.doctor.dialog.UpdateNoticeDialog;
import jx.doctor.model.Profile;
import jx.doctor.model.Profile.TProfile;
import jx.doctor.model.me.CheckAppVersion;
import jx.doctor.model.me.CheckAppVersion.TCheckAppVersion;
import jx.doctor.network.JsonParser;
import jx.doctor.network.NetworkApiDescriptor.CommonAPI;
import jx.doctor.network.NetworkApiDescriptor.UserAPI;
import jx.doctor.serv.CommonServ.ReqType;
import jx.doctor.serv.CommonServRouter;
import jx.doctor.sp.SpApp;
import jx.doctor.sp.SpUser;
import jx.doctor.ui.activity.me.SettingsActivity;
import jx.doctor.ui.activity.user.login.LoginActivity;
import jx.doctor.ui.frag.DataCenterFrag;
import jx.doctor.ui.frag.HomeFrag;
import jx.doctor.ui.frag.MeFrag;
import jx.doctor.ui.frag.MeetingFrag;


@Route
public class MainActivity extends BaseVpActivity {

    public static final int KTabHome = 0;
    public static final int KTabMeeting = 1;
    public static final int KTabData = 2;
    public static final int KTabMe = 3;

    private final int KReqIdProfile = 1;
    private final int KReqIdApp = 2;

    private final int KPermissionCodeLocation = 10;

    private LinearLayout mLayoutTab;
    private View mTabPrev;

    @Arg(opt = true, defaultInt = KTabHome)
    int mCurrPage;

    @Override
    public void initData(Bundle state) {
        add(new HomeFrag());
        add(new MeetingFrag());
        add(new DataCenterFrag());
        add(new MeFrag());
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void findViews() {
        super.findViews();

        mLayoutTab = findView(R.id.main_layout_tab);
    }

    @Override
    public void setViews() {
        super.setViews();

        //检查有没有定位权限
        checkPermission(KPermissionCodeLocation, Permission.location);

        addIndicators();

        setOffscreenPageLimit(getCount());
        setScrollable(false);

        setCurrPosition(mCurrPage);

        // 判断是否已经绑定极光推送
        YSLog.d(TAG, " 是否重新绑定极光推送 " + SpJPush.inst().needRegisterJP());
        YSLog.d(TAG, " 保存的RegistrationId = " + SpJPush.inst().registerId());
        if (SpJPush.inst().needRegisterJP() && !TextUtil.isEmpty(SpJPush.inst().registerId())) {
            CommonServRouter.create()
                    .type(ReqType.j_push)
                    .jPushRegisterId(SpJPush.inst().registerId())
                    .route(this);
            YSLog.d(TAG, "启动绑定极光服务");
        }

        //判断是否需要弹绑定的dialog
        if (TextUtil.isEmpty(Profile.inst().getString(TProfile.mobile)) && TextUtil.isEmpty(Profile.inst().getString(TProfile.wxNickname))) {
            if (SpUser.inst().isShowBind()) {
                showBind();
                SpUser.inst().saveShowBind();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // 静默更新用户数据
        if (SpUser.inst().needUpdateProfile()) {
            exeNetworkReq(KReqIdProfile, UserAPI.profile().build());
        }
        //判断是否需要检查版本
        if (SpApp.inst().needUpdateApp()) {
            exeNetworkReq(KReqIdApp, CommonAPI.checkAppVersion().build());
        }
    }

    private void addIndicators() {

        addIndicator(KTabHome, R.drawable.main_selector_home, getString(R.string.title_home));
        addIndicator(KTabMeeting, R.drawable.main_selector_meeting, getString(R.string.title_meeting));
        addIndicator(KTabData, R.drawable.main_selector_data, getString(R.string.title_data));
        addIndicator(KTabMe, R.drawable.main_selector_me, getString(R.string.title_me));

        setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (mTabPrev != null) {
                    mTabPrev.setSelected(false);
                }
                mTabPrev = mLayoutTab.getChildAt(position);
                mTabPrev.setSelected(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void addIndicator(final int index, @DrawableRes int drawableId, CharSequence text) {
        View v = inflate(R.layout.layout_main_tab);

        ImageView iv = v.findViewById(R.id.main_tab_iv);
        iv.setImageResource(drawableId);

        TextView tv = v.findViewById(R.id.main_tab_tv);
        tv.setText(text);

        v.setOnClickListener(v1 -> setCurrPosition(index, false));

        if (index == KTabHome) {
            v.setSelected(true);
            mTabPrev = v;
        }
        fit(v);

        LayoutParams p = LayoutUtil.getLinearParams(LayoutUtil.MATCH_PARENT, LayoutUtil.MATCH_PARENT);
        p.weight = 1;
        mLayoutTab.addView(v, p);
    }

    @Override
    protected Boolean enableSwipeFinish() {
        return false;
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {

        if (id == KReqIdProfile) {
            if (r.isSucceed()) {
                Profile.inst().update((Profile) r.getData());
                SpUser.inst().updateProfileRefreshTime();

                notify(NotifyType.profile_change);
            }
        } else if (id == KReqIdApp) {
            if (r.isSucceed()) {
                //保存更新时间
                SpApp.inst().updateAppRefreshTime();
                CheckAppVersion data = (CheckAppVersion) r.getData();
                if (data != null) {
                    //  判断版本是否需要更新
                    new UpdateNoticeDialog(this, data.getString(TCheckAppVersion.downLoadUrl)).show();
                }
            }
        }
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        if (id == KReqIdProfile) {
            return JsonParser.ev(resp.getText(), Profile.class);
        } else {
            return JsonParser.ev(resp.getText(), CheckAppVersion.class);
        }
    }

    private void showBind() {
        HintDialog bindDialog = new HintDialog(this);
        bindDialog.addHintView(inflate(R.layout.dialog_binding_phone_or_wx));
        bindDialog.addBlueButton(R.string.cancel);
        bindDialog.addBlueButton(R.string.go_binding, v -> startActivity(SettingsActivity.class)); // 跳转到设置页面
        bindDialog.show();
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {

        if (type == NotifyType.logout) {
            finish();
        } else if (type == NotifyType.token_out_of_date) {
            //清除栈里的activity
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SingletonImpl.inst().freeAll();
    }

    @Override
    public void onBackPressed() {
        if (enableExit()) {
            super.onBackPressed();
        } else {
            showToast("再按一次退出");
        }
    }
}
