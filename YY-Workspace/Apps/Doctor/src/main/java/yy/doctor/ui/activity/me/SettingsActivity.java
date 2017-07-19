package yy.doctor.ui.activity.me;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.TextView;

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
import lib.ys.YSLog;
import lib.ys.form.FormEx.TForm;
import lib.ys.ui.other.NavBar;
import lib.ys.util.FileUtil;
import lib.ys.view.ToggleButton;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseFormActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.BottomDialog;
import yy.doctor.dialog.CommonDialog;
import yy.doctor.dialog.UpdateNoticeDialog;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;
import yy.doctor.model.me.CheckAppVersion;
import yy.doctor.model.me.CheckAppVersion.TCheckAppVersion;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.serv.CommonServ;
import yy.doctor.serv.CommonServ.ReqType;
import yy.doctor.sp.SpApp;
import yy.doctor.sp.SpUser;
import yy.doctor.ui.activity.login.LoginActivity;
import yy.doctor.util.CacheUtil;
import yy.doctor.util.Util;

/**
 * 设置页面
 *
 * @author CaiXiang
 * @since 2017/4/12
 */
public class SettingsActivity extends BaseFormActivity {

    private static final int KColorNormal = Color.parseColor("#666666");
    private static final int KColorCancel = Color.parseColor("#01b557");

    private static final String KM = "M";

    private ToggleButton mTog;
    private TextView mTvExit;

    private String mImgSize;
    private String mSoundSize;

    @IntDef({
            RelatedId.check_version,

            RelatedId.change_password,
            RelatedId.clear_img_cache,

            RelatedId.clear_sound_cache,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int check_version = 1;

        int change_password = 2;

        int clear_img_cache = 3;
        int clear_sound_cache = 4;
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

    @Override
    public void initData() {
        super.initData();

        mImgSize = getFolderSize(CacheUtil.getBmpCacheDir(), CacheUtil.getUploadCacheDir());
        mSoundSize = getFolderSize(CacheUtil.getMeetingSoundCacheDir());

        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.content_text)
                .related(RelatedId.check_version)
                .name(R.string.check_version)
                .build());

        addItem(new Builder(FormType.divider_large).build());
        addItem(new Builder(FormType.content_text)
                .related(RelatedId.change_password)
                .name(R.string.change_pwd)
                .build());

        addItem(new Builder(FormType.divider_large).build());
        addItem(new Builder(FormType.content_text)
                .name(R.string.clear_img_cache)
                .related(RelatedId.clear_img_cache)
                .text(mImgSize)
                .build());

        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.content_text)
                .name(R.string.clear_sound_cache)
                .related(RelatedId.clear_sound_cache)
                .text(mSoundSize)
                .build());

        addItem(new Builder(FormType.divider_large).build());
    }

    @Override
    protected View createHeaderView() {
        return inflate(R.layout.layout_settings_header);
    }

    @Override
    protected View createFooterView() {
        return inflate(R.layout.layout_settings_footer);
    }

    @Override
    public void findViews() {
        super.findViews();

        mTog = findView(R.id.settings_header_switcher);
        mTvExit = findView(R.id.settings_footer_tv_exit_account);
    }

    @Override
    public void setViews() {
        super.setViews();

        mTog.setToggleState(true);
        mTvExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.settings_footer_tv_exit_account: {
                showDialogExit();
            }
            break;
        }
    }

    @Override
    protected void onFormItemClick(View v, int position) {
        super.onFormItemClick(v, position);

        @RelatedId int relatedId = getItem(position).getInt(TForm.related);
        switch (relatedId) {
            case RelatedId.check_version: {
                exeNetworkReq(NetFactory.checkAppVersion());
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
    }

    private void showDialogExit() {

        final CommonDialog dialog = new CommonDialog(this);
        dialog.addItem(getString(R.string.exit_account), v -> {

            dialog.dismiss();

            Intent intent = new Intent(SettingsActivity.this, CommonServ.class);
            intent.putExtra(Extra.KType, ReqType.logout);
            intent.putExtra(Extra.KData, Profile.inst().getString(TProfile.token));
            startService(intent);

            SettingsActivity.this.notify(NotifyType.logout);

            //清空个人信息，把极光绑定改为false 登录后需要重新绑定
            SpUser.inst().clear();
            SpJPush.inst().jPushIsRegister(false);
            Profile.inst().clear();

            startActivity(LoginActivity.class);
            finish();
        });

        dialog.addItem(getString(R.string.close_app), v -> {
            dialog.dismiss();
            SettingsActivity.this.notify(NotifyType.exit);
            finish();
        });
        dialog.show();
    }

    private void showDialogClearImgCache() {

        final BottomDialog dialog = new BottomDialog(this, position -> {

            if (position == 0) {

                Observable.fromCallable(() -> FileUtil.delFolder(CacheUtil.getBmpCacheDir()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                getRelatedItem(RelatedId.clear_img_cache).put(TForm.text, "0M");
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
                                getRelatedItem(RelatedId.clear_sound_cache).put(TForm.text, "0M");
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

}
