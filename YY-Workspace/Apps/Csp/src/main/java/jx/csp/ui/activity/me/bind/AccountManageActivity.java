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
import jx.csp.model.def.FormType;
import jx.csp.model.form.Form;
import jx.csp.presenter.AccountManagePresenterImpl;
import jx.csp.util.Util;
import lib.platform.Platform.Type;
import lib.ys.ConstantsEx;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseFormActivity;

/**
 * 账号管理
 *
 * @auther Huoxuyu
 * @since 2017/9/26
 */
public class AccountManageActivity extends BaseFormActivity {


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
                .text(Profile.inst().getBindNickName(LoginType.wechat))
                .hint(R.string.account_not_bind));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_bind)
                .related(RelatedId.bind_sina)
                .name(R.string.account_sina)
                .drawable(R.drawable.form_ic_account_sina)
                .text(Profile.inst().getBindNickName(LoginType.sina))
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
                .related(RelatedId.bind_jingxin)
                .name(R.string.account_jingxin)
                .drawable(R.drawable.form_ic_account_jingxin)
                .text(Profile.inst().getBindNickName(LoginType.yaya))
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
                // FIXME: 2017/11/7 等待长玲修改极光的设置
                mView.judgeBindStatus(RelatedId.bind_email, getString(R.string.account_unbind_email), TProfile.email);
            }
            break;
            case RelatedId.bind_wx: {
                if (TextUtil.isEmpty(Profile.inst().getBindNickName(LoginType.wechat))) {
                    refresh(RefreshWay.dialog);
                    mPresenter.doAuth(Type.wechat, RelatedId.bind_wx);
                } else {
                    mPresenter.unBindThirdPartyReq(RelatedId.bind_wx, RelatedId.bind_wx, getString(R.string.account_unbind_wx));
                }
            }
            break;
            case RelatedId.bind_sina: {
                if (TextUtil.isEmpty(Profile.inst().getBindNickName(LoginType.sina))) {
                    refresh(RefreshWay.dialog);
                    mPresenter.doAuth(Type.sina, RelatedId.bind_sina);
                } else {
                    mPresenter.unBindThirdPartyReq(RelatedId.bind_sina, RelatedId.bind_sina, getString(R.string.account_unbind_sina));
                }
            }
            break;
            case RelatedId.bind_jingxin: {
                if (TextUtil.isEmpty(Profile.inst().getBindNickName(LoginType.yaya))) {
                    startActivity(YaYaAuthorizeBindActivity.class);
                } else {
                    mPresenter.unBindThirdPartyReq(RelatedId.bind_jingxin, RelatedId.bind_jingxin, getString(R.string.account_unbind_yaya));
                }
            }
            break;
        }
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        switch (type) {
            case NotifyType.bind_phone: {
                mView.bindRefreshItem((String) data, RelatedId.bind_phone);
            }
            break;
            case NotifyType.bind_wx: {
                mView.bindRefreshItem((String) data, RelatedId.bind_wx);
            }
            break;
            case NotifyType.bind_sina: {
                mView.bindRefreshItem((String) data, RelatedId.bind_sina);
            }
            break;
            case NotifyType.bind_email: {
                mView.bindRefreshItem(Profile.inst().getString(TProfile.email), RelatedId.bind_email);
            }
            break;
            case NotifyType.bind_yaya: {
                mView.bindRefreshItem((String) data, RelatedId.bind_jingxin);
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
                }
            } else {
                // 已绑定
                confirmUnBindDialog(tips, v -> {
                    if (Util.noNetwork()) {
                        return;
                    }
                    refresh(RefreshWay.dialog);
                    mPresenter.unBindMobileOrEmailReq(id, id);
                });
            }
        }

        @Override
        public void bindRefreshItem(String data, int id) {
            getRelatedItem(id).save(data, data);
            refreshRelatedItem(id);
            showToast(R.string.account_bind_succeed);
        }

        @Override
        public void unBindRefreshItem(int id) {
            getRelatedItem(id).save(ConstantsEx.KEmpty, ConstantsEx.KEmpty);
            refreshRelatedItem(id);
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

        @Override
        public void setViewState(int state) {

        }
    }
}
