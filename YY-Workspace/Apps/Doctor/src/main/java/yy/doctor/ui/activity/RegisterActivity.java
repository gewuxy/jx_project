package yy.doctor.ui.activity;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.text.Html;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

import lib.bd.location.Gps;
import lib.bd.location.Gps.TGps;
import lib.bd.location.Location;
import lib.bd.location.LocationNotifier;
import lib.bd.location.OnLocationNotify;
import lib.bd.location.Place;
import lib.bd.location.Place.TPlace;
import lib.network.model.NetworkResp;
import lib.network.model.err.NetError;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.form.FormEx.TForm;
import lib.ys.ui.other.NavBar;
import lib.ys.util.RegexUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.yy.model.form.BaseForm;
import lib.yy.network.BaseJsonParser.ErrorCode;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseFormActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.BaseHintDialog;
import yy.doctor.model.Profile;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;
import yy.doctor.model.form.edit.EditCaptchaForm;
import yy.doctor.model.form.text.intent.IntentForm.IntentType;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.sp.SpApp;
import yy.doctor.ui.activity.login.LoginActivity;
import yy.doctor.ui.activity.me.profile.SectionActivity;
import yy.doctor.ui.activity.me.profile.TitleActivity;
import yy.doctor.ui.activity.register.CaptchaActivity;
import yy.doctor.ui.activity.register.ProvinceActivity;
import yy.doctor.util.Util;

import static lib.ys.form.FormEx.TForm.val;

/**
 * 注册界面  7.1
 * <p>
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */
public class RegisterActivity extends BaseFormActivity implements OnEditorActionListener, OnLocationNotify {

    private final int KRegister = 0;
    private final int KLogin = 1;
    private final int KMaxCount = 3; // 10分钟内最多获取3次验证码
    private final long KCaptchaDuration = TimeUnit.MINUTES.toMillis(10);

    private EditText mEtActivatedCode;      //填写激活码
    private TextView mTvAgree;       //注册按钮的下一行字
    private TextView mTvActivatedCode;   //获取激活码
    private boolean mFlag;//密码是否可见

    private long mStartTime; // 开始计算10分钟间隔的时间
    private int mCount;//计算点击多少次


    private TextView mTvCaptcha;//获取验证码的textview
    private String mUserName;       //用户名
    private String mPwd;            //密码
    private View mTvReg;

    private BaseHintDialog mDialog;

    @IntDef({
            RelatedId.name,
            RelatedId.pwd,
            RelatedId.medicine,
            RelatedId.location,
            RelatedId.hospital,
            RelatedId.ActivatedCode,
            RelatedId.phone_number,
            RelatedId.department,
            RelatedId.captcha,
            RelatedId.doctor,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int name = 1;
        int pwd = 2;
        int medicine = 3;
        int location = 4;
        int hospital = 5;
        int ActivatedCode = 6;
        int phone_number = 7;
        int department = 8;
        int captcha = 9;
        int doctor = 10;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.register, this);
        bar.addViewRight(R.mipmap.register_scan, v -> {
            startActivity(ScanActivity.class);
        });
    }

    @Override
    public void initData() {
        super.initData();

        mFlag = true;
        mCount = 0;

        addItem(new Builder(FormType.et_phone_number)
                .related(RelatedId.phone_number)
                .hint(R.string.phone_number)
                .build());

        addItem(new Builder(FormType.register_divider).build());
        addItem(new Builder(FormType.et_captcha)
                .related(RelatedId.captcha)
                .textColor(R.color.register_captcha_text_selector)
                .hint(R.string.captcha)
                .enable(false)
                .build());

        addItem(new Builder(FormType.register_divider).build());
        addItem(new Builder(FormType.et_register_pwd)
                .related(RelatedId.pwd)
                .hint(R.string.pwd)
                .drawable(R.drawable.register_pwd_selector)
                .build());

        addItem(new Builder(FormType.register_divider).build());
        addItem(new Builder(FormType.et_register)
                .related(RelatedId.name)
                .hint(R.string.real_name)
                .build());

        addItem(new Builder(FormType.register_divider).build());
        addItem(new Builder(FormType.text_intent_no_name)
                .related(RelatedId.location)
                .hint(R.string.province_city_district)
                .intent(new Intent(this, ProvinceActivity.class).putExtra(Extra.KData, IntentType.location))
                .build());


        addItem(new Builder(FormType.register_divider).build());
        addItem(new Builder(FormType.text_intent_no_name)
                .related(RelatedId.hospital)
                .hint(R.string.choose_hospital)
                .intent(new Intent(this, HospitalActivity.class).putExtra(Extra.KData, IntentType.hospital))
//                .drawable(R.mipmap.hospital_level_other)
                .build());

        addItem(new Builder(FormType.register_divider).build());
        addItem(new Builder(FormType.text_intent_no_name)
                .related(RelatedId.medicine)
                .hint(R.string.medicine)
                .intent(new Intent(this, SectionActivity.class).putExtra(Extra.KData, IntentType.medicine))
                .build());

        addItem(new Builder(FormType.register_divider).build());
        addItem(new Builder(FormType.et_register)
                .related(RelatedId.department)
                .hint(yy.doctor.R.string.department)
                .build());

        addItem(new Builder(FormType.register_divider).build());
        addItem(new Builder(FormType.text_intent_no_name)
                .related(RelatedId.doctor)
                .hint(R.string.doctor)
                .intent(new Intent(this, TitleActivity.class).putExtra(Extra.KData, IntentType.doctor))
                .build());

        addItem(new Builder(FormType.register_divider).build());
    }

    @Override
    protected View createHeaderView() {
        return inflate(R.layout.layout_register_header);
    }

    @Override
    protected View createFooterView() {
        return inflate(R.layout.layout_register_footer);
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvReg = findView(R.id.register);

        mEtActivatedCode = findView(R.id.register_et_capcha);
        mTvActivatedCode = findView(R.id.register_tv_activated_code);

        mTvAgree = findView(R.id.register_tv_agree);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.register);
        setOnClickListener(mTvActivatedCode);
        mTvReg.setEnabled(false);

        String str = "点击<font color='#888888'>“注册”</font>即表示您同意";
        mTvAgree.setText(Html.fromHtml(str));
        mEtActivatedCode.setOnEditorActionListener(this);

        runOnUIThread(() -> {
            //检查有没有定位权限   没有的话直接弹dialog
            if (checkPermission(0, Permission.location)) {
                startLocation();
            } else {
                onLocationError();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_tv_activated_code:
                //mTvReg.setEnabled(true);
                startActivity(CaptchaActivity.class);
                break;
            case R.id.register:
                enroll();
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            enroll();
            return true;
        }
        return false;
    }


    /**
     * 注册操作
     */
    private void enroll() {

        // 判断空
        if (!check()) {
            mTvReg.setEnabled(true);
            return;
        }

        // 检查激活码是否为空
        String code = mEtActivatedCode.getText().toString().trim();
        if (TextUtil.isEmpty(code)) {
            showToast("请输入" + getString(R.string.title_fetch_captcha));
            return;
        }

        // 检查姓名 是否有特殊符号
        if (Util.checkNameLegal(getItemStr(RelatedId.name))) {
            showToast(R.string.input_real_name);
            return;
        }

        // 检查密码
        String strPwd = getItemStr(RelatedId.pwd);
        // String strPwdNg = getItemStr(RelatedId.pwd_sure);
        if (strPwd.length() < 6 || strPwd.length() > 24) {
            showToast(R.string.input_right_pwd_num);
            return;
        }
        // mPwd = strPwd;

        // 省市区
        String addresses = getItemStr(RelatedId.location);
        Place place = new Place(addresses);

        //注册
        refresh(RefreshWay.dialog);
        exeNetworkReq(KRegister, NetFactory.register()
                .username(mUserName)
                .linkman(getItemStr(RelatedId.name))
                .pwd(getItemStr(RelatedId.pwd))
                .province(place.getString(TPlace.province))
                .city(place.getString(TPlace.city))
                .area(place.getString(TPlace.district))
                .hospital(getItemStr(RelatedId.hospital))
                .invite(code)
                .build());
    }

    /**
     * 获取Item的文本信息
     */
    private String getItemStr(@RelatedId int relatedId) {
        return getRelatedItem(relatedId).getString(val);
    }

    @Override
    protected void onFormViewClick(View v, int position, Object related) {

        switch ((int) related) {
            case RelatedId.captcha: {
//                if (v.getId() == R.id.form_tv_text) {
//                    mCount++;
//                    String str = getRelatedItem(RelatedId.phone_number).getHolder().getEt().getText().toString();
//                    BaseHintDialog baseHintDialog = new BaseHintDialog(this);
//                    View view = inflate(R.layout.dialog_captcha);
//                    mCaptchaPhoneNumber = (TextView) view.findViewById(R.id.captcha_phone_number);
//                    mCaptchaPhoneNumber.setText(str);
//                    baseHintDialog.addHintView(view);
//                    baseHintDialog.addButton("好", v1 -> {
//                        if (mCount <= 3) {
//                            showToast("验证码是1234");//获取验证码输入验证码的逻辑，请求服务器，现假设一个验证码
//                        }
//                        baseHintDialog.dismiss();
//                        ((EditCaptchaForm) getRelatedItem(RelatedId.captcha)).change();
//                        if (mCountDown == null) {
//                            mCountDown = new CountDown();
//                            mCountDown.setListener(this);
//                        }
//                        mCountDown.start(TimeUnit.SECONDS.toSeconds(60));
//                    });
//                    baseHintDialog.addButton("取消", v1 -> {
//                        baseHintDialog.dismiss();
//                    });
//                    baseHintDialog.show();
//                }

                if (mCount == 0) {
                    mStartTime = System.currentTimeMillis();
                }

                mCount++;
                if (mCount > KMaxCount) {
                    long duration = System.currentTimeMillis() - mStartTime;
                    if (duration <= KCaptchaDuration) {
                        showToast("获取验证码太频繁");
                        return;
                    } else {
                        mCount = 1;
                    }
                }

                String phone = getRelatedItem(RelatedId.phone_number).getString(TForm.val);
                if (!RegexUtil.isMobileCN(phone.replace(" ", ""))) {
                    showToast("....不是电话号");
                    return;
                }

                BaseHintDialog dialog = new BaseHintDialog(this);

                View view = inflate(R.layout.dialog_captcha);
                TextView tv = (TextView) view.findViewById(R.id.captcha_tv_phone_number);
                tv.setText(phone);

                dialog.addHintView(view);
                dialog.addButton("好", v1 -> {

                    dialog.dismiss();

                    ((EditCaptchaForm) getRelatedItem(RelatedId.captcha)).start();
                });
                dialog.addButton("取消", v1 -> {
                    dialog.dismiss();
                });

                dialog.show();
            }
            break;
            case RelatedId.pwd: {
                mFlag = !mFlag;

                // FIXME: 逻辑放在form内部
                EditText et = getRelatedItem(RelatedId.pwd).getHolder().getEt();
                String content = getRelatedItem(RelatedId.pwd).getString(TForm.val);
                getRelatedItem(RelatedId.pwd).getHolder().getIv().setSelected(mFlag);
                if (!mFlag) {
                    et.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                et.setSelection(content.length());//光标移到最后
            }
            break;
        }
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        BaseForm form = getRelatedItem(RelatedId.captcha);
        if (type == NotifyType.province_finish) {
            Place place = (Place) data;
            String text = place.toString();

            getRelatedItem(RelatedId.location).save(text, text);
            refreshRelatedItem(RelatedId.location);
        } else if (type == NotifyType.fetch_message_captcha) {
            form.put(TForm.enable, true);
            refreshItem(form);
        } else if (type == NotifyType.disable_fetch_message_captcha) {
            form.put(TForm.enable, false);
            refreshItem(form);
        }
    }

    @Override
    public void onPermissionResult(int code, @PermissionResult int result) {
        switch (result) {
            case PermissionResult.granted: {
                Location.inst().start();
            }
            break;
            case PermissionResult.denied:
            case PermissionResult.never_ask: {
                onLocationError();
            }
            break;
        }
    }

    @Override
    public void onLocationResult(boolean isSuccess, Gps gps) {
        if (isSuccess) {
            //定位成功
            Place place = gps.getEv(TGps.place);
            if (place != null) {
                addOnPreDrawListener(new OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        TextView text = getRelatedItem(RelatedId.location).getHolder().getTvText();
                        text.setText(place.toString());

                        removeOnPreDrawListener(this);
                        return true;
                    }
                });
            } else {
                onLocationError();
            }
        } else {
            onLocationError();
        }

        stopLocation();
    }

    /**
     * 初始化Dialog
     */
    private void onLocationError() {
        YSLog.d("Gps", "失败");

        // FIXME: 失败的多种处理
        onNetworkError(0, new NetError(ErrorCode.KUnKnow, "定位失败"));

        //定位失败  显示dialog
        mDialog = new BaseHintDialog(this);
        mDialog.addHintView(inflate(R.layout.dialog_locate_fail));
        mDialog.addButton(getString(R.string.know), v -> mDialog.dismiss());
        mDialog.show();
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
        if (id == KLogin) {//登录
            stopRefresh();
            Result<Profile> r = (Result<Profile>) result;
            if (r.isSucceed()) {
                Profile.inst().update(r.getData());
                notify(NotifyType.login);
                startActivity(MainActivity.class);
                finish();
            } else {
                startActivity(LoginActivity.class);
                finish();
                showToast(r.getError());
            }
        } else {//注册
            Result r = (Result) result;
            if (r.isSucceed()) {
                //注册成功后登录,登录有结果才stopRefresh
                //保存用户名
                SpApp.inst().saveUserName(mUserName);
                //exeNetworkReq(KLogin, NetFactory.login(getItemStr(RelatedId.email), mPwd));
                YSLog.d("yaya", "_________________________");
            } else {
                stopRefresh();
                showToast(r.getError());
            }
        }
    }

    private void startLocation() {
        LocationNotifier.inst().add(this);
        Location.inst().start();
    }

    private void stopLocation() {
        LocationNotifier.inst().remove(this);
        Location.inst().stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDialog != null) {
            mDialog.dismiss();

        }

        stopLocation();
        Location.inst().onDestroy();
    }
}
