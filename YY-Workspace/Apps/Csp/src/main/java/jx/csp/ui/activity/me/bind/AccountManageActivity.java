package jx.csp.ui.activity.me.bind;

import android.support.annotation.IntDef;
import android.view.View;
import android.view.View.OnClickListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.Constants.LoginType;
import jx.csp.R;
import jx.csp.contact.AccountManageContract;
import jx.csp.dialog.CommonDialog2;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.authorize.PlatformAuthorizeUserInfoManager;
import jx.csp.model.form.Form;
import jx.csp.model.def.FormType;
import jx.csp.presenter.AccountManagePresenterImpl;
import jx.csp.util.Util;
import lib.ys.ConstantsEx;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseFormActivity;

/**
 * 账号管理
 *
 * @auther Huoxuyu
 * @since 2017/9/26
 */
public class AccountManageActivity extends BaseFormActivity {

    // TODO: 等待王兰完成
    private PlatformAuthorizeUserInfoManager mPlatAuth;

    private AccountManageContract.P mPresenter;
    private AccountManageContract.V mView;

    @IntDef({
            RelatedId.bind_wx,
            RelatedId.bind_sina,
            RelatedId.bind_facebook,
            RelatedId.bind_twitter,
            RelatedId.bind_jingxin,
            RelatedId.bind_phone,
            RelatedId.bind_email,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface RelatedId {
        int bind_wx = 1;
        int bind_sina = 2;
        int bind_facebook = 3;
        int bind_twitter = 4;
        int bind_jingxin = 5;
        int bind_phone = 6;
        int bind_email = 7;
    }

    @Override
    public void initData() {
        super.initData();
        mPlatAuth = new PlatformAuthorizeUserInfoManager();

        mView = new AccountManageViewImpl();
        mPresenter = new AccountManagePresenterImpl(mView);

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_bind)
                .related(RelatedId.bind_phone)
                .name(R.string.account_phone)
                .drawable(R.drawable.form_ic_account_phone)
                .text(Profile.inst().getString(TProfile.mobile))
                .hint(R.string.account_not_bind));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_bind)
                .related(RelatedId.bind_wx)
                .name(R.string.account_wx)
                .drawable(R.drawable.form_ic_account_wx)
                .text(Profile.inst().getString(TProfile.wx))
                .hint(R.string.account_not_bind));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_bind)
                .related(RelatedId.bind_sina)
                .name(R.string.account_sina)
                .drawable(R.drawable.form_ic_account_sina)
                .text(Profile.inst().getString(TProfile.sina))
                .hint(R.string.account_not_bind));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_bind)
                .related(RelatedId.bind_email)
                .name(R.string.account_email)
                .drawable(R.drawable.form_ic_account_email)
                .text(Profile.inst().getString(TProfile.email))
                .hint(R.string.account_not_bind));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_bind)
                .related(RelatedId.bind_twitter)
                .name(R.string.account_twitter)
                .drawable(R.drawable.form_ic_account_twitter)
                .text(Profile.inst().getString(TProfile.twitter))
                .hint(R.string.account_not_bind));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_bind)
                .related(RelatedId.bind_jingxin)
                .name(R.string.account_jingxin)
                .drawable(R.drawable.form_ic_account_jingxin)
                .text(Profile.inst().getBindNickName(LoginType.yaya_login))
                .hint(R.string.account_not_bind));
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, getString(R.string.account_manage), this);
    }

    @Override
    protected void onFormItemClick(View v, int position) {
        @RelatedId int relatedId = getItem(position).getRelated();
        switch (relatedId) {
            case RelatedId.bind_phone: {
                mView.judgeBindStatus(RelatedId.bind_phone, getString(R.string.account_unbind_phone), TProfile.mobile);
            }
            break;
            case RelatedId.bind_email: {
                // FIXME: 2017/10/23 测试极光推送，接受绑定消息
                mView.judgeBindStatus(RelatedId.bind_email, getString(R.string.account_unbind_email), TProfile.email);
            }
            break;
            case RelatedId.bind_wx: {

            }
            break;
            case RelatedId.bind_sina: {

            }
            break;
            case RelatedId.bind_twitter: {
                refresh(RefreshWay.dialog);
                mPresenter.changeThirdPartyBindStatus(RelatedId.bind_twitter,
                        RelatedId.bind_twitter,
                        Profile.inst().getString(TProfile.id),
                        Profile.inst().getString(TProfile.nickName),
                        Profile.inst().getString(TProfile.gender),
                        Profile.inst().getString(TProfile.avatar),
                        TProfile.twitter,
                        getString(R.string.account_unbind_twitter));
            }
            break;
            case RelatedId.bind_jingxin: {
                mView.judgeBindStatus(RelatedId.bind_jingxin, getString(R.string.account_unbind_yaya), TProfile.bindInfoList);
            }
            break;
        }
    }

    // TODO: 2017/10/26 未完成, 差账号授权
    @Override
    public void onNotify(@NotifyType int type, Object data) {
        switch (type) {
            case NotifyType.bind_phone: {
                mView.bindSuccess((String) data, RelatedId.bind_phone);
            }
            break;
            case NotifyType.bind_wx: {
                mView.bindSuccess((String) data, RelatedId.bind_wx);
            }
            break;
            case NotifyType.bind_email: {
                mView.bindSuccess(Profile.inst().getString(TProfile.email), RelatedId.bind_email);
            }
            break;
            case NotifyType.bind_yaya: {
                mView.bindSuccess((String) data, RelatedId.bind_jingxin);
            }
            break;
        }
    }

    private class AccountManageViewImpl implements AccountManageContract.V {

        @Override
        public void judgeBindStatus(int id, String tips, TProfile key) {
            if (TextUtil.isEmpty(Profile.inst().getString(key))) {
                switch (id) {
                    case RelatedId.bind_phone: {
                        startActivity(BindPhoneActivity.class);
                    }
                    break;
                    case RelatedId.bind_email: {
                        startActivity(BindEmailActivity.class);
                    }
                    break;
                    case RelatedId.bind_jingxin: {
                        startActivity(YaYaAuthorizeBindActivity.class);
                    }
                    break;
                }
            } else {
                // 已绑定
                mView.confirmUnBindDialog(tips, v -> {
                    if (Util.noNetwork()) {
                        return;
                    }
                    refresh(RefreshWay.dialog);
                    mPresenter.unBindMobileAndEmail(id, id);
                });
            }
        }

        @Override
        public void bindSuccess(String data, int id) {
            getRelatedItem(id).save(data, data);
            refreshRelatedItem(id);
            showToast(R.string.account_bind_succeed);
        }

        @Override
        public void unBindSuccess(Result r, int id, TProfile key) {
            if (r.isSucceed()) {
                showToast(R.string.account_unbind_succeed);

                getRelatedItem(id).save(ConstantsEx.KEmpty, ConstantsEx.KEmpty);
                refreshRelatedItem(id);

                Profile.inst().put(key, ConstantsEx.KEmpty);
                Profile.inst().saveToSp();
                AccountManageActivity.this.notify(NotifyType.profile_change);
            } else {
                showToast(r.getMessage());
            }
        }

        @Override
        public void confirmUnBindDialog(CharSequence hint, OnClickListener l) {
            CommonDialog2 d = new CommonDialog2(AccountManageActivity.this);
            d.setHint(hint);
            d.addButton(R.string.confirm, R.color.text_333, l);
            d.addBlueButton(R.string.cancel);
            d.show();
        }

        @Override
        public void onStopRefresh() {
            stopRefresh();
        }
    }
}
