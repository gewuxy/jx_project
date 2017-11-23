package jx.csp.ui.activity.me;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.R;
import jx.csp.constant.FormType;
import jx.csp.contact.SettingsContract;
import jx.csp.model.form.Form;
import jx.csp.presenter.SettingsPresenterImpl;
import jx.csp.util.CacheUtil;
import jx.csp.util.Util;
import lib.ys.ui.other.NavBar;
import lib.ys.util.res.ResLoader;
import lib.yy.ui.activity.base.BaseFormActivity;

/**
 * 设置
 *
 * @auther Huoxuyu
 * @since 2017/9/25
 */

public class SettingsActivity extends BaseFormActivity {

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
    public void initData(Bundle state) {
        super.initData(state);

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
                mPresenter.logout(SettingsActivity.this);
            }
            break;
        }
    }

    @Override
    protected void onFormItemClick(View v, int position) {
        @RelatedId int relatedId = getItem(position).getRelated();
        switch (relatedId) {
            case RelatedId.change_password: {
                mPresenter.changePassWord();
            }
            break;
            case RelatedId.clear_img_cache: {
                mPresenter.clearCache(SettingsActivity.this, RelatedId.clear_img_cache, getString(R.string.setting_clear_img_cache), getString(R.string.cancel), CacheUtil.getBmpCacheDir(), CacheUtil.getUploadCacheDir());
            }
            break;
            case RelatedId.clear_sound_cache: {
                mPresenter.clearCache(SettingsActivity.this, RelatedId.clear_sound_cache, getString(R.string.setting_clear_sound_cache), getString(R.string.cancel), CacheUtil.getAudioCacheDir());
            }
            break;
        }
    }

    private class SettingsViewImpl implements SettingsContract.V {

        @Override
        public void refreshItem(int related) {
            getRelatedItem(related).text("0M");
            refreshRelatedItem(related);
        }

        @Override
        public void closePage() {
            finish();
        }

        @Override
        public void onStopRefresh() {
        }

        @Override
        public void setViewState(int state) {
        }
    }
}
