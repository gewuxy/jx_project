package yy.doctor.ui.activity.user.register;

import android.content.Intent;
import android.os.Bundle;
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
import lib.bd.location.Location;
import lib.bd.location.LocationNotifier;
import lib.bd.location.OnLocationNotify;
import lib.network.model.NetworkReq;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.form.OnFormObserver;
import lib.ys.ui.other.NavBar;
import lib.ys.util.DeviceUtil;
import lib.ys.util.PackageUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionChecker;
import lib.ys.util.permission.PermissionResult;
import lib.ys.util.res.ResLoader;
import lib.yy.model.form.BaseForm;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseFormActivity;
import yy.doctor.Constants.CaptchaType;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.HintDialog;
import yy.doctor.dialog.HintDialogMain;
import yy.doctor.model.Place;
import yy.doctor.model.Place.TPlace;
import yy.doctor.model.Profile;
import yy.doctor.model.form.Form;
import yy.doctor.model.form.FormType;
import yy.doctor.model.form.edit.EditCaptchaForm;
import yy.doctor.model.form.text.intent.IntentForm.IntentType;
import yy.doctor.model.hospital.HospitalLevel;
import yy.doctor.model.hospital.HospitalLevel.THospitalLevel;
import yy.doctor.model.hospital.HospitalName;
import yy.doctor.model.hospital.HospitalName.THospitalName;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkApiDescriptor.RegisterAPI;
import yy.doctor.network.NetworkApiDescriptor.UserAPI;
import yy.doctor.network.UrlUtil;
import yy.doctor.sp.SpApp;
import yy.doctor.sp.SpUser;
import yy.doctor.ui.activity.CommonWebViewActivityRouter;
import yy.doctor.ui.activity.MainActivity;
import yy.doctor.ui.activity.me.profile.SectionActivity;
import yy.doctor.ui.activity.me.profile.TitleActivity;
import yy.doctor.ui.activity.user.PcdActivity;
import yy.doctor.ui.activity.user.hospital.BaseHospitalActivity.FromType;
import yy.doctor.ui.activity.user.hospital.HospitalActivity;
import yy.doctor.util.Util;
import yy.doctor.util.input.InputFilterChineseImpl;
import yy.doctor.util.input.InputFilterSpace;

/**
 * 注册界面  7.1
 * <p>
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */
public class RegisterActivity extends BaseFormActivity implements
        OnEditorActionListener,
        OnLocationNotify,
        OnFormObserver,
        TextWatcher {

    private final int KIdRegister = 0;
    private final int KIdLogin = 1;
    private final int KIdCaptcha = 2;
    private final int KReturnCode = 101;
    private final int KReturnCode2 = 102;

    private final int KActivateCodeCheckStatus = 100;

    private final int KMaxCount = 3; // 最多获取3次验证码
    private final long KCaptchaDuration = TimeUnit.MINUTES.toMillis(10); // 10分钟

    private final String KUrlDisclaimer = UrlUtil.getHostName() + "api/register/get_protocol"; // 免责声明
    private final String KUrlActivityCode = UrlUtil.getHostName() + "api/register/get_invite_code"; // 服务协议

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

    private View mLayoutCaptcha; // 激活码这个Item
    private View mTvReg; // 注册按钮
    private TextView mTvHeader; // 提示语
    private TextView mTvAgree; // 注册按钮的下一行字
    private EditText mEtActivatedCode; // 激活码
    private ImageView mIvCancel; // 激活码的“×”图标

    private int mHospitalLevel; // 医院等级id

    private int mCount; // 计算点击多少次
    private long mStartTime; // 开始计算10分钟间隔的时间

    private String mMasId; // 二维码返回的id

    private int mEnableSize; // 完成个数
    private Set<Integer> mStatus; // 记录要完成的内容

    @Override
    public void initData(Bundle state) {
        super.initData(state);

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
        addItem(Form.create(FormType.et_pwd)
                .observer(this)
                .related(RelatedId.pwd)
                .hint(R.string.pwd)
                .drawable(R.drawable.register_pwd_selector)
                .illegality(true));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.et)
                .observer(this)
                .related(RelatedId.name)
                .layout(R.layout.form_edit_no_text)
                .input(new InputFilterChineseImpl())
                .limit(18)
                .hint(R.string.real_name));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_no_name)
                .observer(this)
                .related(RelatedId.location)
                .hint(R.string.province_city_district)
                .intent(new Intent(this, PcdActivity.class))
                .type(IntentType.location));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_no_name)
                .observer(this)
                .related(RelatedId.hospital)
                .hint(R.string.choose_hospital)
                .intent(new Intent(this, HospitalActivity.class).putExtra(Extra.KData, FromType.register))
                .type(IntentType.hospital));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_no_name)
                .related(RelatedId.special)
                .observer(this)
                .intent(new Intent(this, SectionActivity.class))
                .type(IntentType.medicine))
                .save(getString(R.string.special), getString(R.string.special));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.et)
                .observer(this)
                .related(RelatedId.department)
                .layout(R.layout.form_edit_no_text)
                .input(new InputFilterSpace())
                .limit(24)
                .hint(yy.doctor.R.string.department));

        addItem(Form.create(FormType.divider_margin));
        addItem(Form.create(FormType.text_intent_no_name)
                .observer(this)
                .related(RelatedId.title)
                .intent(new Intent(this, TitleActivity.class))
                .type(IntentType.doctor_title))
                .save(getString(R.string.title), getString(R.string.title));

        addItem(Form.create(FormType.divider_margin));
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.register, this);
        bar.addViewRight(R.drawable.register_scan, v -> {
            if (checkPermission(0, Permission.camera)) {
                startActivityForResult(ScanActivity.class, 0);
            }
        });
    }

    @Override
    public void findViews() {
        super.findViews();

        mTvReg = findView(R.id.register);
        mEtActivatedCode = findView(R.id.register_et_captcha);
        mIvCancel = findView(R.id.iv_activated_cancel);
        mTvAgree = findView(R.id.register_tv_agree);
        mTvHeader = findView(R.id.register_header);
        mLayoutCaptcha = findView(R.id.register_layout_captcha);
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnClickListener(R.id.register_tv_activated_code);
        setOnClickListener(R.id.help_and_feedback_footer_tv_agreement);
        setOnClickListener(mIvCancel);
        setOnClickListener(mTvReg);

        mTvReg.setEnabled(false);

        SpannableString s = new SpannableString(getString(R.string.you_agree));
        s.setSpan(new ForegroundColorSpan(ResLoader.getColor(R.color.text_888)), 3, 5, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        mTvAgree.setText(s);

        // 静默检查定位权限
        if (PermissionChecker.allow(RegisterActivity.this, Permission.location)) {
            startLocation();
        }

        mEtActivatedCode.addTextChangedListener(this);
        if (!DeviceUtil.isNetworkEnabled()) {
            showToast(R.string.network_disabled);
        }
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_tv_activated_code: {
                CommonWebViewActivityRouter.create(getString(R.string.title_fetch_captcha), KUrlActivityCode)
                        .route(this);
            }
            break;
            case R.id.register: {
                register();
            }
            break;
            case R.id.iv_activated_cancel: {
                mEtActivatedCode.setText("");
            }
            break;
            case R.id.help_and_feedback_footer_tv_agreement: {
                CommonWebViewActivityRouter.create(getString(R.string.service_agreement), KUrlDisclaimer)
                        .route(this);
            }
        }
    }

    @Override
    protected void onFormViewClick(View v, int position, Object related) {
        switch ((int) related) {
            case RelatedId.captcha: {
                if (v.getId() == R.id.form_tv_text) {

                    View view = inflate(R.layout.dialog_captcha);
                    TextView tv = (TextView) view.findViewById(R.id.captcha_tv_phone_number);
                    String phone = getItemStr(RelatedId.phone_number);
                    tv.setText(phone);

                    HintDialog dialog = new HintDialog(this);
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
                        exeNetworkReq(KIdCaptcha, RegisterAPI.captcha(getPhone(), CaptchaType.fetch).build());
                    });
                    dialog.show();
                }
            }
            break;
        }
    }

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
            goneView(mIvCancel);
        } else {
            mStatus.add(KActivateCodeCheckStatus);
            showView(mIvCancel);
        }

        setBtnStatus();
    }

    @Override
    public void onPermissionResult(int code, @PermissionResult int result) {
        switch (result) {
            case PermissionResult.granted: {
                startActivityForResult(ScanActivity.class, 0);
            }
            break;
            case PermissionResult.denied:
            case PermissionResult.never_ask: {
                showToast(getString(R.string.user_photo_permission));
            }
            break;
        }
    }

    @Override
    public void onLocationResult(boolean isSuccess, Gps gps) {
        stopLocation();
        if (!isSuccess) {
            return;
        }

        addOnPreDrawListener(new OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                String place = new Place(gps).getDesc();
                YSLog.d(TAG, place);
                getRelatedItem(RelatedId.location).save(place, place);
                refreshRelatedItem(RelatedId.location);
                removeOnPreDrawListener(this);
                return true;
            }
        });
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
        if (id == KIdLogin) {
            //登录
            stopRefresh();

            if (r.isSucceed()) {
                Profile.inst().update((Profile) r.getData());
                SpUser.inst().updateProfileRefreshTime();
                notify(NotifyType.login);
                startActivity(MainActivity.class);
                finish();
            } else {
                onNetworkError(id, r.getError());
                finish();
            }
        } else if (id == KIdCaptcha) {
            if (r.isSucceed()) {
                ((EditCaptchaForm) getRelatedItem(RelatedId.captcha)).start();
                showToast(R.string.send_captcha);
            } else {
                onNetworkError(id, r.getError());
            }
        } else {
            //注册
            if (r.getCode() == KReturnCode || r.getCode() == KReturnCode2) {
                stopRefresh();
                HintDialogMain d = new HintDialogMain(this);
                d.setHint(getString(R.string.phone_have_been_register));
                d.addBlueButton(R.string.cancel);
                d.addBlueButton(R.string.immediately_login, v -> finish());
                d.show();
                return;
            }
            if (r.isSucceed()) {
                //注册成功后登录,登录有结果才stopRefresh
                //保存用户名
                SpApp.inst().saveUserName(getPhone());
                exeNetworkReq(KIdLogin, UserAPI.login(getPhone(), getItemStr(RelatedId.pwd), null, PackageUtil.getMetaValue("MASTER_ID")).build());
            } else {
                onNetworkError(id, r.getError());
            }
        }
    }

    /**
     * 对键盘的处理
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            register();
            return true;
        }
        return false;
    }

    /**
     * 注册操作
     */
    private void register() {
        String phone = getItemStr(RelatedId.phone_number);
        if (!Util.isMobileCN(phone)) {
            showToast(R.string.not_phone_number);
            return;
        }

        // 密码
        String strPwd = getItemStr(RelatedId.pwd);
        if (!strPwd.matches(Util.symbol())) {
            showToast(R.string.input_special_symbol);
            return;
        }
        if (strPwd.length() < 6) {
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

        mHospitalLevel = getRelatedItem(RelatedId.hospital).getData();

        // 激活码
        String code = mEtActivatedCode.getText().toString().trim();

        //注册
        refresh(RefreshWay.dialog);
        NetworkReq r = RegisterAPI.reg()
                .mobile(getPhone())
                .captcha(getItemStr(RelatedId.captcha))
                .password(getItemStr(RelatedId.pwd))
                .linkman(getItemStr(RelatedId.name))
                .province(place.getString(TPlace.province))
                .city(place.getString(TPlace.city))
                .zone(place.getString(TPlace.district))
                .hospital(getItemStr(RelatedId.hospital))
                .hospitalLevel(mHospitalLevel) // 医院级别
                .category(category) // 专科一级名称，要分开
                .name(name) // 专科二级名称
                .department(getItemStr(RelatedId.department)) // 科室名称
                .title(getItemStr(RelatedId.title)) // 职称
                .invite(code)
                .masterId(mMasId)
                .build();
        exeNetworkReq(KIdRegister, r);

    }

    private String getPhone() {
        return getItemStr(RelatedId.phone_number).replace(" ", "");
    }

    /**
     * 获取Item的文本信息
     */
    private String getItemStr(@RelatedId int relatedId) {
        return getRelatedItem(relatedId).getVal();
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
    public void onNotify(@NotifyType int type, Object data) {
        switch (type) {
            case NotifyType.pcd_selected: {
                if (data instanceof Place) {
                    Place place = (Place) data;
                    String text = place.getDesc();
                    getRelatedItem(RelatedId.location).save(text, text);
                }
                refreshRelatedItem(RelatedId.location);
            }
            break;
            case NotifyType.fetch_message_captcha: {
                BaseForm form = getRelatedItem(RelatedId.captcha);
                form.enable(true);
                refreshItem(form);
            }
            break;
            case NotifyType.disable_fetch_message_captcha: {
                BaseForm form = getRelatedItem(RelatedId.captcha);
                form.enable(false);
                refreshItem(form);
            }
            break;
            case NotifyType.hospital_finish: {
                if (data instanceof HospitalName) {
                    HospitalName h = (HospitalName) data;
                    HospitalLevel l = h.get(THospitalName.level);
                    String hospital = h.getString(THospitalName.name);
                    BaseForm item = getRelatedItem(RelatedId.hospital);
                    item.save(hospital, hospital);
                    item.url(l.getString(THospitalLevel.picture));
                    item.data(l.getInt(THospitalLevel.id));
                }
                refreshRelatedItem(RelatedId.hospital);
        }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //resultCode == RESULT_FIRST_USER只是一个区别于之前的RESULT_OK
        if (resultCode == RESULT_FIRST_USER && data != null) {
            String name = data.getStringExtra(Extra.KData);
            mMasId = data.getStringExtra(Extra.KId);
            mTvHeader.setText(String.format(getString(R.string.free_provide), name));
            goneView(mLayoutCaptcha);
            mStatus.add(KActivateCodeCheckStatus);
        }else if (resultCode == RESULT_FIRST_USER && data == null) {
            mTvHeader.setText("请填写以下信息");
            showView(mLayoutCaptcha);
            mStatus.remove(KActivateCodeCheckStatus);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopLocation();
        Location.inst().onDestroy();
    }

    @Override
    public void callback(Object... params) {
        int related = (int) params[0];
        boolean valid = (boolean) params[1];

        if (valid) {
            if (!mStatus.contains(related)) {
                mStatus.add(related);
            }
        } else {
            mStatus.remove(related);
        }

        setBtnStatus();
    }

    /**
     * 根据填写的资料完成度设置注册按钮是否可以点击
     */
    private void setBtnStatus() {
        if (mTvReg == null) {
            return;
        }
        if (mStatus.size() == mEnableSize) {
            // 按钮可以点击
            mTvReg.setEnabled(true);
        } else {
            // 按钮不能点击
            mTvReg.setEnabled(false);
        }
    }
}
