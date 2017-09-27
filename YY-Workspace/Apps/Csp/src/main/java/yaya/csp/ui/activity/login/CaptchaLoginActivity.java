package yaya.csp.ui.activity.login;

import android.support.annotation.IntDef;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;
import yaya.csp.Extra;
import yaya.csp.R;
import yaya.csp.dialog.HintDialog;
import yaya.csp.model.Profile;
import yaya.csp.model.form.Form;
import yaya.csp.model.form.FormType;
import yaya.csp.network.NetworkAPISetter.LoginAPI;
import yaya.csp.sp.SpApp;
import yaya.csp.sp.SpUser;
import yaya.csp.ui.activity.TestActivity;
import yaya.csp.util.UISetter;
import yaya.csp.util.Util;

/**
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

    private EditText mEtPhoneNumber;
    private EditText mEtCaptcha;
    private EditText mEtNickName;
    private TextView mAgreeProtocol;
    private String mRequest; // 判断桌面快捷方式进来
    private int mCount; // 计算点击多少次
    private long mStartTime; // 开始计算10分钟间隔的时间
    private final int KMaxCount = 3; // 最多获取3次验证码
    private final long KCaptchaDuration = TimeUnit.MINUTES.toMillis(10); // 10分钟

    @Override
    public void initData() {
        super.initData();
        mCount = 0;
        mRequest = getIntent().getStringExtra(Extra.KData);

        addItem(Form.create(FormType.et_phone_number)
                .related(RelatedId.phone_number)
                .hint(R.string.input_phone_number)
                .drawable(R.drawable.login_ic_phone));
        addItem(Form.create(FormType.divider_margin));

        addItem(Form.create(FormType.et_captcha))
                .related(RelatedId.captcha)
                .hint(R.string.input_captcha)
                .textColorRes(R.color.bind_captcha_text_selector)
                .drawable(R.drawable.login_ic_pwd)
                .enable(false);
        addItem(Form.create(FormType.divider_margin));

        addItem(Form.create(FormType.et)
                .related(RelatedId.nickname)
                .hint(R.string.input_nickname)
                .drawable(R.drawable.login_ic_nickname));
        addItem(Form.create(FormType.divider_margin));
    }

    @Override
    public void findViews() {
        super.findViews();
        mAgreeProtocol = findView(R.id.phone_agree_protocol);
    }

    @Override
    public void setViews() {
        super.setViews();

        //清空用户信息
        Profile.inst().clear();
        mEtPhoneNumber = getRelatedItem(RelatedId.phone_number).getHolder().getEt();
        mEtPhoneNumber.addTextChangedListener(this);
        mEtCaptcha = getRelatedItem(RelatedId.captcha).getHolder().getEt();
        mEtCaptcha.addTextChangedListener(this);
        mEtNickName = getRelatedItem(RelatedId.nickname).getHolder().getEt();
        mEtNickName.addTextChangedListener(this);

        mAgreeProtocol.setText( UISetter.setLoginProtocol(getString(R.string.agree_login)));
    }

    @Override
    protected CharSequence getNavBarText() {
        return getString(R.string.captcha_login);
    }

    @Override
    protected CharSequence getSetText() {
        return getString(R.string.confirm_login);
    }

    @Override
    protected void onFormViewClick(View v, int position, Object related) {
        switch ((int)related){
            case RelatedId.captcha:{
                View view = inflate(R.layout.dialog_captcha);
                TextView tv = (TextView) view.findViewById(R.id.captcha_tv_phone_number);
                String phone = getItemStr(RelatedId.phone_number);
                tv.setText(phone);

                HintDialog dialog = new HintDialog(this);
                dialog.addHintView(view);
                dialog.addBlueButton(R.string.cancel);
                dialog.addBlueButton(getString(R.string.well), v1 -> {
                    mCount++;
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
                   // exeNetworkReq(KIdCaptcha, RegisterAPI.captcha(getPhone(), CaptchaType.fetch).build());
                });
            }
        }
    }

    @Override
    protected void toSet() {
        refresh(RefreshWay.dialog);
        exeNetworkReq(LoginAPI.login(6).mobile(getPhone()).captcha(getCaptcha()).nickName(getNickName()).build());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result<Profile> r = (Result<Profile>) result;
        if (r.isSucceed()) {
            //保存用户名,验证码登录的用户名是昵称
            SpApp.inst().saveUserName(getNickName());
            Profile.inst().update(r.getData());
            SpUser.inst().updateProfileRefreshTime();

            //判断跳转到哪里
            if (TextUtil.isEmpty(mRequest)) {
                //Fixme:跳转到首页，目前暂时没有
                startActivity(TestActivity.class);
            } else {
                setResult(RESULT_OK);
            }
            stopRefresh();
            finish();
        } else {
            onNetworkError(id, r.getError());
        }
    }

    @Override
    protected int getFooterResId() {
        return R.layout.layout_captcha_login_footer;
    }

    @Override
    public void afterTextChanged(Editable s) {
        setChanged(Util.isMobileCN(Util.getEtString(mEtPhoneNumber)) && TextUtil.isNotEmpty(Util.getEtString(mEtCaptcha))
                && TextUtil.isNotEmpty(Util.getEtString(mEtNickName)));
    }

    public String getPhone() {
        if (mEtPhoneNumber == null) {
            return "";
        }
        return Util.getEtString(mEtPhoneNumber);
    }

    public String getCaptcha() {
        if (mEtCaptcha == null) {
            return "";
        }
        return Util.getEtString(mEtCaptcha);
    }

    public String getNickName() {
        if (mEtNickName == null) {
            return "";
        }
        return Util.getEtString(mEtNickName);
    }

    /**
     * 获取Item的文本信息
     */
    private String getItemStr(@RelatedId int relatedId) {
        return getRelatedItem(relatedId).getVal();
    }

}
