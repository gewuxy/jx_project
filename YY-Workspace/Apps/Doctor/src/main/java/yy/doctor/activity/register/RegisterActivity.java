package yy.doctor.activity.register;

import android.support.annotation.IntDef;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import lib.network.model.NetworkResponse;
import lib.ys.ui.other.NavBar;
import lib.ys.form.FormItemEx.TFormElem;
import lib.yy.Notifier.NotifyType;
import lib.yy.activity.base.BaseFormActivity;
import lib.yy.network.Response;
import yy.doctor.BuildConfig;
import yy.doctor.R;
import yy.doctor.activity.MainActivity;
import yy.doctor.model.Profile;
import yy.doctor.model.Register;
import yy.doctor.model.form.Builder;
import yy.doctor.model.form.FormType;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 注册界面
 * <p>
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */
public class RegisterActivity extends BaseFormActivity {

    public static final int KFromRegister = 1;
    private EditText mActivationCode;
    private TextView mGetActivationCode;
    private TextView mRegister;
    private String mUserName;   //用户名
    private String mPwd;        //密码

    @IntDef({
            RelatedId.email,
            RelatedId.name,
            RelatedId.pwd,
            RelatedId.marksure_pwd,
            RelatedId.location,
            RelatedId.hospital,
            RelatedId.activation_code,
    })
    private @interface RelatedId {
        int email = 0;
        int name = 1;
        int pwd = 2;
        int marksure_pwd = 3;
        int location = 4;
        int hospital = 5;
        int activation_code = 6;
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, "注册", this);

    }

    @Override
    public void initData() {
        super.initData();

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.et_register)
                .related(RelatedId.email)
                .hint("邮箱")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et_register)
                .related(RelatedId.name)
                .hint("姓名")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et_register)
                .related(RelatedId.pwd)
                .hint("密码")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et_register)
                .related(RelatedId.marksure_pwd)
                .hint("确认密码")
                .build());

        addItem(new Builder(FormType.divider_large).build());

        addItem(new Builder(FormType.et_register)
                .related(RelatedId.location)
                .hint("广东 广州")
                .build());

        addItem(new Builder(FormType.divider).build());

        addItem(new Builder(FormType.et_register)
                .related(RelatedId.hospital)
                .hint("医院名称")
                .drawable(R.mipmap.ic_more)
                .build());

    }

    @Override
    protected View createFooterView() {
        return inflate(R.layout.layout_register_footer);
    }

    @Override
    public void findViews() {
        super.findViews();

        mActivationCode = findView(R.id.register_et_activation_code);
        mGetActivationCode = findView(R.id.register_get_activation_code);
        mRegister = findView(R.id.register);

        mGetActivationCode.setOnClickListener(this);
        mRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_et_activation_code:
                break;
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

        String strInvite = getItem(11).getString(TFormElem.text);
        mUserName = getItem(RelatedId.email).getString(TFormElem.text);
        String strNickname = "";
        String strLinkman = getItem(RelatedId.name).getString(TFormElem.text);
        String strMobile = "";
        String strPwd = getItem(RelatedId.pwd).getString(TFormElem.text);
        String strPwdNg = getItem(RelatedId.marksure_pwd).getString(TFormElem.text);
        String strProvince = "";
        String strCity = getItem(RelatedId.location).getString(TFormElem.text);
        String strHospital = "";
        String strDepartment = "";
        String strLicence = "";

        if (BuildConfig.TEST) {
            strInvite = "02123456789";
            mUserName = "123456789@163.com";
            strNickname = mUserName;
            strLinkman = "test1";
            strMobile = "";
            strPwd = "123456";
            strPwdNg = "123456";
            strProvince = "广东";
            strCity = "广州";
            strHospital = "第一医院";
            strDepartment = "";
            strLicence = "";
        }

//                if (!check()) {
//            return;
//        }

        if (!strPwd.equals(strPwdNg)) {
            mPwd = strPwd;
            showToast("密码不一致");
            return;
        }

        //自动登录
        notify(NotifyType.login);
        exeNetworkRequest(1, NetFactory.login(mUserName, mPwd));
        startActivity(MainActivity.class);
        finish();
        /*refresh(RefreshWay.dialog);
        exeNetworkRequest(0, NetFactory.register(strInvite, mUserName, strNickname, strLinkman,
                strMobile, strPwd, strProvince, strCity, strHospital, strDepartment, strLicence));*/
    }

    @Override
    protected void onFormViewClick(View v, int position, Object related) {

        if (v instanceof ImageView) {
            @RelatedId int relatedId = getItem(position).getInt(TFormElem.related);
            switch (relatedId) {
                case RelatedId.hospital:
                    startActivityForResult(HospitalActivity.class, KFromRegister);
                    break;
            }
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResponse nr) throws Exception {
        if (id == 1) {
            return JsonParser.ev(nr.getText(), Profile.class);
        }
        return JsonParser.ev(nr.getText(), Register.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == 1) {
            Response<Profile> r = (Response<Profile>) result;

            if (r.isSucceed()) {
                Profile.inst().update(r.getData());
                startActivity(MainActivity.class);
                finish();
            } else {
                showToast(r.getError());
            }
        }

        Response<Register> r = (Response<Register>) result;
        if (r.isSucceed()) {
            Register.inst().update(r.getData());
            //登录
            notify(NotifyType.login);
        } else {
            stopRefresh();
            showToast(r.getError());
        }
    }

}
