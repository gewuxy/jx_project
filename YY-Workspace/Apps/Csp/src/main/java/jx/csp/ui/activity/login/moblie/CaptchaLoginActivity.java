package jx.csp.ui.activity.login.moblie;

import android.support.annotation.IntDef;
import android.text.Editable;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

import jx.csp.R;
import jx.csp.constant.BindId;
import jx.csp.constant.CaptchaType;
import jx.csp.constant.FormType;
import jx.csp.dialog.CommonDialog;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.model.form.Form;
import jx.csp.model.form.edit.EditCaptchaForm;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.network.UrlUtil;
import jx.csp.sp.SpUser;
import jx.csp.ui.activity.CommonWebViewActivityRouter;
import jx.csp.ui.activity.login.BaseLoginActivity;
import jx.csp.ui.activity.MainActivity;
import jx.csp.util.Util;
import lib.jx.model.form.BaseForm;
import lib.jx.notify.Notifier.NotifyType;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.TextUtil;


/**
 * 验证码登录，又称手机登录
 *
 * @auther WangLan
 * @since 2017/9/25
 */

public class CaptchaLoginActivity extends BaseLoginActivity {

    @IntDef({
            RelatedId.phone_number,
            RelatedId.captcha,
            RelatedId.nickname,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int phone_number = 0;
        int captcha = 1;
        int nickname = 2;
    }

    private final int KIdLogin = 1;
    private final int KIdCaptcha = 2;
    private final int KMaxCount = 3; // 最多获取3次验证码
    private final long KCaptchaDuration = TimeUnit.MINUTES.toMillis(10); // 10分钟

    private View mLayout;
    private int mCount; // 计算点击多少次
    private long mStartTime; // 开始计算10分钟间隔的时间


    @Override
    public void initData() {
        super.initData();

        mCount = 0;

        addItem(Form.create(FormType.et_phone_number)
                .related(RelatedId.phone_number)
                .hint(R.string.mobile)
                .textWatcher(this)
                .layout(R.layout.form_edit_login_phone_number)
                .drawable(R.drawable.login_ic_phone));
        addItem(Form.create(FormType.divider_margin_login));
        addItem(Form.create(FormType.divider_large_login));

        addItem(Form.create(FormType.et_captcha))
                .related(RelatedId.captcha)
                .hint(R.string.captcha)
                .textWatcher(this)
                .textColorRes(R.color.bind_captcha_text_selector)
                .drawable(R.drawable.login_ic_pwd)
                .enable(false);

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

        //清空用户信息
        Profile.inst().clear();

        setOnClickListener(R.id.protocol);
        showView(mLayout);
    }

    @Override
    protected CharSequence getSetText() {
        return getString(R.string.captcha_login);
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
    protected void onFormViewClick(View v, int position, Object related) {
        switch ((int) related) {
            case RelatedId.captcha: {
                if (v.getId() == R.id.form_tv_text) {
                    View view = inflate(R.layout.dialog_captcha);
                    TextView tv = view.findViewById(R.id.captcha_tv_phone_number);
                    String phone = getRelatedString(RelatedId.phone_number);
                    tv.setText(phone);

                    CommonDialog dialog = new CommonDialog(this);
                    dialog.addHintView(view);
                    dialog.addBlackButton(R.string.cancel);
                    dialog.addBlackButton(getString(R.string.well), v1 -> {
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
                        exeNetworkReq(KIdCaptcha, UserAPI.sendCaptcha(getPhone(), CaptchaType.fetch).build());
                    });
                    dialog.show();
                }
            }
            break;
        }
    }

    @Override
    protected void toSet() {
        refresh(RefreshWay.dialog);
        exeNetworkReq(KIdLogin, UserAPI.login(BindId.phone).mobile(getPhone()).captcha(getCaptcha()).build());
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), Profile.class);
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        stopRefresh();
        if (id == KIdLogin) {
            if (r.isSucceed()) {
                Profile data = (Profile) r.getData();
                Profile.inst().update(data);

                //如果有nickname这个字段
                if (TextUtil.isNotEmpty(data.getString(TProfile.nickName))) {
                    notify(NotifyType.login);
                    SpUser.inst().updateProfileRefreshTime();
                    startActivity(MainActivity.class);
                } else {
                    startActivity(CaptchaLoginNicknameActivity.class);
                }
                finish();
            } else {
                onNetworkError(id, r.getError());
            }
        } else if (id == KIdCaptcha) {
            if (r.isSucceed()) {
                ((EditCaptchaForm) getRelatedItem(RelatedId.captcha)).start();
                showToast(R.string.send_captcha);
            } else {
                onNetworkError(id, r.getError());
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        setChanged(Util.isMobileCN(getPhone()) && TextUtil.isNotEmpty(getCaptcha()));
    }

    public String getPhone() {
        return getRelatedString(RelatedId.phone_number).replace(" ", "");
    }

    public String getCaptcha() {
        return getRelatedString(RelatedId.captcha);
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        if (type == NotifyType.logout) {
            finish();
        } else if (type == NotifyType.fetch_message_captcha) {
            BaseForm form = getRelatedItem(RelatedId.captcha);
            form.enable(true);
            refreshItem(form); // 发送了Notify,fetch_message_captcha
        } else if (type == NotifyType.disable_fetch_message_captcha) {
            BaseForm form = getRelatedItem(RelatedId.captcha);
            form.enable(false);
            refreshItem(form);
        }
    }

}
