package jx.csp.ui.activity.me.bind;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

import jx.csp.Constants.CaptchaType;
import jx.csp.R;
import jx.csp.dialog.HintDialog;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.form.Form;
import jx.csp.model.form.FormType;
import jx.csp.model.form.edit.EditCaptchaForm;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.util.Util;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.TextUtil;
import lib.yy.model.form.BaseForm;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;

/**
 * 绑定手机号
 *
 * @auther Huoxuyu
 * @since 2017/9/26
 */
public class BindPhoneActivity extends BaseSetActivity {

    private final int KIdCaptcha = 0;
    private final int KMaxCount = 3; // 最多获取3次验证码
    private final long KCaptchaDuration = TimeUnit.MINUTES.toMillis(10); // 10分钟

    private int mCount; // 计算点击多少次
    private long mStartTime; // 开始计算10分钟间隔的时间

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
        mCount = 0;

        addItem(Form.create(FormType.divider_large));
        addItem(Form.create(FormType.et_phone_number)
                .related(RelatedId.phone_number)
                .layout(R.layout.form_edit_bind_phone_number)
                .hint(R.string.input_phone_number));

        addItem(Form.create(FormType.divider));
        addItem(Form.create(FormType.et_captcha)
                .related(RelatedId.captcha)
                .drawable(R.drawable.login_ic_pwd)
                .textColorRes(R.color.bind_captcha_text_selector)
                .enable(false)
                .hint(R.string.input_captcha));
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
        return getString(R.string.account_bind_phone);
    }

    @Override
    protected CharSequence getSetText() {
        return getString(R.string.account_confirm_bind);
    }

    @NonNull
    private String getPhone() {
        return Util.getEtString(mEtPhone).replace(" ", "");
    }

    @NonNull
    private String getCaptcha() {
        return Util.getEtString(mEtCaptcha);
    }

    @Override
    protected void doSet() {
        refresh(RefreshWay.dialog);
        exeNetworkReq(UserAPI.bindPhone(getPhone(), getCaptcha()).build());
    }

    @Override
    public void afterTextChanged(Editable s) {
        // 是手机号且验证码长度为6
        setChanged(Util.isMobileCN(getPhone()) && TextUtil.isNotEmpty(getCaptcha()) && getCaptcha().length() == 6);
    }

    @Override
    protected void onFormViewClick(View v, int position, Object related) {
        switch ((int) related) {
            case RelatedId.captcha: {
                if (v.getId() == R.id.form_tv_text) {
                    if (getPhone().equals(Profile.inst().getString(TProfile.mobile))) {
                        showToast(R.string.account_is_bind);
                        return;
                    }
                    View view = inflate(R.layout.dialog_captcha);
                    TextView tv = view.findViewById(R.id.captcha_tv_phone_number);
                    String phone = getItemStr(RelatedId.phone_number);
                    tv.setText(phone);

                    HintDialog dialog = new HintDialog(this);
                    dialog.addHintView(view);
                    dialog.addGrayButton(R.string.cancel);
                    dialog.addBlueButton(getString(R.string.well), v1 -> {
                        mCount++;
                        YSLog.d("mCount:", mCount + "");
                        if (mCount == 1) {
                            mStartTime = System.currentTimeMillis();
                        }
                        if (mCount > KMaxCount) {
                            long duration = System.currentTimeMillis() - mStartTime;
                            if (duration <= KCaptchaDuration) {
                                showToast(R.string.get_captcha_frequently);
                                return;
                            } else {
                                mCount = 1;
                            }
                        }
                        exeNetworkReq(KIdCaptcha, UserAPI.sendCaptcha(getPhone(), CaptchaType.re_fetch).build());
                    });
                    dialog.show();
                }
            }
            break;
        }
    }

    /**
     * 获取Item的文本信息
     */
    private String getItemStr(@RelatedId int relatedId) {
        return getRelatedItem(relatedId).getVal();
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        stopRefresh();
        Result r = (Result) result;
        if (r.isSucceed()) {
            if (id == KIdCaptcha) {
                // 获取验证码
                EditCaptchaForm item = (EditCaptchaForm) getRelatedItem(RelatedId.captcha);
                item.start();
                showToast(R.string.account_send_captcha);
            } else {
                Profile.inst().put(TProfile.mobile, getPhone());
                Profile.inst().saveToSp();
                notify(NotifyType.profile_change, getPhone());
                notify(NotifyType.bind_phone, getPhone());
                finish();
            }
        } else {
            showToast(r.getMessage());
        }
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        BaseForm form = getRelatedItem(RelatedId.captcha);
        if (type == NotifyType.fetch_message_captcha) {
            form.enable(true);
        } else if (type == NotifyType.disable_fetch_message_captcha) {
            form.enable(false);
        }
        refreshItem(form);
    }
}