package yy.doctor.ui.activity.register;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import lib.bd.location.Gps;
import lib.bd.location.Gps.TGps;
import lib.bd.location.Location;
import lib.bd.location.LocationNotifier;
import lib.bd.location.OnLocationNotify;
import lib.bd.location.Place;
import lib.bd.location.Place.TPlace;
import lib.network.model.NetworkResp;
import lib.network.model.err.NetErrorBuilder;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.form.OnFormObserver;
import lib.ys.ui.other.NavBar;
import lib.ys.util.DeviceUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.view.ViewUtil;
import lib.yy.model.form.BaseForm;
import lib.yy.network.BaseJsonParser.ErrorCode;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseFormActivity;
import yy.doctor.Constants.CaptchaType;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.BaseHintDialog;
import yy.doctor.model.Profile;
import yy.doctor.model.form.Form;
import yy.doctor.model.form.FormType;
import yy.doctor.model.form.edit.EditCaptchaForm;
import yy.doctor.model.form.text.intent.IntentForm.IntentType;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.sp.SpApp;
import yy.doctor.ui.activity.MainActivity;
import yy.doctor.ui.activity.ScanActivity;
import yy.doctor.ui.activity.login.LoginActivity;
import yy.doctor.ui.activity.me.profile.SectionActivity;
import yy.doctor.ui.activity.me.profile.TitleActivity;
import yy.doctor.util.Util;

/**
 * 注册界面  7.1
 * <p>
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */
public class RegisterActivity extends BaseFormActivity
        implements OnEditorActionListener, OnLocationNotify, OnFormObserver {

    private final int KRegister = 0;
    private final int KLogin = 1;
    private final int KCaptcha = 2;
    private final int KMaxCount = 3; // 10分钟内最多获取3次验证码
    private final int KActivateCodeCheckStatus = 100;


    private final long KCaptchaDuration = TimeUnit.MINUTES.toMillis(10);

    private EditText mEtActivatedCode;      //填写激活码

    @IntDef({
            RelatedId.phone_number,
            RelatedId.captcha,
            RelatedId.pwd,
            RelatedId.name,
            RelatedId.location,
            RelatedId.hospital,
            RelatedId.special,
            RelatedId.department,
            RelatedId.title,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {

        int phone_number = 1;
        int captcha = 2;
        int pwd = 3;
        int name = 4;
        int location = 5;
        int hospital = 6;
        int special = 7;
        int department = 8;
        int title = 9;
    }

    private TextView mTvAgree;       //注册按钮的下一行字
    private TextView mTvActivatedCode;   //获取激活码

    private ImageView mIvCancel; // 激活码的“×”图标
    private long mStartTime; // 开始计算10分钟间隔的时间

    private int mCount;//计算点击多少次

    private View mTvReg;
    private BaseHintDialog mDialog;

    private String mPhone;
    private TextView mTvHeader;
    private View mLayoutCaptcha;

    private String mMasId;

    private int mEnableSize;
    private Set<Integer> mStatus;

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.register, this);
        bar.addViewRight(R.mipmap.register_scan, v -> startActivityForResult(ScanActivity.class, 0));
    }

    @Override
    public void initData() {
        super.initData();

        mCount = 0;

        mStatus = new HashSet<>();
        // 激活码不在form体系内，需要单独 +1
        mEnableSize = RelatedId.class.getDeclaredFields().length + 1;

        addItem(Form.create(FormType.et_phone_number)
                .related(RelatedId.phone_number)
                .observer(this)
                .hint(R.string.phone_number));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.et_captcha)
                .observer(this)
                .related(RelatedId.captcha)
                .textColorRes(R.color.register_captcha_text_selector)
                .hint(R.string.captcha)
                .enable(false));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.et_register_pwd)
                .observer(this)
                .related(RelatedId.pwd)
                .hint(R.string.pwd)
                .drawable(R.drawable.register_pwd_selector));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.et_register)
                .observer(this)
                .related(RelatedId.name)
                .hint(R.string.real_name));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_no_name)
                .observer(this)
                .related(RelatedId.location)
                .hint(R.string.province_city_district)
                .intent(new Intent(this, ProvinceActivity.class).putExtra(Extra.KData, IntentType.location))
                .type(IntentType.location));


        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_no_name)
                .observer(this)
                .related(RelatedId.hospital)
                .hint(R.string.choose_hospital)
                .intent(new Intent(this, HospitalActivity.class).putExtra(Extra.KData, IntentType.hospital))
                .type(IntentType.hospital));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_no_name)
                .observer(this)
                .related(RelatedId.special)
                .hint(R.string.special)
                .intent(new Intent(this, SectionActivity.class).putExtra(Extra.KData, IntentType.medicine))
                .type(IntentType.medicine));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.et_register)
                .observer(this)
                .related(RelatedId.department)
                .hint(yy.doctor.R.string.department));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_no_name)
                .observer(this)
                .related(RelatedId.title)
                .hint(R.string.title)
                .intent(new Intent(this, TitleActivity.class))
                .type(IntentType.doctor_title));

        addItem(Form.create(FormType.divider_margin));
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
        mEtActivatedCode = findView(R.id.register_et_captcha);
        mTvActivatedCode = findView(R.id.register_tv_activated_code);
        mIvCancel = findView(R.id.iv_activated_cancel);
        mTvAgree = findView(R.id.register_tv_agree);
        mTvHeader = findView(R.id.register_header);
        mLayoutCaptcha = findView(R.id.register_layout_captcha);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.register);
        setOnClickListener(mTvActivatedCode);
        setOnClickListener(mIvCancel);
        mTvReg.setEnabled(false);
        SpannableString s = new SpannableString("点击“注册”即表示您同意");
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#888888")), 2, 6, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        mTvAgree.setText(s);

        runOnUIThread(() -> {
            //检查有没有定位权限   没有的话直接弹dialog
            if (checkPermission(0, Permission.location)) {
                startLocation();
            } else {
                onLocationError();
            }
        });

        mEtActivatedCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtil.isEmpty(s)) {
                    mStatus.remove(KActivateCodeCheckStatus);
                } else {
                    mStatus.add(KActivateCodeCheckStatus);
                }

                setBtnStatus();

                if (TextUtil.isNotEmpty(s)) {
                    ViewUtil.showView(mIvCancel);
                } else {
                    ViewUtil.goneView(mIvCancel);
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_tv_activated_code:
                startActivity(CaptchaActivity.class);
                break;
            case R.id.register:
                enroll();
                break;
            case R.id.academic_iv_clean:
                mEtActivatedCode.setText("");
        }
    }

    //对键盘的处理
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
        mPhone = getRelatedItem(RelatedId.phone_number).getVal();
        // 判断空
        if (!check()) {
            return;
        }

        // 检查激活码是否为空
        String code = mEtActivatedCode.getText().toString().trim();
      /*  if (TextUtil.isEmpty(code)) {
            showToast("请输入" + getString(R.string.title_fetch_captcha));
            return;
        }*/

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

        // 省市区
        String addresses = getItemStr(RelatedId.location);
        Place place = new Place(addresses);
        //专科,按照空格来分
        String special = getItemStr(RelatedId.special);
        String[] s = special.split(" ");
        String category = s[0];
        String name = s[1];

        int id = getRelatedItem(RelatedId.hospital).getHolder().getIv().getId();
        String hosLevel = "三级";
        switch (id) {
            case R.id.level_three:
                hosLevel = "三级";
                break;
            case R.id.level_two:
                hosLevel = "二级";
                break;
            case R.id.level_one:
                hosLevel = "一级";
                break;
            case R.id.level_community:
                hosLevel = "社区卫生服务中心";
                break;
            case R.id.level_village:
                hosLevel = "乡镇卫生院";
                break;
            case R.id.level_clinic:
                hosLevel = "诊所";
                break;
            case R.id.level_other:
                hosLevel = "其他";
                break;
        }

        //注册
        refresh(RefreshWay.dialog);
        exeNetworkReq(KRegister, NetFactory.register()
                .mobile(getPhone())
                .captcha(getItemStr(RelatedId.captcha))
                .password(getItemStr(RelatedId.pwd))
                .linkman(getItemStr(RelatedId.name))
                .province(place.getString(TPlace.province))
                .city(place.getString(TPlace.city))
                .zone(place.getString(TPlace.district))
                .hospital(getItemStr(RelatedId.hospital))
                .hosLevel(hosLevel)//医院级别没返回来
                .category(category)//专科一级名称，要分开
                .name(name)//专科二级名称
                .department(getItemStr(RelatedId.department))//科室名称
                .title(getItemStr(RelatedId.title))//职称
                .invite(code)
                .masterId(mMasId)
                .build());
    }

    private String getPhone() {
        return mPhone.toString().replace(" ", "");
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

                    BaseHintDialog dialog = new BaseHintDialog(this);

                    View view = inflate(R.layout.dialog_captcha);
                    TextView tv = (TextView) view.findViewById(R.id.captcha_tv_phone_number);
                    tv.setText(mPhone);

                    dialog.addHintView(view);
                    dialog.addButton("好", v1 -> {
                        if (mCount == 0) {
                            mStartTime = System.currentTimeMillis();
                        }
                        mCount++;
                        if (mCount > KMaxCount) {
                            long duration = System.currentTimeMillis() - mStartTime;
                            if (duration <= KCaptchaDuration) {
                                showToast("获取验证码太频繁");
                                dialog.dismiss();
                                return;
                            } else {
                                mCount = 1;
                            }
                        }
                        exeNetworkReq(KCaptcha, NetFactory.captcha(mPhone.replace(" ", ""), CaptchaType.fetch));
                        dialog.dismiss();
                        ((EditCaptchaForm) getRelatedItem(RelatedId.captcha)).start();
                    });
                    dialog.addButton("取消", v1 -> {
                        dialog.dismiss();
                    });

                    dialog.show();
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
            String text = place.toString();

            getRelatedItem(RelatedId.location).save(text, text);
            refreshRelatedItem(RelatedId.location);
        } else if (type == NotifyType.fetch_message_captcha) {
            form.enable(true);
            refreshItem(form);
        } else if (type == NotifyType.disable_fetch_message_captcha) {
            form.enable(false);
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
            YSLog.d(TAG, place.toString());
            if (place != null) {
                addOnPreDrawListener(new OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        TextView locationText = getRelatedItem(RelatedId.location).getHolder().getTvText();
                        locationText.setText(place.toString());
                        getRelatedItem(RelatedId.location).save(locationText.toString(), locationText.toString());

                        TextView specialText = getRelatedItem(RelatedId.special).getHolder().getTvText();
                        specialText.setText("内科 普内科");
                        getRelatedItem(RelatedId.special).save(specialText.toString(), specialText.toString());
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

        onNetworkError(KRegister, NetErrorBuilder.create()
                .code(ErrorCode.KUnKnow)
                .message("定位失败")
                .build());

        if (!DeviceUtil.isNetworkEnabled()) {
            showToast("当前网络不可用,不可定位");
        } else {
            //有网但是定位失败  显示dialog
            mDialog = new BaseHintDialog(this);
            mDialog.addHintView(inflate(R.layout.dialog_locate_fail));
            mDialog.addButton(getString(R.string.know), v -> mDialog.dismiss());
            mDialog.show();
        }

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
        if (id == KLogin) {
            //登录
            stopRefresh();

            Result<Profile> r = (Result<Profile>) result;
            if (r.isSucceed()) {
                Profile.inst().update(r.getData());
                notify(NotifyType.login);
                startActivity(MainActivity.class);
                finish();
            } else {
                onNetworkError(id, r.getError());

                startActivity(LoginActivity.class);
                finish();
            }
        } else if (id == KCaptcha) {
            Result r = (Result) result;
            if (r.isSucceed()) {
                showToast("成功");
            } else {
                onNetworkError(id, r.getError());
            }
        } else {
            //注册
            Result r = (Result) result;
            if (r.isSucceed()) {
                showToast("成功");
                //注册成功后登录,登录有结果才stopRefresh
                //保存用户名
                SpApp.inst().saveUserName(getPhone());
                exeNetworkReq(KLogin, NetFactory.login(getPhone(), getItemStr(RelatedId.pwd), null));
                YSLog.d("yaya", "_________________________");
            } else {
                onNetworkError(id, r.getError());
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //resultCode == RESULT_FIRST_USER只是一个区别于之前的ESULT_OK
        if (resultCode == RESULT_FIRST_USER && data != null) {
            String name = data.getStringExtra(Extra.KData);
            mMasId = data.getStringExtra(Extra.KId);
            mTvHeader.setText("该账号（价值69.9元），由" + name + "为您免费提供");
            goneView(mLayoutCaptcha);
            mStatus.add(KActivateCodeCheckStatus);
        }
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
            mTvReg.setEnabled(true);
        } else {
            // 按钮不能点击
            mTvReg.setEnabled(false);
        }
    }
}
