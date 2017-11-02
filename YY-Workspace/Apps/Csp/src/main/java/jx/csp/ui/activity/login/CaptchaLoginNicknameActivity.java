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
import jx.csp.model.form.Form;
import jx.csp.model.def.FormType;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.ui.activity.main.MainActivity;
import jx.csp.util.Util;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;

/**
 * 手机登录之后没有昵称的跳出的输入昵称
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
                showToast("没有文案，先酱紫，哈哈");
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
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Profile.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<Profile> r = (Result) result;
        if (r.isSucceed()) {
            stopRefresh();
            //保存到本地
            Profile.inst().update(r.getData());
            startActivity(MainActivity.class);
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
        return Util.getEtString(mEtNickName);
    }
}
