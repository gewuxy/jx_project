package yy.doctor.ui.activity.register;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.bd.location.Place;
import lib.bd.location.Place.TPlace;
import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.form.FormEx.TFormElem;
import lib.ys.ui.other.NavBar;
import lib.ys.util.RegexUtil;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseFormActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.Pcd;
import yy.doctor.model.Profile;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.sp.SpApp;
import yy.doctor.ui.activity.LoginActivity;
import yy.doctor.ui.activity.MainActivity;
import yy.doctor.util.Util;
import yy.doctor.view.AutoCompleteEditText;

/**
 * 注册界面
 * <p>
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */
public class RegisterActivity extends BaseFormActivity implements OnEditorActionListener {

    private static final int KRegister = 0;
    private static final int KLogin = 1;

    private AutoCompleteEditText mEtEmail;
    private EditText mEtCapcha;      //填写激活码
    private TextView mTvCapcha;   //获取激活码
    private TextView mTvRegister;     //注册
    private String mUserName;       //用户名
    private String mPwd;            //密码

    @IntDef({
            RelatedId.name,
            RelatedId.pwd,
            RelatedId.pwd_sure,
            RelatedId.location,
            RelatedId.hospital,
            RelatedId.capcha,
            RelatedId.phone_number,
            RelatedId.email,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int name = 1;
        int pwd = 2;
        int pwd_sure = 3;
        int location = 4;
        int hospital = 5;
        int capcha = 6;
        int phone_number = 7;
        int email = 8;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.register, this);
    }

    @Override
    public void initData() {
        super.initData();

        addItem(new Builder(FormType.et_email)
                .related(RelatedId.email)
                .hint("电子邮箱")
                .build());

        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.et_register)
                .related(RelatedId.name)
                .hint(R.string.real_name)
                .build());

        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.et_register_pwd)
                .related(RelatedId.pwd)
                .hint(R.string.pwd)
                .build());

        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.et_register_pwd)
                .related(RelatedId.pwd_sure)
                .hint(R.string.confirm_pwd)
                .build());

        addItem(new Builder(FormType.divider_large).build());
        addItem(new Builder(FormType.text_register_intent)
                .related(RelatedId.location)
                .hint(R.string.province_city)
                .intent(new Intent(this, ProvinceActivity.class))
                .build());

        addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.et_register)
                .related(RelatedId.hospital)
                .hint(R.string.hospital_name)
                .drawable(R.mipmap.form_ic_more)
                .build());

        /*addItem(new Builder(FormType.divider).build());
        addItem(new Builder(FormType.et_phone_number)
                .related(RelatedId.phone_number)
                .hint(R.string.phone_number)
                .build());*/
    }

  /*  @Override
    protected View createHeaderView() {
       return inflate(R.layout.layout_edit_email);
    }*/

    @Override
    protected View createFooterView() {
        return inflate(R.layout.layout_register_footer);
    }

    @Override
    public void findViews() {
        super.findViews();

        mEtEmail = findView(R.id.register_auto_et_emai);
        mEtCapcha = findView(R.id.register_et_capcha);
        mTvCapcha = findView(R.id.register_tv_capcha);
        mTvRegister = findView(R.id.register);

        setOnClickListener(mTvCapcha);
        setOnClickListener(mTvRegister);
        mEtCapcha.setOnEditorActionListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_tv_capcha:
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

        // FIXME: FromItem
        // 检查邮箱判断是否为null (不是FromItem)
      /*  mUserName = mEtEmail.getText().toString().trim();
        if (TextUtil.isEmpty(mUserName)) {
            showToast("请输入" + getString(R.string.email));
            return;
        }*/

        mUserName = getItemStr(RelatedId.email);
        // 判断空
        if (!check()) {
            return;
        }

        if (TextUtil.isEmpty(mUserName)) {
            showToast("请输入" + getString(R.string.email));
            return;
        }

        // 检查激活码是否为空
        String captcha = mEtCapcha.getText().toString().trim();
        if (TextUtil.isEmpty(captcha)) {
            showToast("请输入" + getString(R.string.title_fetch_captcha));
            return;
        }


         //检查邮箱是否合法 (不是FromItem)
       /* if (!RegexUtil.isEmail(mUserName)) {
            showToast(R.string.input_right_email);
            return;
        }*/
        if (!RegexUtil.isEmail(mUserName)) {
            showToast(R.string.input_right_email);
            return;
        }

        // 检查姓名 是否有特殊符号
        if (RegexUtil.checkSpecialSymbol(getItemStr(RelatedId.name))) {
            showToast(R.string.input_real_name);
            return;
        }

        // 检查密码
        String strPwd = getItemStr(RelatedId.pwd);
        String strPwdNg = getItemStr(RelatedId.pwd_sure);
        if (strPwd.length() < 6 || strPwd.length() > 18) {
            showToast(R.string.input_right_pwd_num);
            return;
        }
        if (!strPwd.equals(strPwdNg)) {
            showToast(R.string.check_pwd_is_same);
            return;
        }
        mPwd = strPwd;

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
                .invite(captcha)
                .build());
    }

    /**
     * 获取Item的文本信息
     */
    private String getItemStr(@RelatedId int relatedId) {
        return getRelatedItem(relatedId).getString(TFormElem.val);
    }

    @Override
    protected void onFormViewClick(View v, int position, Object related) {
        @RelatedId int relatedId = getItem(position).getInt(TFormElem.related);
        switch (relatedId) {
            case RelatedId.hospital: {
                // 暂时不判断, 是否保留??
                Place place = new Place();
//                String location = "";
//                String[] locations = location.split(" ");
//                String province;
//                String city;
//                if (TextUtil.isEmpty(location)) {
//                    province = getString(R.string.guang_dong);
//                    city = getString(R.string.guang_zhou);
//                } else {
//                    province = locations[0];
//                    city = locations[1];
//                }

                place.put(TPlace.province, getString(R.string.guang_dong));
                place.put(TPlace.city, getString(R.string.guang_zhou));
                HospitalActivity.nav(RegisterActivity.this, place);
            }
            break;
        }
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {

        if (type == NotifyType.province_finish) {
            Place place = (Place) data;

            String p = place.getString(TPlace.province);
            String c = place.getString(TPlace.city);
            String d = place.getString(TPlace.district);
            String pcdStr = Util.generatePcd(p, c, d);

            getRelatedItem(RelatedId.location).put(TFormElem.name, pcdStr);
            getRelatedItem(RelatedId.location).save(pcdStr, pcdStr);
            refreshRelatedItem(RelatedId.location);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String hospital = data.getStringExtra(Extra.KData);
            getRelatedItem(RelatedId.hospital).put(TFormElem.text, hospital);
            refreshRelatedItem(RelatedId.hospital);
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
                exeNetworkReq(KLogin, NetFactory.login(getItemStr(RelatedId.email), mPwd));
                YSLog.d("yaya","_________________________");
            } else {
                stopRefresh();
                showToast(r.getError());
            }
        }
    }

}
