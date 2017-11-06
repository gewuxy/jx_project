package jx.csp.ui.activity.me;

import android.support.annotation.IntDef;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.R;
import jx.csp.contact.SettingsContract;
import jx.csp.dialog.BottomDialog;
import jx.csp.dialog.CommonDialog2;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.def.FormType;
import jx.csp.model.form.Form;
import jx.csp.presenter.SettingsPresenterImpl;
import jx.csp.ui.activity.login.AuthLoginActivity;
import jx.csp.ui.activity.me.bind.BindEmailTipsActivity;
import jx.csp.ui.activity.me.bind.ChangePwdActivity;
import jx.csp.util.CacheUtil;
import jx.csp.util.Util;
import lib.ys.ui.other.NavBar;
import lib.ys.util.FileUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.ui.activity.base.BaseFormActivity;

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

    private SettingsContract.P mPresenter;
    private SettingsContract.V mView;

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

        mView = new SettingsViewImpl();
        mPresenter = new SettingsPresenterImpl(mView);

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text)
                .related(RelatedId.change_password)
                .layout(R.layout.form_text_me)
                .drawable(R.drawable.form_ic_setting_lock)
                .name(R.string.setting_change_pwd));

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text_clear_cache)
                .related(RelatedId.clear_img_cache)
                .name(R.string.setting_clear_img_cache)
                .text(mPresenter.getFolderSize(CacheUtil.getBmpCacheDir(), CacheUtil.getUploadCacheDir()))
                .textColor(ResLoader.getColor(R.color.text_af)));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_clear_cache)
                .related(RelatedId.clear_sound_cache)
                .name(R.string.setting_clear_sound_cache)
                .text(mPresenter.getFolderSize(CacheUtil.getAudioCacheDir()))
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
                mView.logout();
            }
            break;
        }
    }

    @Override
    protected void onFormItemClick(View v, int position) {
        @RelatedId int relatedId = getItem(position).getRelated();
        switch (relatedId) {
            case RelatedId.change_password: {
                mView.changePassWord();
            }
            break;
            case RelatedId.clear_img_cache: {
                mView.clearCache(RelatedId.clear_img_cache, R.string.setting_clear_img_cache, CacheUtil.getBmpCacheDir(), CacheUtil.getUploadCacheDir());
            }
            break;
            case RelatedId.clear_sound_cache: {
                mView.clearCache(RelatedId.clear_sound_cache, R.string.setting_clear_sound_cache, CacheUtil.getAudioCacheDir());
            }
            break;
        }
    }

    private class SettingsViewImpl implements SettingsContract.V {

        @Override
        public void changePassWord() {
            if (TextUtil.isEmpty(Profile.inst().getString(TProfile.email))) {
                startActivity(BindEmailTipsActivity.class);
            } else {
                //已绑定邮箱,直接跳转到修改页面
                startActivity(ChangePwdActivity.class);
            }
        }

        @Override
        public void clearCache(int related, int resId, String... folderPath) {
            BottomDialog d = new BottomDialog(SettingsActivity.this, position -> {

                if (position == 0) {
                    Util.runOnSubThread(() -> {
                        boolean result = true;
                        for (int i = 0; i < folderPath.length; ++i) {
                            if (!FileUtil.delFolder(folderPath[i])) {
                                result = false;
                                break;
                            }
                        }

                        if (result) {
                            runOnUIThread(() -> {
                                getRelatedItem(related).text("0M");
                                refreshRelatedItem(related);
                                showToast(R.string.setting_clear_succeed);
                            });
                        } else {
                            showToast(R.string.setting_clear_error);
                        }
                    });
                }
            });
            d.addItem(getString(resId), ResLoader.getColor(KColorNormal));
            d.addItem(getString(R.string.cancel), ResLoader.getColor(KColorCancel));
            d.show();
        }

        @Override
        public void logout() {
            CommonDialog2 d = new CommonDialog2(SettingsActivity.this);
            d.setHint(getString(R.string.setting_exit_current_account));
            d.addBlackButton(getString(R.string.setting_exit), v -> {
                mPresenter.startLogoutService(SettingsActivity.this);

                startActivity(AuthLoginActivity.class);
                finish();
            });
            d.addBlueButton(R.string.cancel);
            d.show();
        }

        @Override
        public void onStopRefresh() {

        }
    }
}
