package jx.csp.ui.activity.login;

import android.support.annotation.IntDef;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jx.csp.R;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.form.Form;
import jx.csp.model.form.FormType;
import jx.csp.network.NetFactory;
import jx.csp.ui.activity.TestActivity;
import jx.csp.util.Util;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;

/**
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

    private EditText mEtNickName;
    private LinearLayout mLinearLayoutProtocol;

    @Override
    public void initData() {
        super.initData();
        addItem(Form.create(FormType.et)
                .related(RelatedId.nickname)
                .hint(R.string.input_nickname)
                .drawable(R.drawable.login_ic_nickname));
        addItem(Form.create(FormType.divider_margin));
    }

    @Override
    public void findViews() {
        super.findViews();
        mLinearLayoutProtocol = findView(R.id.linea_layout_protocol);
    }

    @Override
    public void setViews() {
        super.setViews();
        mEtNickName = getRelatedItem(RelatedId.nickname).getHolder().getEt();
        mEtNickName.addTextChangedListener(this);

        setOnClickListener(R.id.protocol);
        //Fixme:没见到效果，什么鬼
        LinearLayout.LayoutParams lp = (LayoutParams) mLinearLayoutProtocol.getLayoutParams();
        lp.setMargins(75,331,75,27);
        mLinearLayoutProtocol.setLayoutParams(lp);
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
                //Fixme:跳转到h5页面，现在还没有文案
                startActivity(TestActivity.class);
            }
            break;
        }
    }

    @Override
    protected void toSet() {
        refresh(RefreshWay.dialog);
        exeNetworkReq(NetFactory.modifyProfile("nickName",getNickName()));
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return super.onNetworkResponse(id, r);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result r = (Result) result;
        if (r.isSucceed()) {
            stopRefresh();
            //保存到本地
            Profile.inst().put(TProfile.nickName,getNickName());
            Profile.inst().saveToSp();
            //Fixme:跳到首页，目前还没有
            startActivity(TestActivity.class);
        }else {
            onNetworkError(id,r.getError());
        }
    }

    @Override
    protected int getFooterResId() {
        return R.layout.layout_captcha_login_footer;
    }

    @Override
    public void afterTextChanged(Editable s) {
        setChanged(TextUtil.isNotEmpty(Util.getEtString(mEtNickName)));
    }

    public String getNickName() {
        if (mEtNickName == null) {
            return "";
        }
        return Util.getEtString(mEtNickName);
    }
}
