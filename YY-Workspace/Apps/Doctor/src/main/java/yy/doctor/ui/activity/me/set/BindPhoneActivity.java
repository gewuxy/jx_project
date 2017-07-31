package yy.doctor.ui.activity.me.set;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.network.model.NetworkResp;
import lib.ys.util.TextUtil;
import lib.yy.model.form.BaseForm;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.Constants.CaptchaType;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.model.form.Form;
import yy.doctor.model.form.FormType;
import yy.doctor.model.form.edit.EditCaptchaForm;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * @auther : GuoXuan
 * @since : 2017/7/24
 */
public class BindPhoneActivity extends BaseSetActivity implements TextWatcher {

    private EditText mEtCaptcha;
    private EditText mEtPhone;
    private final int KCaptcha = 0;

    @IntDef({
            RelatedId.phone_number,
            RelatedId.captcha,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int phone_number = 0;
        int captcha = 1;
    }

    @Override
    public void initData() {
        super.initData();

        addItem(Form.create(FormType.et_phone_number)
                .related(RelatedId.phone_number)
                .layout(R.layout.form_edit_bind_phone_number)
                .hint("请输入手机号"));

        addItem(Form.create(FormType.divider));

        addItem(Form.create(FormType.et_captcha)
                .related(RelatedId.captcha)
                .layout(R.layout.form_edit_bind_captcha)
                .textColorRes(R.color.register_captcha_text_selector)
                .hint(R.string.captcha)
                .enable(false));

        addItem(Form.create(FormType.divider));
    }

    @Override
    public void setViews() {
        super.setViews();

        mEtPhone = getRelatedItem(RelatedId.phone_number).getHolder().getEt();
        mEtCaptcha = getRelatedItem(RelatedId.captcha).getHolder().getEt();

        mEtPhone.addTextChangedListener(this);
        mEtCaptcha.addTextChangedListener(this);
    }

    @Override
    protected CharSequence getNavBarText() {
        return "账号绑定";
    }

    @Override
    protected CharSequence getSetText() {
        return "确认绑定";
    }

    @Override
    protected void bind() {
        exeNetworkReq(NetFactory.bindMobile(getPhone(), mEtCaptcha.getText().toString().trim()));
    }

    private String getPhone() {
        return Util.getEtString(mEtPhone).replace(" ", "");
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        setChanged(Util.isMobileCN(getPhone())
                && TextUtil.isNotEmpty(Util.getEtString(mEtCaptcha)));
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    protected void onFormViewClick(View v, int position, Object related) {
        switch ((int) related) {
            case RelatedId.captcha: {
                if (v.getId() == R.id.form_tv_text) {
                    exeNetworkReq(KCaptcha,
                            NetFactory.captcha(getPhone(), CaptchaType.fetch));
                    ((EditCaptchaForm) getRelatedItem(RelatedId.captcha)).start();
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
        Result r = (Result) result;
        if (id == KCaptcha) {
            // 获取验证码
        } else {
            if (r.isSucceed()) {
                setResult(RESULT_OK, new Intent().putExtra(Extra.KData, getPhone()));
                Profile.inst().put(TProfile.mobile, getPhone());
                finish();
            } else {
                showToast(r.getError());
            }
        }
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        BaseForm form = getRelatedItem(RelatedId.captcha);
        if (type == NotifyType.fetch_message_captcha) {
            form.enable(true);
            refreshItem(form);
        } else if (type == NotifyType.disable_fetch_message_captcha) {
            form.enable(false);
            refreshItem(form);
        }
    }
}