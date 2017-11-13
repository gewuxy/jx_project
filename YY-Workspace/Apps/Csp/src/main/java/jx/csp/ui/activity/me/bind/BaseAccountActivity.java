package jx.csp.ui.activity.me.bind;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.view.View;
import android.view.View.OnClickListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.R;
import jx.csp.constant.BindId;
import jx.csp.contact.AccountManageContract;
import jx.csp.dialog.CommonDialog2;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
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
 * @auther Huoxuyu
 * @since 2017/11/8
 */

abstract public class BaseAccountActivity extends BaseFormActivity {

    protected AccountManageContract.P mPresenter;
    protected AccountManageContract.V mView;

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
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, getString(R.string.account_manage), this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);

        mView = new AccountManageViewImpl();
        mPresenter = new AccountManagePresenterImpl(mView);
    }

    @Override
    protected void onFormItemClick(View v, int position) {
        @RelatedId int relatedId = getItem(position).getRelated();
        switch (relatedId) {
            case RelatedId.bind_phone: {
                mView.judgeBindStatus(BindId.phone, getString(R.string.account_unbind_phone), TProfile.mobile);
            }
            break;
            case RelatedId.bind_email: {
                mView.judgeBindStatus(BindId.email, getString(R.string.account_unbind_email), TProfile.email);
            }
            break;
            case RelatedId.bind_wx: {
                mView.judgeBindStatus(BindId.wechat, Type.wechat, getString(R.string.account_unbind_wx));
            }
            break;
            case RelatedId.bind_sina: {
                mView.judgeBindStatus(BindId.sina, Type.sina, getString(R.string.account_unbind_sina));
            }
            break;
            case RelatedId.bind_facebook: {
                mView.judgeBindStatus(BindId.facebook, Type.facebook, getString(R.string.account_unbind_facebook));
            }
            break;
            case RelatedId.bind_twitter: {
                mView.judgeBindStatus(BindId.twitter, Type.twitter, getString(R.string.account_unbind_twitter));
            }
            break;
            case RelatedId.bind_jingxin: {
                if (TextUtil.isEmpty(Profile.inst().getBindNickName(BindId.yaya))) {
                    startActivity(YaYaAuthorizeBindActivity.class);
                } else {
                    mView.showUnBindDialog(BindId.yaya, getString(R.string.account_unbind_yaya));
                }
            }
            break;
        }
    }


    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type < NotifyType.bind_wx || type > NotifyType.bind_twitter) {
            // 不接收bind以外的消息
            return;
        }

        switch (type) {
            case NotifyType.bind_phone: {
                mView.refreshItem(RelatedId.bind_phone, (String) data);
            }
            break;
            case NotifyType.bind_wx: {
                mView.refreshItem(RelatedId.bind_wx, (String) data);
            }
            break;
            case NotifyType.bind_sina: {
                mView.refreshItem(RelatedId.bind_sina, (String) data);
            }
            break;
            case NotifyType.bind_fackbook: {
                mView.refreshItem(RelatedId.bind_facebook, (String) data);
            }
            break;
            case NotifyType.bind_twitter: {
                mView.refreshItem(RelatedId.bind_twitter, (String) data);
            }
            break;
            case NotifyType.bind_email: {
                mView.refreshItem(RelatedId.bind_email, Profile.inst().getString(TProfile.email));
            }
            break;
            case NotifyType.bind_yaya: {
                mView.refreshItem(RelatedId.bind_jingxin, (String) data);
            }
            break;
        }

        showToast(R.string.account_bind_succeed);
    }

    private class AccountManageViewImpl implements AccountManageContract.V {

        @Override
        public void judgeBindStatus(int bindId, String tips, TProfile key) {
            if (TextUtil.isEmpty(Profile.inst().getString(key))) {
                switch (bindId) {
                    case BindId.phone: {
                        startActivity(BindPhoneActivity.class);
                    }
                    break;
                    case BindId.email: {
                        startActivity(BindEmailActivity.class);
                    }
                    break;
                }
            } else {
                // 已绑定
                showConfirmUnBindDialog(tips, v -> {
                    if (Util.noNetwork()) {
                        return;
                    }
                    refresh(RefreshWay.dialog);
                    mPresenter.unBind(bindId);
                });
            }
        }

        @Override
        public void showUnBindDialog(int bindId, String tips) {
            showConfirmUnBindDialog(tips, v -> {
                if (Util.noNetwork()) {
                    return;
                }
                refresh(RefreshWay.dialog);
                mPresenter.unBind(bindId);
            });
        }

        @Override
        public void judgeBindStatus(int bindId, Type type, String tips) {
            if (TextUtil.isEmpty(Profile.inst().getBindNickName(bindId))) {
                refresh(RefreshWay.dialog);
                mPresenter.auth(bindId, type);
            } else {
                showUnBindDialog(bindId, tips);
            }
        }

        @Override
        public void refreshItem(@RelatedId int id, String data) {
            getRelatedItem(id).save(data, data);
            refreshRelatedItem(id);
        }

        @Override
        public void refreshItem(@RelatedId int id) {
            getRelatedItem(id).save(ConstantsEx.KEmpty, ConstantsEx.KEmpty);
            refreshRelatedItem(id);
        }

        @Override
        public void showConfirmUnBindDialog(CharSequence hint, OnClickListener l) {
            CommonDialog2 d = new CommonDialog2(BaseAccountActivity.this);
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
