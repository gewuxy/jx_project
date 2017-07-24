package yy.doctor.ui.activity;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.text.Editable;
import android.text.Html;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
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
import lib.ys.util.TextUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.yy.model.form.BaseForm;
import lib.yy.network.BaseJsonParser.ErrorCode;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseFormActivity;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.BaseHintDialog;
import yy.doctor.model.Pcd;
import yy.doctor.model.Profile;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;
import yy.doctor.model.form.edit.EditCaptchaForm;
import yy.doctor.model.form.text.TextRegisterIntentForm.IntentType;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.sp.SpApp;
import yy.doctor.ui.activity.login.LoginActivity;
import yy.doctor.ui.activity.me.profile.SectionActivity;
import yy.doctor.ui.activity.me.profile.TitleActivity;
import yy.doctor.ui.activity.register.CaptchaActivity;
import yy.doctor.ui.activity.register.ProvinceActivity;
import yy.doctor.ui.activity.register.ScanActivity;
import yy.doctor.util.Util;


/**
 * 注册界面  7.1
 * <p>
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */
public class RegisterActivity extends BaseFormActivity implements OnEditorActionListener,OnLocationNotify{

    private static final int KRegister = 0;
    private static final int KLogin = 1;

    private EditText mEtActivatedCode;      //填写激活码
    private TextView mTvAgree;       //注册按钮的下一行字
    private TextView mTvActivatedCode;   //获取激活码
    private TextView mCaptchaPhoneNumber; //验证码提示的对话框中注册的手机
    private boolean flag;//密码是否可见
    private int count;//计算点击多少次


    private TextView mTvCaptcha;//获取验证码的textview
    private String mUserName;       //用户名
    private String mPwd;            //密码
    private CountDown mCountDown;
    private int mRelatedId;
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

        flag = true;
        count = 0;
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
        addItem(new Builder(FormType.text_register_intent)
                .related(RelatedId.location)
                .hint(R.string.province_city_district)
                .intent(new Intent(this, ProvinceActivity.class).putExtra(Extra.KData, IntentType.location))
                .build());


        addItem(new Builder(FormType.register_divider).build());
        addItem(new Builder(FormType.text_register_intent)
                .related(RelatedId.hospital)
                .hint(R.string.choose_hospital)
                .intent(new Intent(this, HospitalActivity.class).putExtra(Extra.KData, IntentType.hospital))
                .drawable(R.mipmap.hospital_level_other)
                .build());

        addItem(new Builder(FormType.register_divider).build());
        addItem(new Builder(FormType.text_register_intent)
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
        addItem(new Builder(FormType.text_register_intent)
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
        mTvReg.setEnabled(false);
        mEtActivatedCode = findView(R.id.register_et_capcha);
        mTvActivatedCode = findView(R.id.register_tv_ActivatedCode);
        mTvAgree = findView(R.id.register_tv_agree);
        mCaptchaPhoneNumber = findView(R.id.captcha_phone_number);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.register);
        setOnClickListener(mTvActivatedCode);


        //检查有没有定位权限   没有的话直接弹dialog
        if (checkPermission(0, Permission.location)) {
            Location.inst().start();
        } else {
            onLocationError();
        }

        LocationNotifier.inst().add(this);

        String str = "点击<font color='#888888'>“注册”</font>即表示您同意";
        mTvAgree.setText(Html.fromHtml(str));
        mEtActivatedCode.setOnEditorActionListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_tv_ActivatedCode:
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
        String[] pcd = new String[Pcd.KMaxCount];
        String[] address = addresses.split(Pcd.KSplit);
        for (int i = 0; i < address.length; ++i) {
            pcd[i] = address[i];
        }

        //注册
        refresh(RefreshWay.dialog);
        exeNetworkReq(KRegister, NetFactory.register()
                .username(mUserName)
                .linkman(getItemStr(RelatedId.name))
                .pwd(getItemStr(RelatedId.pwd))
                .province(pcd[Pcd.KProvince])
                .city(pcd[Pcd.KCity])
                .area(pcd[Pcd.KDistrict])
                .hospital(getItemStr(RelatedId.hospital))
                .invite(code)
                .build());
    }

    /**
     * 获取Item的文本信息
     */
    private String getItemStr(@RelatedId int relatedId) {
        return getRelatedItem(relatedId).getString(TForm.val);
    }

    @Override
    protected void onFormViewClick(View v, int position, Object related) {
        @RelatedId int i = getItem(position).getInt(TForm.related);
        mRelatedId = i;
        switch (mRelatedId) {
            case RelatedId.captcha: {

                count++;
                String str = getRelatedItem(RelatedId.phone_number).getHolder().getEt().getText().toString();
                BaseHintDialog baseHintDialog = new BaseHintDialog(this);
                View view = inflate(R.layout.dialog_captcha);
                mCaptchaPhoneNumber = (TextView) view.findViewById(R.id.captcha_phone_number);
                mCaptchaPhoneNumber.setText(str);
                baseHintDialog.addHintView(view);
                baseHintDialog.addButton("好", v1 -> {
                    if (count <= 3) {
                        showToast("验证码是1234");//获取验证码输入验证码的逻辑，请求服务器，现假设一个验证码
                    }
                    baseHintDialog.dismiss();
                    ((EditCaptchaForm) getRelatedItem(RelatedId.captcha)).change();
                    if (mCountDown == null) {
                        mCountDown = new CountDown();
                        mCountDown.setListener(new OnCountDownListener() {
                            @Override
                            public void onCountDown(long remainCount) {
                                if (remainCount != 0) {
                                    if (count > 3) {
                                        showToast("获取验证码太频繁");
                                    }
                                }
                            }

                            @Override
                            public void onCountDownErr() {
                            }
                        });
                    }
                    mCountDown.start(TimeUnit.SECONDS.toSeconds(60));
                });
                baseHintDialog.addButton("取消", v1 -> {
                    baseHintDialog.dismiss();
                });
                baseHintDialog.show();


            }
            break;
            case RelatedId.pwd: {
                flag = !flag;
                EditText et = getRelatedItem(RelatedId.pwd).getHolder().getEt();
                Editable content = getRelatedItem(RelatedId.pwd).getHolder().getEt().getText();
                getRelatedItem(RelatedId.pwd).getHolder().getIv().setSelected(flag);
                if (!flag) {
                    getRelatedItem(RelatedId.pwd).getHolder().getEt().setTransformationMethod(PasswordTransformationMethod.getInstance());
                    et.setSelection(content.length());//光标移到最后
                } else {
                    getRelatedItem(RelatedId.pwd).getHolder().getEt().setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    et.setSelection(content.length());
                }
            }
            break;
        }
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {
        BaseForm form = getRelatedItem(RelatedId.captcha);
        if (type == NotifyType.province_finish) {
            Place place = (Place) data;

            String p = place.getString(TPlace.province);
            String c = place.getString(TPlace.city);
            String d = place.getString(TPlace.district);
            String pcdStr = Util.generatePcd(p, c, d);

            getRelatedItem(RelatedId.location).put(TForm.name, pcdStr);
            getRelatedItem(RelatedId.location).save(pcdStr, pcdStr);
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
            String mLocation = Util.generatePcd(place.getString(TPlace.province), place.getString(TPlace.city), place.getString(TPlace.district));
            TextView text = getRelatedItem(RelatedId.location).getHolder().getTvText();
            text.setText(mLocation);
        }else {
            //定位失败  显示dialog
            // FIXME: 失败
            YSLog.d("Gps", "失败");
            onLocationError();
        }
    }
    /**
     * 初始化Dialog
     */
    private void onLocationError() {
        onNetworkError(0, new NetError(ErrorCode.KUnKnow, "定位失败"));

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

    @Override
    public void onNetworkError(int id, NetError error) {
        super.onNetworkError(id, error);

    }

    @Override
    protected void onDestroy() {
        EditCaptchaForm item = (EditCaptchaForm) getRelatedItem(RelatedId.captcha);
        item.recycle();
        if (mCountDown != null) {
            mCountDown.recycle();
        }

        super.onDestroy();
    }
}
