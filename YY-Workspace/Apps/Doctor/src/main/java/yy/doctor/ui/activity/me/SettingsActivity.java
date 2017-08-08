package yy.doctor.ui.activity.me;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.view.View;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import lib.jg.jpush.SpJPush;
import lib.network.model.NetworkResp;
import lib.wx.WXLoginApi;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.util.FileUtil;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseFormActivity;
import yy.doctor.Constants;
import yy.doctor.Constants.WXType;
import yy.doctor.R;
import yy.doctor.dialog.BottomDialog;
import yy.doctor.dialog.HintDialogMain;
import yy.doctor.dialog.UpdateNoticeDialog;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.form.Form;
import yy.doctor.model.form.FormType;
import yy.doctor.model.form.text.intent.IntentForm.IntentType;
import yy.doctor.model.me.CheckAppVersion;
import yy.doctor.model.me.CheckAppVersion.TCheckAppVersion;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.serv.CommonServ.ReqType;
import yy.doctor.serv.CommonServIntent;
import yy.doctor.sp.SpApp;
import yy.doctor.sp.SpUser;
import yy.doctor.ui.activity.login.LoginActivity;
import yy.doctor.ui.activity.me.set.BindEmailActivity;
import yy.doctor.ui.activity.me.set.BindPhoneActivity;
import yy.doctor.ui.activity.me.set.ChangePwdActivity;
import yy.doctor.util.CacheUtil;
import yy.doctor.util.Util;

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

    private final int KUnBindEmail = 0;
    private final int KUnBindWX = 1;
    private final int KVersion = 2;

    private String mImgSize;
    private String mSoundSize;

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

    private String getFolderSize(String... path) {
        long size = 0;
        try {
            for (String s : path) {
                size += FileUtil.getFolderSize(new File(s));
            }
        } catch (Exception e) {
            YSLog.e(TAG, "getFolderSize", e);
        }
        return size / 1024 / 1024 + KM;
    }

    private String getProfileString(TProfile key) {
        String string = Profile.inst().getString(key);
        if (TextUtil.isEmpty(string)) {
            return getString(R.string.no_binding);
        } else {
            return string;
        }
    }

    @Override
    public void initData() {
        super.initData();

        mImgSize = getFolderSize(CacheUtil.getBmpCacheDir(), CacheUtil.getUploadCacheDir());
        mSoundSize = getFolderSize(CacheUtil.getMeetingSoundCacheDir());

        addItem(Form.create(FormType.text)
                .related(RelatedId.bind_wx)
                .name(R.string.wx_account)
                .text(getProfileString(TProfile.wxNickname)));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.bind_phone)
                .type(IntentType.set_phone)
                .name(R.string.phone_num_account)
                .text(getProfileString(TProfile.mobile)))
                .intent(new Intent(this, BindPhoneActivity.class));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text)
                .related(RelatedId.bind_email)
                .name(R.string.email_account)
                .text(getProfileString(TProfile.username)));

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text_intent)
                .related(RelatedId.change_password)
                .type(IntentType.set_pwd)
                .name(R.string.change_pwd))
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
                .text(mImgSize));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text)
                .related(RelatedId.clear_sound_cache)
                .name(R.string.clear_sound_cache)
                .text(mSoundSize));

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
                HintDialogMain dialog = new HintDialogMain(this);
                dialog.setHint("确定要退出当前登录账号吗?");
                dialog.addButton("退出", v1 -> {
                    dialog.dismiss();

                    CommonServIntent.create()
                            .type(ReqType.logout)
                            .token(Profile.inst().getString(TProfile.token))
                            .start(this);

                    notify(NotifyType.logout);

                    //清空个人信息，把极光绑定改为false 登录后需要重新绑定
                    SpUser.inst().clear();
                    SpJPush.inst().jPushIsRegister(false);
                    Profile.inst().clear();

                    startActivity(LoginActivity.class);
                    finish();
                });
                dialog.addButton("取消", v1 -> dialog.dismiss());
                dialog.show();
            }
            break;
        }
    }

    @Override
    protected void onFormItemClick(View v, int position) {

        @RelatedId int relatedId = getItem(position).getRelated();
        switch (relatedId) {
            case RelatedId.bind_wx: {
                String nickName = Profile.inst().getString(TProfile.wxNickname);
                if (getString(R.string.no_binding).equals(nickName) || TextUtil.isEmpty(nickName)) {
                    // 未绑定
                    WXLoginApi.create(SettingsActivity.this, Constants.KAppId);
                    WXLoginApi.sendReq(WXType.bind);
                } else {
                    // 已绑定
                    HintDialogMain relieveDialog = new HintDialogMain(SettingsActivity.this);
                    relieveDialog.setHint("是否解除绑定微信号");
                    relieveDialog.addButton(R.string.affirm, R.color.text_666, v1 -> {
                        exeNetworkReq(KUnBindWX, NetFactory.bindWX(null));
                        relieveDialog.dismiss();
                    });
                    relieveDialog.addButton(R.string.cancel, R.color.text_666, v1 -> relieveDialog.dismiss());
                    relieveDialog.show();
                }
            }
            break;
            case RelatedId.bind_email: {
                String email = Profile.inst().getString(TProfile.username);
                if (TextUtil.isNotEmpty(email)) {
                    // 已绑定
                    HintDialogMain relieveDialog = new HintDialogMain(SettingsActivity.this);
                    relieveDialog.setHint("是否解除绑定邮箱");
                    relieveDialog.addButton(R.string.affirm, R.color.text_666, v1 -> {
                        exeNetworkReq(KUnBindEmail, NetFactory.unBindEmail());
                        relieveDialog.dismiss();
                    });
                    relieveDialog.addButton(R.string.cancel, R.color.text_666, v1 -> relieveDialog.dismiss());
                    relieveDialog.show();
                } else {
                    startActivity(BindEmailActivity.class);
                }
            }
            break;
            case RelatedId.check_version: {
                exeNetworkReq(KVersion, NetFactory.checkAppVersion());
            }
            break;
            case RelatedId.change_password: {
                startActivity(ChangePwdActivity.class);
            }
            break;
            case RelatedId.clear_img_cache: {
                showDialogClearImgCache();
            }
            break;
            case RelatedId.clear_sound_cache: {
                showDialogClearSoundCache();
            }
            break;
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), CheckAppVersion.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == KVersion) {

            Result<CheckAppVersion> r = (Result<CheckAppVersion>) result;
            if (r.isSucceed()) {
                //保存更新时间
                SpApp.inst().updateAppRefreshTime();
                CheckAppVersion data = r.getData();
                //  判断版本是否需要更新
                if (data != null) {
                    new UpdateNoticeDialog(this, data.getString(TCheckAppVersion.downLoadUrl)).show();
                } else {
                    showToast(R.string.already_latest_version);
                }
            }
        } else if (id == KUnBindWX) {

            Result r = (Result) result;
            if (r.isSucceed()) {
                showToast("解绑成功");
                Profile.inst().put(TProfile.wxNickname, getString(R.string.no_binding));
                getRelatedItem(RelatedId.bind_wx).save(getString(R.string.no_binding), getString(R.string.no_binding));
                refreshRelatedItem(RelatedId.bind_wx);
            } else {
                showToast(r.getError());
            }
        } else {

            Result r = (Result) result;
            if (r.isSucceed()) {
                showToast("解绑成功");
                Profile.inst().put(TProfile.username, getString(R.string.no_binding));
                getRelatedItem(RelatedId.bind_email).save(getString(R.string.no_binding), getString(R.string.no_binding));
                refreshRelatedItem(RelatedId.bind_email);
            } else {
                showToast(r.getError());
            }
        }
    }

    private void showDialogClearImgCache() {

        final BottomDialog dialog = new BottomDialog(this, position -> {

            if (position == 0) {

                Observable.fromCallable(() -> FileUtil.delFolder(CacheUtil.getBmpCacheDir()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                getRelatedItem(RelatedId.clear_img_cache).text("0M");
                                refreshRelatedItem(RelatedId.clear_img_cache);
                                showToast(R.string.clear_img_cache_success);
                            }
                        });
            }
        });
        dialog.addItem(getString(R.string.clear_img_cache), KColorNormal);
        dialog.addItem(getString(R.string.cancel), KColorCancel);

        dialog.show();
    }

    private void showDialogClearSoundCache() {

        final List<String> data = new ArrayList<>();
        data.add(getString(R.string.clear_sound_cache));
        data.add(getString(R.string.cancel));

        final BottomDialog dialog = new BottomDialog(this, position -> {

            if (position == 0) {

                Observable.fromCallable(() -> FileUtil.delFolder(CacheUtil.getMeetingSoundCacheDir()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                getRelatedItem(RelatedId.clear_sound_cache).text("0M");
                                refreshRelatedItem(RelatedId.clear_sound_cache);
                                showToast(getString(R.string.clear_sound_cache_success));
                            }
                        });
            }
        });
        dialog.addItem(getString(R.string.clear_sound_cache), KColorNormal);
        dialog.addItem(getString(R.string.cancel), KColorCancel);

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        WXLoginApi.detach();
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.bind_wx) {
            String wxNickname = (String) data;
            Profile.inst().put(TProfile.wxNickname, wxNickname);
            getRelatedItem(RelatedId.bind_wx).save(wxNickname, wxNickname);
            refreshRelatedItem(RelatedId.bind_wx);
        }
    }
}
