package jx.csp.ui.activity.me;

import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.view.View;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import lib.jg.jpush.SpJPush;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.util.FileUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseFormActivity;
import jx.csp.R;
import jx.csp.dialog.BottomDialog;
import jx.csp.dialog.HintDialogMain;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.form.Form;
import jx.csp.model.form.FormType;
import jx.csp.sp.SpUser;
import jx.csp.ui.activity.me.set.ChangePwdActivity;
import jx.csp.util.CacheUtil;
import jx.csp.util.Util;

/**
 * 设置
 *
 * @auther Huoxuyu
 * @since 2017/9/25
 */

public class SettingsActivity extends BaseFormActivity {

    private final String KM = "M";
    private final int KColorNormal = R.color.text_666;
    private final int KColorCancel = R.color.text_167afe;

    @IntDef({
            RelatedId.change_password,
            RelatedId.clear_img_cache,
            RelatedId.clear_sound_cache,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int change_password = 1;
        int clear_img_cache = 2;
        int clear_sound_cache = 3;
    }

    @Override
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text_intent_me)
                .related(RelatedId.change_password)
                .drawable(R.drawable.form_ic_setting_lock)
                .name(R.string.setting_change_pwd));

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text)
                .related(RelatedId.clear_img_cache)
                .layout(R.layout.form_text_clear_cache)
                .name(R.string.setting_clear_img_cache)
                .text(getFolderSize(CacheUtil.getBmpCacheDir(), CacheUtil.getUploadCacheDir()))
                .textColor(ResLoader.getColor(R.color.text_af)));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text)
                .related(RelatedId.clear_sound_cache)
                .layout(R.layout.form_text_clear_cache)
                .name(R.string.setting_clear_sound_cache)
                .text(getFolderSize(CacheUtil.getAudioCacheDir()))
                .textColor(ResLoader.getColor(R.color.text_af)));
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.setting, this);
    }

    @Override
    protected View createFooterView() {
        return inflate(R.layout.activity_setting);
    }

    @Override
    public void setViews() {
        super.setViews();
        setOnClickListener(R.id.setting_tv_exit_account);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_tv_exit_account: {
                userExit();
            }
            break;
        }
    }

    @Override
    protected void onFormItemClick(View v, int position) {
        @RelatedId int relatedId = getItem(position).getRelated();
        switch (relatedId) {
            case RelatedId.change_password: {
                // FIXME: 2017/9/25 修改密码的判断
//                if (TextUtil.isEmpty(Profile.inst().getString(TProfile.email))) {
//                    startActivity(BindEmailJumpActivity.class);
//                }else {
//                    //已绑定邮箱,直接跳转到修改页面
                    startActivity(ChangePwdActivity.class);
//                }
            }
            break;
            case RelatedId.clear_img_cache: {
                clearCache(CacheUtil.getBmpCacheDir(), RelatedId.clear_img_cache, R.string.setting_clear_img_cache);
            }
            break;
            case RelatedId.clear_sound_cache: {
                clearCache(CacheUtil.getAudioCacheDir(), RelatedId.clear_sound_cache, R.string.setting_clear_sound_cache);
            }
            break;
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
        d.addItem(getString(resId), ResLoader.getColor(KColorNormal));
        d.addItem(getString(R.string.cancel), ResLoader.getColor(KColorCancel));
        d.show();
    }

    /**
     * 退出账号
     */
    private void userExit() {
        HintDialogMain d = new HintDialogMain(this);
        d.setHint(getString(R.string.setting_exit_current_account));
        d.addBlackButton(getString(R.string.setting_exit), v -> {
//            CommonServRouter.create()
//                    .type(ReqType.logout)
//                    .token(Profile.inst().getString(TProfile.token))
//                    .route(this);
//
//            notify(NotifyType.logout);

            //清空个人信息，把极光绑定改为false 登录后需要重新绑定
            SpUser.inst().clear();
            SpJPush.inst().jPushIsRegister(false);
            Profile.inst().clear();

            finish();
        });
        d.addBlueButton(R.string.cancel);
        d.show();
    }

    @Override
    public void onNotify(int type, Object data) {
        if (type == NotifyType.bind_email) {
            String email = Profile.inst().getString(TProfile.userName);
            getRelatedItem(RelatedId.change_password).save(email, email);
            refreshRelatedItem(RelatedId.change_password);
        }
    }
}
