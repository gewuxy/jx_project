package yaya.csp.ui.activity.me;

import android.content.Intent;
import android.graphics.Color;
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
import lib.yy.ui.activity.base.BaseFormActivity;
import yaya.csp.R;
import yaya.csp.dialog.BottomDialog;
import yaya.csp.dialog.HintDialogMain;
import yaya.csp.model.Profile;
import yaya.csp.model.form.Form;
import yaya.csp.model.form.FormType;
import yaya.csp.model.form.text.IntentForm.IntentType;
import yaya.csp.sp.SpUser;
import yaya.csp.ui.activity.me.set.ChangePwdActivity;
import yaya.csp.util.CacheUtil;
import yaya.csp.util.Util;

/**
 * 设置
 *
 * @auther Huoxuyu
 * @since 2017/9/25
 */

public class SettingsActivity extends BaseFormActivity {

    private final String KM = "M";
    private final int KColorNormal = Color.parseColor("#666666");
    private final int KColorCancel = Color.parseColor("#01b557");

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
                .drawable(R.drawable.form_ic_lock)
                .name(R.string.change_pwd))
                .type(IntentType.common)
                .intent(new Intent(this, ChangePwdActivity.class));

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text)
                .related(RelatedId.clear_img_cache)
                .name(R.string.clear_img_cache)
                .text(getFolderSize(CacheUtil.getBmpCacheDir(), CacheUtil.getUploadCacheDir()))
                .textColor(ResLoader.getColor(R.color.text_AF)));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.text)
                .related(RelatedId.clear_sound_cache)
                .name(R.string.clear_sound_cache)
                .text(getFolderSize(CacheUtil.getMeetingSoundCacheDir()))
                .textColor(ResLoader.getColor(R.color.text_AF)));
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
        userExit();
    }

    @Override
    protected void onFormItemClick(View v, int position) {
        @RelatedId int relatedId = getItem(position).getRelated();
        switch (relatedId) {
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
        d.addItem(getString(resId), KColorNormal);
        d.addItem(getString(R.string.cancel), KColorCancel);
        d.show();
    }

    /**
     * 退出账号
     */
    private void userExit() {
        HintDialogMain d = new HintDialogMain(this);
        d.setHint("确定要退出当前账号吗?");
        d.addBlueButton("退出", v -> {
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

//            startActivity(LoginActivity.class);
            finish();
        });
        d.addBlueButton(R.string.cancel);
        d.show();
    }
}
