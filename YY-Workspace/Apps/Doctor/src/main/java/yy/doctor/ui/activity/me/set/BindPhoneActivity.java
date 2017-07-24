package yy.doctor.ui.activity.me.set;

import android.support.annotation.IntDef;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.util.TextUtil;
import yy.doctor.R;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;
import yy.doctor.util.Util;

/**
 * @auther : GuoXuan
 * @since : 2017/7/24
 */
public class BindPhoneActivity extends BaseSetActivity implements TextWatcher {

    private EditText mEtCaptcha;
    private EditText mEtPhone;

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

        addItem(new Builder(FormType.et_phone_number)
                .related(RelatedId.phone_number)
                .layout(R.layout.form_edit_bind_phone_number)
                .hint("请输入手机号")
                .build());

        addItem(new Builder(FormType.divider)
                .build());

        addItem(new Builder(FormType.et_captcha)
                .related(RelatedId.captcha)
                .layout(R.layout.form_edit_bind_captcha)
                .hint("请输入验证码")
                .build());

        addItem(new Builder(FormType.divider)
                .build());
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        setChanged(Util.isMobileCN(Util.getEtString(mEtPhone))
                && TextUtil.isNotEmpty(Util.getEtString(mEtCaptcha)));
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public CharSequence getNavBarText() {
        return "账号绑定";
    }

    @Override
    public CharSequence getSetText() {
        return "确认绑定";
    }
}