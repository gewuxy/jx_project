package jx.csp.ui.activity.login.moblie;

import android.support.annotation.IntDef;
import android.text.Editable;
import android.text.InputFilter;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.App;
import jx.csp.R;
import jx.csp.constant.Constants;
import jx.csp.constant.FormType;
import jx.csp.model.Profile;
import jx.csp.model.form.Form;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.network.UrlUtil;
import jx.csp.sp.SpUser;
import jx.csp.ui.activity.CommonWebViewActivityRouter;
import jx.csp.ui.activity.login.BaseLoginActivity;
import jx.csp.ui.activity.MainActivity;
import jx.csp.util.Util;
import lib.jx.notify.Notifier.NotifyType;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.TextUtil;

/**
 * 手机登录之后没有昵称的跳出的输入昵称
 *
 * @auther WangLan
 * @since 2017/9/28
 */

public class CaptchaLoginNicknameActivity extends BaseLoginActivity {

    @IntDef({
            RelatedId.nickname,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int nickname = 0;
    }

    private View mLayout;

    @Override
    public void initData() {
        super.initData();
        addItem(Form.create(FormType.et)
                .related(RelatedId.nickname)
                .hint(R.string.input_nickname)
                .textWatcher(this)
                .layout(R.layout.form_edit_nickname)
                .input((InputFilter) (source, start, end, dest, dstart, dend) -> {
                    if (source.equals(" ")) {
                        return "";
                    }
                    return null;
                })
                .drawable(R.drawable.login_ic_nickname));
        addItem(Form.create(FormType.divider_margin_login));
    }

    @Override
    public void findViews() {
        super.findViews();

        mLayout = findView(R.id.layout_email_login);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.protocol);
        showView(mLayout);
        goneView(mTvWelcome);
    }

    @Override
    protected CharSequence getSetText() {
        return getString(R.string.affirm);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.protocol: {
                String agreement;
                if (Util.checkAppCn()) {
                    agreement = getString(R.string.service_agreement);
                }else {
                    agreement = getString(R.string.service_agreement_oversea);
                }
                CommonWebViewActivityRouter.create(UrlUtil.getUrlDisclaimer()).name(agreement)
                        .route(this);
            }
            break;
        }
    }

    @Override
    protected void toSet() {
        refresh(RefreshWay.dialog);
        exeNetworkReq(UserAPI.modify().nickName(getNickName()).build());
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), Profile.class);
    }

    @Override
    public boolean interceptNetSuccess(int id, IResult r) {
        if (r.getCode() == Constants.KAccountFrozen) {
            App.AccountFrozen frozen = new App.AccountFrozen();
            frozen.onIntercept(r, CaptchaLoginNicknameActivity.this);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        stopRefresh();
        if (r.isSucceed()) {
            //保存到本地
            Profile.inst().update((Profile) r.getData());
            notify(NotifyType.login);
            SpUser.inst().updateProfileRefreshTime();
            startActivity(MainActivity.class);
            finish();
        } else {
            onNetworkError(id, r.getError());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        setChanged(TextUtil.isNotEmpty(getNickName()));
    }

    public String getNickName() {
        return getRelatedString(RelatedId.nickname);
    }
}
