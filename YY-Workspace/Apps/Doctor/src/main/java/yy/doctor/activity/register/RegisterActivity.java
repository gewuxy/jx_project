package yy.doctor.activity.register;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.form.FormItemEx.TFormElem;
import lib.ys.ui.other.NavBar;
import lib.ys.util.RegexUtil;
import lib.yy.Notifier.NotifyType;
import lib.yy.activity.base.BaseFormActivity;
import lib.yy.network.Result;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.activity.LoginActivity;
import yy.doctor.activity.MainActivity;
import yy.doctor.model.Profile;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;
import yy.doctor.view.AutoCompleteEditText;

/**
 * 注册界面
 * <p>
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */
public class RegisterActivity extends BaseFormActivity {

    private static final int KRegister = 0;
    private static final int KLogin = 1;

    private AutoCompleteEditText mEtEmail;
    private EditText mActCode;      //填写激活码
    private TextView mGetActCode;   //获取激活码
    private TextView mRegister;     //注册
    private String mUserName;       //用户名
    private String mPwd;            //密码

    @IntDef({
            RelatedId.name,
            RelatedId.pwd,
            RelatedId.marksure_pwd,
            RelatedId.location,
            RelatedId.hospital,
            RelatedId.activation_code,
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface RelatedId {
        int name = 1;
        int pwd = 2;
        int marksure_pwd = 3;
        int location = 4;
        int hospital = 5;
        int activation_code = 6;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.register, this);
    }

    @Override
    public void initData() {
        super.initData();

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
                .related(RelatedId.marksure_pwd)
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
                .drawable(R.mipmap.ic_more)
                .build());
    }

    @Override
    protected View createHeaderView() {
        return inflate(R.layout.layout_edit_email);
    }

    @Override
    protected View createFooterView() {
        return inflate(R.layout.layout_register_footer);
    }

    @Override
    public void findViews() {
        super.findViews();

        mEtEmail = findView(R.id.register_auto_et_emai);
        mActCode = findView(R.id.register_et_activation_code);
        mGetActCode = findView(R.id.register_get_activation_code);
        mRegister = findView(R.id.register);

        mGetActCode.setOnClickListener(this);
        mRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_get_activation_code:
                startActivity(ActivationCodeExplainActivity.class);
                break;
            case R.id.register:
                enroll();
                break;
        }
    }

    /**
     * 注册操作
     */
    private void enroll() {

        mUserName = mEtEmail.getText().toString().trim();
        String strPwd = getItemStr(RelatedId.pwd);
        String strPwdNg = getItemStr(RelatedId.marksure_pwd);

        String strProvince = null;
        String strCity = null;
        String strArea = null;
        String str = getItemStr(RelatedId.location);
        YSLog.d(TAG, " address = " + str);
        String[] strs = str.split(" ");
        for (int i = 0; i < strs.length; i++) {
            String s = strs[i];
            if (i == 0) {
                strProvince = s;
            } else if (i == 1) {
                strCity = s;
            } else {
                strArea = s;
            }
        }

        //检查邮箱
        if (!RegexUtil.isEmail(mUserName)) {
            showToast(R.string.input_right_email);
            return;
        }
        if (!check()) {
            return;
        }
        //检查姓名 是否有特殊符号
        if (RegexUtil.checkString(getItemStr(RelatedId.name))) {
            showToast(R.string.input_real_name);
            return;
        }

        if (strPwd.length() < 6 || strPwd.length() > 18) {
            showToast(R.string.input_right_pwd_num);
            return;
        }
        if (!strPwd.equals(strPwdNg)) {
            showToast(R.string.check_pwd_is_same);
            return;
        }
        mPwd = strPwd;

        //注册
        refresh(RefreshWay.dialog);
        exeNetworkReq(KRegister, NetFactory.register()
                .username(mUserName)
                .linkman(getItemStr(RelatedId.name))
                .pwd(getItemStr(RelatedId.pwd))
                .province(strProvince)
                .city(strCity)
                .area(strArea)
                .hospital(getItemStr(RelatedId.hospital))
                .invite(mActCode.getText().toString().trim())
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
        if (v instanceof ImageView) {
            @RelatedId int relatedId = getItem(position).getInt(TFormElem.related);
            switch (relatedId) {
                case RelatedId.hospital:
                    startActivityForResult(HospitalActivity.class, 0);
                    break;
            }
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
                exeNetworkReq(KLogin, NetFactory.login(mUserName, mPwd));
            } else {
                stopRefresh();
                showToast(r.getError());
            }
        }
    }

}
