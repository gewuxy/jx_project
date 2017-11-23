package jx.csp.ui.activity.login;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.text.Editable;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.R;
import jx.csp.constant.FormType;
import jx.csp.model.Profile;
import jx.csp.model.form.Form;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.network.UrlUtil;
import jx.csp.ui.activity.CommonWebViewActivityRouter;
import jx.csp.ui.activity.main.MainActivity;
import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.TextUtil;
import lib.yy.notify.Notifier.NotifyType;

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

    @Override
    public void initData(Bundle state) {
        super.initData(state);
        addItem(Form.create(FormType.et)
                .related(RelatedId.nickname)
                .hint(R.string.input_nickname)
                .textWatcher(this)
                .drawable(R.drawable.login_ic_nickname));
        addItem(Form.create(FormType.divider_margin));
    }

    @Override
    public void findViews() {
        super.findViews();
    }

    @Override
    public void setViews() {
        super.setViews();
        setOnClickListener(R.id.protocol);
    }

    @Override
    protected CharSequence getNavBarText() {
        return getString(R.string.captcha_login);
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
                CommonWebViewActivityRouter.create(UrlUtil.getUrlDisclaimer()).name(getString(R.string.service_agreement))
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
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            stopRefresh();
            //保存到本地
            Profile.inst().update((Profile) r.getData());
            notify(NotifyType.login);
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
        return getRelatedItem(RelatedId.nickname).getVal().trim();
    }
}
