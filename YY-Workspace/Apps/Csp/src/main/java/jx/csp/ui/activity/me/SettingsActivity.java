package jx.csp.ui.activity.me;

import android.support.annotation.IntDef;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.R;
import jx.csp.constant.FormType;
import jx.csp.contact.SettingsContract;
import jx.csp.dialog.CommonDialog2;
import jx.csp.model.form.Form;
import jx.csp.presenter.SettingsPresenterImpl;
import jx.csp.util.Util;
import lib.jx.ui.activity.base.BaseFormActivity;
import lib.ys.action.IntentAction;
import lib.ys.ui.other.NavBar;

/**
 * 设置
 *
 * @auther HuoXuYu
 * @since 2017/9/25
 */

public class SettingsActivity extends BaseFormActivity {

    private SettingsContract.P mPresenter;
    private SettingsContract.V mView;

    @IntDef({
            RelatedId.multi_language,
            RelatedId.change_password,
            RelatedId.clear_cache,
            RelatedId.feedback,
            RelatedId.about_csp,
            RelatedId.operating_guide,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface RelatedId {
        int multi_language = 0;
        int change_password = 1;
        int clear_cache = 2;
        int feedback = 3;
        int about_csp = 4;
        int operating_guide = 5;
    }

    @Override
    public void initData() {
        super.initData();

        mView = new SettingsViewImpl();
        mPresenter = new SettingsPresenterImpl(mView);

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text)
                .related(RelatedId.multi_language)
                .name(R.string.multi_language));

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text)
                .related(RelatedId.change_password)
                .name(R.string.setting_change_pwd));
        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text)
                .related(RelatedId.clear_cache)
                .name(R.string.clear_cache));

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text)
                .related(RelatedId.feedback)
                .name(R.string.help_and_feedback_opinion_feedback));
        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text)
                .related(RelatedId.about_csp)
                .name(R.string.about_csp));

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.text)
                .related(RelatedId.operating_guide)
                .name(R.string.operating_guide));
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
                mView.logoutDialog();
            }
            break;
        }
    }

    @Override
    protected void onFormItemClick(View v, int position) {
        @RelatedId int relatedId = getItem(position).getRelated();
        switch (relatedId) {
            case RelatedId.multi_language: {
                showToast("0");
            }
            break;
            case RelatedId.change_password: {
                mPresenter.changePassWord();
            }
            break;
            case RelatedId.clear_cache: {
                startActivity(ClearCacheActivity.class);
            }
            break;
            case RelatedId.feedback: {
                IntentAction.mail()
                        .address("app@medcn.cn")
                        .subject(R.string.help_and_feedback_email_subject)
                        .alert("没有邮件类应用")
                        .launch();
            }
            break;
            case RelatedId.about_csp: {
                showToast("3");
            }
            break;
            case RelatedId.operating_guide: {
                showToast("4");
            }
            break;
        }
    }

    private class SettingsViewImpl implements SettingsContract.V {

        @Override
        public void logoutDialog() {
            CommonDialog2 d = new CommonDialog2(SettingsActivity.this);
            d.setHint(R.string.setting_exit_current_account);
            d.addBlackButton(R.string.setting_exit, v -> {
                mPresenter.logout(SettingsActivity.this);
            });
            d.addBlackButton(R.string.cancel);
            d.show();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }
}
