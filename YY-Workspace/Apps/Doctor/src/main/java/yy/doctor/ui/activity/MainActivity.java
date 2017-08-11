package yy.doctor.ui.activity;

import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import lib.jg.jpush.SpJPush;
import lib.network.model.NetworkResp;
import lib.processor.annotation.AutoIntent;
import lib.processor.annotation.Extra;
import lib.ys.YSLog;
import lib.ys.impl.SingletonImpl;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.UtilEx;
import lib.ys.util.permission.Permission;
import lib.ys.util.view.LayoutUtil;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseVPActivity;
import yy.doctor.BuildConfig;
import yy.doctor.R;
import yy.doctor.dialog.HintDialog;
import yy.doctor.dialog.UpdateNoticeDialog;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.me.CheckAppVersion;
import yy.doctor.model.me.CheckAppVersion.TCheckAppVersion;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.serv.CommonServ.ReqType;
import yy.doctor.serv.CommonServIntent;
import yy.doctor.sp.SpApp;
import yy.doctor.sp.SpUser;
import yy.doctor.ui.activity.me.SettingsActivity;
import yy.doctor.ui.activity.user.login.LoginActivity;
import yy.doctor.ui.frag.DataCenterFrag;
import yy.doctor.ui.frag.HomeFrag;
import yy.doctor.ui.frag.MeetingFrag;
import yy.doctor.ui.frag.me.MeFrag;


@AutoIntent
public class MainActivity extends BaseVPActivity {

    public static final int KTabHome = 0;
    public static final int KTabMeeting = 1;
    public static final int KTabData = 2;
    public static final int KTabMe = 3;

    private final int KReqIdProfile = 1;
    private final int KReqIdApp = 2;

    private final int KPermissionCodeLocation = 10;

    private LinearLayout mLayoutTab;
    private View mTabPrev;

    @Extra(optional = true, defaultInt = KTabHome)
    int mCurrPage;

    @Override
    public void initData() {
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

        YSLog.d(TAG, "wucaixiang@medcn.cn md5 =  " + UtilEx.md5("wucaixiang@medcn.cn"));

        //检查有没有定位权限
        checkPermission(KPermissionCodeLocation, Permission.location);

        addIndicators();

        setOffscreenPageLimit(getCount());
        setScrollable(false);

        setCurrentItem(mCurrPage);

        if (BuildConfig.TEST) {
            // 判断是否已经绑定极光推送
            YSLog.d(TAG, " 是否重新绑定极光推送 " + SpJPush.inst().needRegisterJP());
            YSLog.d(TAG, " 保存的RegistrationId = " + SpJPush.inst().registerId());
            if (SpJPush.inst().needRegisterJP() && !TextUtil.isEmpty(SpJPush.inst().registerId())) {
                CommonServIntent.create()
                        .type(ReqType.j_push)
                        .jPushRegisterId(SpJPush.inst().registerId())
                        .start(this);
                YSLog.d(TAG, "启动绑定极光服务");
            }
        } else {
            // 判断是否已经绑定极光推送
            YSLog.d(TAG, " 是否重新绑定极光推送 " + SpJPush.inst().needRegisterJP());
            YSLog.d(TAG, " 保存的RegistrationId = " + SpJPush.inst().registerId());
            if (SpJPush.inst().needRegisterJP() && !TextUtil.isEmpty(SpJPush.inst().registerId())) {
                CommonServIntent.create()
                        .type(ReqType.j_push)
                        .jPushRegisterId(SpJPush.inst().registerId())
                        .start(this);
                YSLog.d(TAG, "启动绑定极光服务");
            }
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
            exeNetworkReq(KReqIdProfile, NetFactory.profile());
        }
        //判断是否需要检查版本
        if (SpApp.inst().needUpdateApp()) {
            exeNetworkReq(KReqIdApp, NetFactory.checkAppVersion());
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

        ImageView iv = (ImageView) v.findViewById(R.id.main_tab_iv);
        iv.setImageResource(drawableId);

        TextView tv = (TextView) v.findViewById(R.id.main_tab_tv);
        tv.setText(text);

        v.setOnClickListener(v1 -> setCurrentItem(index, false));

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
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == KReqIdProfile) {
            return JsonParser.ev(r.getText(), Profile.class);
        } else {
            return JsonParser.ev(r.getText(), CheckAppVersion.class);
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {

        if (id == KReqIdProfile) {
            Result<Profile> r = (Result<Profile>) result;
            if (r.isSucceed()) {
                Profile.inst().update(r.getData());
                SpUser.inst().updateProfileRefreshTime();

                notify(NotifyType.profile_change);
            }
        } else if (id == KReqIdApp) {
            Result<CheckAppVersion> r = (Result<CheckAppVersion>) result;
            if (r.isSucceed()) {
                //保存更新时间
                SpApp.inst().updateAppRefreshTime();
                CheckAppVersion data = r.getData();
                if (data != null) {
                    //  判断版本是否需要更新
                    new UpdateNoticeDialog(this, data.getString(TCheckAppVersion.downLoadUrl)).show();
                }
            }
        }
    }

    private void showBind() {
        HintDialog bindDialog = new HintDialog(this);
        bindDialog.addHintView(inflate(R.layout.dialog_binding_phone_or_wx));
        bindDialog.addButton(R.string.cancel, v -> bindDialog.dismiss());
        bindDialog.addButton(R.string.go_binding, v -> {
            //跳转到设置页面
            startActivity(SettingsActivity.class);
            bindDialog.dismiss();
        });
        bindDialog.show();
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {

        if (type == NotifyType.logout || type == NotifyType.exit) {
            finish();
        } else if (type == NotifyType.token_out_of_date) {
            startActivity(LoginActivity.class);
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
