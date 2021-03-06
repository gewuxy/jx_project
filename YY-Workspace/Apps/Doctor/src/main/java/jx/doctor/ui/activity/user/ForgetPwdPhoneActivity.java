package jx.doctor.ui.activity.user;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.form.OnFormObserver;
import lib.ys.ui.other.NavBar;
import lib.ys.util.PackageUtil;
import lib.jx.model.form.BaseForm;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.activity.base.BaseFormActivity;
import jx.doctor.Constants.CaptchaType;
import jx.doctor.R;
import jx.doctor.dialog.HintDialog;
import jx.doctor.model.Profile;
import jx.doctor.model.form.Form;
import jx.doctor.model.form.FormType;
import jx.doctor.model.form.edit.EditCaptchaForm;
import jx.doctor.network.JsonParser;
import jx.doctor.network.NetworkApiDescriptor.RegisterAPI;
import jx.doctor.network.NetworkApiDescriptor.UserAPI;
import jx.doctor.ui.activity.MainActivity;
import jx.doctor.util.Util;

//import yy.doctor.network.NetworkApiDescriptor.ForgetAPI;

/**
 * 手机短信找回密码
 *
 * @auther HuoXuYu
 * @since 2017/7/19
 */

public class ForgetPwdPhoneActivity extends BaseFormActivity implements OnFormObserver {

    private final int KIdLogin = 0;
    private final int KIdCaptcha = 1;
    private final int KIdModify = 2;

    private final int KMaxCount = 3; // 10分钟内最多获取3次验证码

    private final long KCaptchaDuration = TimeUnit.MINUTES.toMillis(10);

    private long mStartTime; // 开始计算10分钟间隔的时间
    private int mCount;//计算点击多少次

    private TextView mTv;
    private String mPhone;

    @IntDef({
            RelatedId.phone_number,
            RelatedId.pwd,
            RelatedId.captcha,
    })

    private @interface RelatedId {
        int phone_number = 1;
        int pwd = 2;
        int captcha = 3;
    }

    private int mEnableSize;
    private Set<Integer> mStatus;

    @Override
    public void initData() {
        super.initData();

        mCount = 0;

        mStatus = new HashSet<>();
        mEnableSize = RelatedId.class.getDeclaredFields().length;

        addItem(Form.create(FormType.et_phone_number)
                .related(RelatedId.phone_number)
                .layout(R.layout.form_edit_forget_phone_number)
                .observer(this)
                .hint(R.string.input_phone));

        addItem(Form.create(FormType.et_captcha)
                .related(RelatedId.captcha)
                .layout(R.layout.form_edit_forget_captcha)
                .textColorRes(R.color.register_captcha_text_selector)
                .hint(R.string.captcha)
                .observer(this)
                .enable(false));

        addItem(Form.create(FormType.et_pwd)
                .related(RelatedId.pwd)
                .layout(R.layout.form_edit_forget_pwd)
                .hint(R.string.input_new_pwd)
                .observer(this)
                .drawable(R.drawable.register_pwd_selector));
    }

    @Override
    protected View createFooterView() {
        return inflate(R.layout.activity_forget_pwd_phone);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.forget_pwd, this);
    }

    @Override
    public void findViews() {
        super.findViews();
        mTv = findView(R.id.forget_tv_phone_login);
    }

    @Override
    public void setViews() {
        super.setViews();
        setBackgroundColor(Color.WHITE);

        setOnClickListener(R.id.forget_tv_phone_login);
        mTv.setEnabled(false);

    }

    /**
     * 获取Item的文本信息
     */
    private String getItemStr(@RelatedId int relatedId) {
        return getRelatedItem(relatedId).getVal();
    }

    @Override
    protected void onFormViewClick(View v, int position, Object related) {

        switch ((int) related) {
            case RelatedId.captcha: {
                if (v.getId() == R.id.form_tv_text) {
                    mPhone = getRelatedItem(RelatedId.phone_number).getVal();
                    if (!Util.isMobileCN(mPhone)) {
                        showToast(R.string.not_phone_number);
                        return;
                    }

                    HintDialog dialog = new HintDialog(this);

                    View view = inflate(R.layout.dialog_captcha);
                    TextView tv = (TextView) view.findViewById(R.id.captcha_tv_phone_number);
                    tv.setText(mPhone);

                    dialog.addHintView(view);
                    dialog.addBlueButton(R.string.cancel);
                    dialog.addBlueButton(R.string.well, v1 -> {
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
                        exeNetworkReq(KIdCaptcha, RegisterAPI.captcha(mPhone.replace(" ", ""), CaptchaType.re_fetch).build());
                    });
                    dialog.show();
                }
            }
            break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.forget_tv_phone_login: {
                doModify();
            }
            break;

        }
    }

    private void doModify() {
        mPhone = getRelatedItem(RelatedId.phone_number).getVal();
        if (!check()) {
            return;
        }

        // 检查密码
        String strPwd = getItemStr(RelatedId.pwd);
        if (!strPwd.matches(Util.symbol())) {
            showToast(R.string.input_special_symbol);
            return;
        }

        if (strPwd.length() < 6) {
            showToast(R.string.input_right_pwd_num);
            return;
        }
        refresh(RefreshWay.dialog);
        exeNetworkReq(KIdModify, UserAPI.phone(getPhone(), getItemStr(RelatedId.captcha), getItemStr(RelatedId.pwd)).build());
    }

    private String getPhone() {
        return mPhone.toString().replace(" ", "");
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        if (id == KIdLogin) {
            return JsonParser.ev(resp.getText(), Profile.class);
        } else {
            return JsonParser.error(resp.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (id == KIdLogin) { //登陆
            stopRefresh();
            if (r.isSucceed()) {
                Profile.inst().update((Profile) r.getData());
                notify(NotifyType.login);
                startActivity(MainActivity.class);
                finish();
            } else {
                stopRefresh();
                showToast(r.getMessage());
            }
        } else if (id == KIdCaptcha) {//验证码
            if (r.isSucceed()) {
                ((EditCaptchaForm) getRelatedItem(RelatedId.captcha)).start();
                showToast(R.string.send_captcha);
            } else {
                showToast(r.getMessage());
            }
        } else if (id == KIdModify) {//修改并设置新密码
            if (r.isSucceed()) {
                showToast(R.string.pwd_change_success);
                //注册成功后登录,登录有结果才stopRefresh
                exeNetworkReq(KIdLogin, UserAPI.login(getPhone(), getItemStr(RelatedId.pwd), null, PackageUtil.getMetaValue("MASTER_ID")).build());
            } else {
                stopRefresh();
                showToast(r.getMessage());
            }
        }
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        BaseForm form = getRelatedItem(RelatedId.captcha);
        if (type == NotifyType.login) {
            finish();
        } else if (type == NotifyType.fetch_message_captcha) {
            form.enable(true);
        } else if (type == NotifyType.disable_fetch_message_captcha) {
            form.enable(false);
        }
        refreshItem(form);

    }

    @Override
    public void callback(Object... params) {
        int position = (int) params[0];
        boolean valid = (boolean) params[1];

        if (valid) {
            if (!mStatus.contains(position)) {
                mStatus.add(position);
            }
        } else {
            mStatus.remove(position);
        }
        setBtnStatus();
    }

    /**
     * 根据填写的资料完成度设置注册按钮是否可以点击
     */
    private void setBtnStatus() {
        if (mStatus.size() == mEnableSize) {
            // 按钮可以点击
            mTv.setEnabled(true);
        } else {
            // 按钮不能点击
            mTv.setEnabled(false);
        }
    }
}
