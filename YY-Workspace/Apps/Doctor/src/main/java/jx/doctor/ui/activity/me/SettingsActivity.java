package jx.doctor.ui.activity.me;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.View.OnClickListener;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import lib.jg.jpush.SpJPush;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.ConstantsEx;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.FileUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.activity.base.BaseFormActivity;
import jx.doctor.Constants;
import jx.doctor.R;
import jx.doctor.dialog.BottomDialog;
import jx.doctor.dialog.HintDialogMain;
import jx.doctor.dialog.HintDialogSec;
import jx.doctor.dialog.UpdateNoticeDialog;
import jx.doctor.model.Profile;
import jx.doctor.model.Profile.TProfile;
import jx.doctor.model.form.Form;
import jx.doctor.model.form.FormType;
import jx.doctor.model.form.text.intent.IntentForm.IntentType;
import jx.doctor.model.me.CheckAppVersion;
import jx.doctor.model.me.CheckAppVersion.TCheckAppVersion;
import jx.doctor.network.JsonParser;
import jx.doctor.network.NetworkApiDescriptor.CommonAPI;
import jx.doctor.network.NetworkApiDescriptor.UserAPI;
import jx.doctor.serv.CommonServ.ReqType;
import jx.doctor.serv.CommonServRouter;
import jx.doctor.sp.SpApp;
import jx.doctor.sp.SpUser;
import jx.doctor.ui.activity.me.set.BindEmailActivity;
import jx.doctor.ui.activity.me.set.BindPhoneActivity;
import jx.doctor.ui.activity.me.set.ChangePwdActivity;
import jx.doctor.ui.activity.user.login.LoginActivity;
import jx.doctor.ui.activity.user.login.WXLoginApi;
import jx.doctor.util.CacheUtil;
import jx.doctor.util.Util;

/**
 * 设置页面
 *
 * @author CaiXiang
 * @since 2017/4/12
 */
public class SettingsActivity extends BaseFormActivity {

    private final String KM = "M";

    private final int KColorNormal = Color.parseColor("#666666");
    private final int KColorCancel = Color.parseColor("#01b557");

    private final int KUnBindEmail = 0; // 解绑邮箱
    private final int KUnBindWX = 1; // 解绑微信
    private final int KVersion = 2; // 检查版本号

    @IntDef({
            RelatedId.bind_wx,
            RelatedId.bind_phone,
            RelatedId.bind_email,

            RelatedId.change_password,
            RelatedId.auto_download_apk,
            RelatedId.check_version,

            RelatedId.clear_img_cache,
            RelatedId.clear_sound_cache,

            RelatedId.audio_play,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int bind_wx = 1;
        int bind_phone = 2;
        int bind_email = 3;

        int change_password = 4;
        int auto_download_apk = 5;
        int check_version = 6;

        int clear_img_cache = 7;
        int clear_sound_cache = 8;

        int audio_play = 9;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.settings, this);
    }

    @Override
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.text)
                .related(RelatedId.bind_wx)
                .name(R.string.wx_account)
                .text(Profile.inst().getString(TProfile.wxNickname))
                .textColor(ResLoader.getColor(R.color.text_b3))
                .hint(R.string.no_bind)
                .hintTextColor(ResLoader.getColor(R.color.text_b3)));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.bind_phone)
                .name(R.string.phone_num_account)
                .text(Profile.inst().getString(TProfile.mobile))
                .textColor(ResLoader.getColor(R.color.text_b3))
                .hint(R.string.no_bind)
                .hintTextColor(ResLoader.getColor(R.color.text_b3)));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text)
                .related(RelatedId.bind_email)
                .name(R.string.email_account)
                .text(Profile.inst().getString(TProfile.username))
                .textColor(ResLoader.getColor(R.color.text_b3))
                .hint(R.string.no_bind)
                .hintTextColor(ResLoader.getColor(R.color.text_b3)));

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.change_password)
                .name(R.string.change_pwd))
                .type(IntentType.common)
                .intent(new Intent(this, ChangePwdActivity.class));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.toggle_button)
                .related(RelatedId.auto_download_apk)
                .name(R.string.wifi_auto_download_new_apk));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text)
                .related(RelatedId.check_version)
                .name(R.string.check_version));

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text)
                .related(RelatedId.clear_img_cache)
                .name(R.string.clear_img_cache)
                .text(getFolderSize(CacheUtil.getBmpCacheDir(), CacheUtil.getUploadCacheDir()))
                .textColor(ResLoader.getColor(R.color.text_b3)));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text)
                .related(RelatedId.clear_sound_cache)
                .name(R.string.clear_sound_cache)
                .text(getFolderSize(CacheUtil.getMeetingSoundCacheDir()))
                .textColor(ResLoader.getColor(R.color.text_b3)));

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.toggle_button)
                .related(RelatedId.audio_play)
                .name(R.string.audio_auto_play_only_wifi));
    }

    @Override
    protected View createFooterView() {
        return inflate(R.layout.layout_settings_footer);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.settings_footer_tv_exit_account);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_footer_tv_exit_account: {
                userExit();
            }
            break;
        }
    }

    @Override
    protected void onFormItemClick(View v, int position) {
        @RelatedId int relatedId = getItem(position).getRelated();
        switch (relatedId) {
            case RelatedId.bind_wx: {
                if (TextUtil.isEmpty(Profile.inst().getString(TProfile.wxNickname))) {
                    // 未绑定

                    if (Util.noNetwork()) {
                        return;
                    }

                    WXLoginApi.create(SettingsActivity.this, Constants.KAppId);
                    if (WXLoginApi.isWXAppInstalled()) {
                        WXLoginApi.sendReq(Constants.WXType.bind);
                    } else {
                        notInstallWx();
                    }
                } else {
                    // 已绑定
                    unBind("是否解除绑定微信号", v1 -> {
                        if (Util.noNetwork()) {
                            return;
                        }
                        refresh(RefreshWay.dialog);
                        exeNetworkReq(KUnBindWX, UserAPI.bindWX().build());
                    });
                }
            }
            break;
            case RelatedId.bind_phone: {
                if (TextUtil.isEmpty(Profile.inst().getString(TProfile.mobile))) {
                    startActivity(BindPhoneActivity.class);
                } else {
                    // 已绑定
                    unBind("是否更换绑定的手机号码？", v1 -> startActivity(BindPhoneActivity.class));
                }
            }
            break;
            case RelatedId.bind_email: {
                if (TextUtil.isEmpty(Profile.inst().getString(TProfile.username))) {
                    startActivity(BindEmailActivity.class);
                } else {
                    // 已绑定
                    unBind("是否解除绑定邮箱", v1 -> {
                        if (Util.noNetwork()) {
                            return;
                        }
                        refresh(RefreshWay.dialog);
                        exeNetworkReq(KUnBindEmail, UserAPI.unBindEmail().build());
                    });
                }
            }
            break;
            case RelatedId.check_version: {
                if (Util.noNetwork()) {
                    return;
                }
                refresh(RefreshWay.dialog);
                exeNetworkReq(KVersion, CommonAPI.checkAppVersion().build());
            }
            break;
            case RelatedId.clear_img_cache: {
                clearCache(CacheUtil.getBmpCacheDir(), RelatedId.clear_img_cache, R.string.clear_img_cache);
            }
            break;
            case RelatedId.clear_sound_cache: {
                clearCache(CacheUtil.getMeetingSoundCacheDir(), RelatedId.clear_sound_cache, R.string.clear_sound_cache);
            }
            break;
        }
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        if (id == KVersion) {
            return JsonParser.ev(resp.getText(), CheckAppVersion.class);
        } else {
            return JsonParser.error(resp.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        stopRefresh();
        if (id == KVersion) {

            if (r.isSucceed()) {
                //保存更新时间
                SpApp.inst().updateAppRefreshTime();
                CheckAppVersion data = (CheckAppVersion) r.getData();
                //  判断版本是否需要更新
                if (data != null) {
                    new UpdateNoticeDialog(this, data.getString(TCheckAppVersion.downLoadUrl)).show();
                } else {
                    showToast(R.string.already_latest_version);
                }
            }
        } else {
            // 绑定的
            if (id == KUnBindWX) {
                unBindUpdate(r, RelatedId.bind_wx, TProfile.wxNickname);
            } else {
                unBindUpdate(r, RelatedId.bind_email, TProfile.username);
            }
        }
    }

    /**
     * 获取路径下的文件大小
     *
     * @param path
     * @return
     */
    private String getFolderSize(String... path) {
        float size = 0;
        try {
            for (String s : path) {
                size += FileUtil.getFolderSize(new File(s));
            }
        } catch (Exception e) {
            YSLog.e(TAG, "getFolderSize", e);
        }
        size /= (2 << 19);
        if (size >= 0.1f) {
            return String.format("%.1f".concat(KM), size);
        } else {
            return 0 + KM;
        }
    }

    /**
     * 解绑成功
     */
    private void unBindUpdate(IResult r, @RelatedId int id, TProfile key) {
        if (r.isSucceed()) {
            showToast("解绑成功");
            getRelatedItem(id).save(ConstantsEx.KEmpty, ConstantsEx.KEmpty);
            refreshRelatedItem(id);
            Profile.inst().put(key, ConstantsEx.KEmpty);
            Profile.inst().saveToSp();
        } else {
            showToast(r.getMessage());
        }
    }

    /**
     * 没有安装微信
     */
    private void notInstallWx() {
        HintDialogSec dialog = new HintDialogSec(SettingsActivity.this);
        dialog.setMainHint(R.string.wx_accredit_error);
        dialog.setSecHint(R.string.wx_check_normal);
        dialog.addBlueButton(R.string.affirm);
        dialog.show();
    }

    /**
     * 解绑 / 换绑
     */
    private void unBind(CharSequence hint, OnClickListener l) {
        HintDialogMain d = new HintDialogMain(SettingsActivity.this);
        d.setHint(hint);
        d.addButton(R.string.affirm, R.color.text_666, l);
        d.addGrayButton(R.string.cancel);
        d.show();
    }

    /**
     * 清除缓存
     */
    private void clearCache(String folderPath, @RelatedId int related, @StringRes int resId) {
        BottomDialog d = new BottomDialog(this, position -> {

            if (position == 0) {
                Observable.fromCallable(() -> FileUtil.delFolder(folderPath))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aBoolean -> {
                            if (aBoolean && !isFinishing()) {
                                getRelatedItem(related).text("0M");
                                refreshRelatedItem(related);
                            }
                        });
            }

        });
        d.addItem(getString(resId), KColorNormal);
        d.addItem(getString(R.string.cancel), KColorCancel);
        d.show();
    }

    /**
     * 退出账号
     */
    private void userExit() {
        HintDialogMain d = new HintDialogMain(this);
        d.setHint("确定要退出当前登录账号吗?");
        d.addBlueButton("退出", v -> {
            CommonServRouter.create()
                    .type(ReqType.logout)
                    .token(Profile.inst().getString(TProfile.token))
                    .route(this);

            notify(NotifyType.logout);

            //清空个人信息，把极光绑定改为false 登录后需要重新绑定
            SpUser.inst().clear();
            SpJPush.inst().jPushIsRegister(false);
            Profile.inst().clear();

            startActivity(LoginActivity.class);
            finish();
        });
        d.addBlueButton(R.string.cancel);
        d.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        WXLoginApi.detach();
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.bind_wx) {
            bindSuccess((String) data, RelatedId.bind_wx);
        } else if (type == NotifyType.bind_phone) {
            bindSuccess((String) data, RelatedId.bind_phone);
        } else if (type == NotifyType.bind_email) {
            String email = Profile.inst().getString(TProfile.username);
            getRelatedItem(RelatedId.bind_email).save(email, email);
            refreshRelatedItem(RelatedId.bind_email);
        }
    }

    private void bindSuccess(String data, @RelatedId int id) {
        getRelatedItem(id).save(data, data);
        refreshRelatedItem(id);
        showToast("绑定成功");
    }
}
