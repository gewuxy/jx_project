package jx.csp.ui.activity.me;

import android.support.annotation.IntDef;
import android.view.View;
import android.view.View.OnClickListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.R;
import jx.csp.dialog.HintDialogMain;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.authorize.PlatformAuthorizeUserInfoManager;
import jx.csp.model.form.Form;
import jx.csp.model.form.FormType;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.ui.activity.me.set.BindEmailActivity;
import jx.csp.ui.activity.me.set.BindPhoneActivity;
import jx.csp.util.Util;
import lib.network.model.NetworkResp;
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

    private PlatformAuthorizeUserInfoManager mPlatAuth;

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
    private @interface RelatedId {
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

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_bind)
                .related(RelatedId.bind_phone)
                .name(R.string.account_phone)
                .drawable(R.drawable.form_ic_account_phone)
                .text(Profile.inst().getString(TProfile.phone))
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
                .related(RelatedId.bind_facebook)
                .name(R.string.account_facebook)
                .drawable(R.drawable.form_ic_account_facebook)
                .text(Profile.inst().getString(TProfile.facebook))
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
                .text(Profile.inst().getString(TProfile.jingxin))
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
                if (TextUtil.isEmpty(Profile.inst().getString(TProfile.phone))) {
                    startActivity(BindPhoneActivity.class);
                } else {
                    // 已绑定
                    unBind(getString(R.string.account_unbind_phone), v1 -> {
                        if (Util.noNetwork()) {
                            return;
                        }
                        refresh(RefreshWay.dialog);
                        exeNetworkReq(RelatedId.bind_phone, UserAPI.unBind(RelatedId.bind_phone).build());
                    });
                }
            }
            break;
            case RelatedId.bind_email: {
                // FIXME: 2017/10/16 不知道什么时候认证邮箱，暂时无法绑定，使用邮箱登录可以显示绑定
                if (TextUtil.isEmpty(Profile.inst().getString(TProfile.email))) {
                    startActivity(BindEmailActivity.class);
                } else {
                    // 已绑定
                    unBind(getString(R.string.account_unbind_email), v1 -> {
                        if (Util.noNetwork()) {
                            return;
                        }
                        refresh(RefreshWay.dialog);
                        exeNetworkReq(RelatedId.bind_email, UserAPI.unBind(RelatedId.bind_email).build());
                    });
                }
            }
            break;
            case RelatedId.bind_wx: {
                if (TextUtil.isEmpty(Profile.inst().getString(TProfile.wx))) {
                    // 未绑定
                    if (Util.noNetwork()) {
                        return;
                    }
                    mPlatAuth.WeiXinAuthorize();
//                    WXLoginApi.create(AccountManageActivity.this, Constants.KAppId);
//                    if (WXLoginApi.isWXAppInstalled()) {
//                        WXLoginApi.sendReq(WXType.bind);
//                    } else {
//                        notInstallWx();
//                    }
                } else {
                    // 已绑定
                    unBind(getString(R.string.account_unbind_wx), v1 -> {
                        if (Util.noNetwork()) {
                            return;
                        }
                        refresh(RefreshWay.dialog);
                        exeNetworkReq(UserAPI.bindAccountStatus()
                                .thirdPartyId(RelatedId.bind_wx)
                                .build());
                    });
                }
            }
            break;
            case RelatedId.bind_sina: {
                if (TextUtil.isEmpty(Profile.inst().getString(TProfile.sina))) {
                    // 未绑定
                    if (Util.noNetwork()) {
                        return;
                    }
                    mPlatAuth.sinaAuthorize();

                } else {

                }
            }
            break;
            case RelatedId.bind_facebook: {
                if (TextUtil.isEmpty(Profile.inst().getString(TProfile.facebook))) {
                    // 未绑定
                    if (Util.noNetwork()) {
                        return;
                    }
                    mPlatAuth.facebookAuthorize();
//                    exeNetworkReq(UserAPI.bindAccountStatus()
//                            .thirdPartyId(RelatedId.bind_facebook)
//                            .uniqueId(Profile.inst().getString(TProfile.id))
//                            .nickName(Profile.inst().getString(TProfile.userName))
//                            .avatar(Profile.inst().getString(TProfile.avatar))
//                            .gender(Profile.inst().getString(TProfile.gender))
//                            .build());
                } else {

                }
            }
            break;
            case RelatedId.bind_twitter: {
                if (TextUtil.isEmpty(Profile.inst().getString(TProfile.twitter))) {
                    // 未绑定
                    if (Util.noNetwork()) {
                        return;
                    }
                    mPlatAuth.twitterAuthorize();
                } else {

                }
            }
            break;
            case RelatedId.bind_jingxin: {
                if (TextUtil.isEmpty(Profile.inst().getString(TProfile.jingxin))) {
                    // 未绑定
                    if (Util.noNetwork()) {
                        return;
                    }
                }else {

                }
            }
            break;
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        stopRefresh();
        // 绑定的
        Result r = (Result) result;
        switch (id) {
            case RelatedId.bind_wx: {
                unBindUpdate(r, RelatedId.bind_wx, TProfile.wx);
            }
            break;
            case RelatedId.bind_sina: {
                unBindUpdate(r, RelatedId.bind_sina, TProfile.sina);
            }
            break;
            case RelatedId.bind_facebook: {
                unBindUpdate(r, RelatedId.bind_facebook, TProfile.facebook);
            }
            break;
            case RelatedId.bind_twitter: {
                unBindUpdate(r, RelatedId.bind_twitter, TProfile.twitter);
            }
            break;
            case RelatedId.bind_jingxin: {
                unBindUpdate(r, RelatedId.bind_twitter, TProfile.twitter);
            }
            break;
            case RelatedId.bind_phone: {
                unBindUpdate(r, RelatedId.bind_phone, TProfile.phone);
            }
            break;
            case RelatedId.bind_email: {
                unBindUpdate(r, RelatedId.bind_email, TProfile.email);
            }
            break;
        }
    }

    /**
     * 绑定成功
     *
     * @param data
     * @param id
     */
    private void bindSuccess(String data, @RelatedId int id) {
        getRelatedItem(id).save(data, data);
        refreshRelatedItem(id);
        showToast(R.string.account_bind_succeed);
    }

    /**
     * 确定是否解绑
     *
     * @param hint
     * @param l
     */
    private void unBind(CharSequence hint, OnClickListener l) {
        HintDialogMain d = new HintDialogMain(AccountManageActivity.this);
        d.setHint(hint);
        d.addButton(R.string.confirm, R.color.text_333, l);
        d.addBlueButton(R.string.cancel);
        d.show();
    }

    /**
     * 解绑成功
     *
     * @param r
     * @param id
     * @param key
     */
    private void unBindUpdate(Result r, @RelatedId int id, TProfile key) {
        if (r.isSucceed()) {
            showToast(R.string.account_unbind_succeed);

            getRelatedItem(id).save(ConstantsEx.KEmpty, ConstantsEx.KEmpty);
            refreshRelatedItem(id);

            Profile.inst().put(key, ConstantsEx.KEmpty);
            Profile.inst().saveToSp();
        } else {
            showToast(r.getMessage());
        }
    }

    /**
     * 没有安装微信
     */
//    private void notInstallWx() {
//        HintDialogSec dialog = new HintDialogSec(AccountManageActivity.this);
//        dialog.setMainHint(R.string.account_accredit_error);
//        dialog.setSecHint(R.string.account_wx_check_normal);
//        dialog.addBlueButton(R.string.confirm);
//        dialog.show();
//    }
    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.bind_wx) {
            bindSuccess((String) data, RelatedId.bind_wx);
        } else if (type == NotifyType.bind_phone) {
            bindSuccess((String) data, RelatedId.bind_phone);
        } else if (type == NotifyType.bind_email) {
            String email = Profile.inst().getString(TProfile.email);
            getRelatedItem(RelatedId.bind_email).save(email, email);
            refreshRelatedItem(RelatedId.bind_email);
        }
    }
}
