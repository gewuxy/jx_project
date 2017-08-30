package yy.doctor.ui.activity.user;

import android.graphics.Color;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.form.OnFormObserver;
import lib.ys.ui.other.NavBar;
import lib.yy.model.form.BaseForm;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseFormActivity;
import yy.doctor.Constants.CaptchaType;
import yy.doctor.R;
import yy.doctor.dialog.HintDialog;
import yy.doctor.model.Profile;
import yy.doctor.model.form.Form;
import yy.doctor.model.form.FormType;
import yy.doctor.model.form.edit.EditCaptchaForm;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkAPISetter.RegisterAPI;
import yy.doctor.network.NetworkAPISetter.UserAPI;
import yy.doctor.ui.activity.MainActivity;
import yy.doctor.util.Util;

//import yy.doctor.network.NetworkAPISetter.ForgetAPI;

/**
 * 手机短信找回密码
 *
 * @auther HuoXuYu
 * @since 2017/7/19
 */

public class ForgetPwdPhoneActivity extends BaseFormActivity implements OnFormObserver {

    private final int KLogin = 1;
    private final int KCaptcha = 2;
    private final int KModify = 3;
    private final int KMaxCount = 4; // 10分钟内最多获取3次验证码

    private final long KCaptchaDuration = TimeUnit.MINUTES.toMillis(10);

    private HintDialog mDialog;
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

        int paddingLeft = fitDp(10);
        int paddingRight = fitDp(10);

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
                        showToast("该号码不是电话号，请输入正确的电话号码");
                        return;
                    }

                    mDialog = new HintDialog(this);
                    View view = inflate(R.layout.dialog_captcha);
                    TextView tv = (TextView) view.findViewById(R.id.captcha_tv_phone_number);
                    tv.setText(mPhone);

                    mDialog.addHintView(view);
                    mDialog.addButton("取消", v1 -> {
                        mDialog.dismiss();
                    });
                    mDialog.addButton("好", v1 -> {
                        if (mCount == 0) {
                            mStartTime = System.currentTimeMillis();
                        }
                        mCount++;
                        if (mCount > KMaxCount) {
                            long duration = System.currentTimeMillis() - mStartTime;
                            if (duration <= KCaptchaDuration) {
                                showToast("获取验证码太频繁");
                                mDialog.dismiss();
                                return;
                            } else {
                                mCount = 1;
                            }
                        }
                        // exeNetworkReq(KCaptcha, NetFactory.captcha(mPhone.replace(" ", ""), CaptchaType.re_fetch));
                        exeNetworkReq(KCaptcha, RegisterAPI.captcha(mPhone.replace(" ", ""), CaptchaType.re_fetch).build());
                        mDialog.dismiss();
                    });
                    mDialog.show();
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
        refresh(RefreshWay.dialog);
        exeNetworkReq(KModify, UserAPI.phone(getPhone(), getItemStr(RelatedId.captcha), getItemStr(RelatedId.pwd)).build());
    }

    private String getPhone() {
        return mPhone.toString().replace(" ", "");
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == KLogin) {
            return JsonParser.ev(r.getText(), Profile.class);
        } else {
            return JsonParser.error(r.getText());
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == KLogin) { //登陆
            stopRefresh();
            Result<Profile> r = (Result<Profile>) result;
            if (r.isSucceed()) {
                Profile.inst().update(r.getData());
                notify(NotifyType.login);
                startActivity(MainActivity.class);
                finish();
            } else {
                stopRefresh();
                showToast(r.getMessage());
            }
        } else if (id == KCaptcha) {//验证码
            Result r = (Result) result;
            if (r.isSucceed()) {
                ((EditCaptchaForm) getRelatedItem(RelatedId.captcha)).start();
                showToast("已发送验证码");
            } else {
                showToast(r.getMessage());
            }
        } else if (id == KModify) {//修改并设置新密码
            Result r = (Result) result;
            if (r.isSucceed()) {
                showToast("修改成功");
                //注册成功后登录,登录有结果才stopRefresh
                exeNetworkReq(KLogin, UserAPI.login(getPhone(), getItemStr(RelatedId.pwd), null).build());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
}
