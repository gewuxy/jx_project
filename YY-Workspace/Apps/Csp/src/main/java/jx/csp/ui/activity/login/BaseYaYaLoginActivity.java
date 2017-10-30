package jx.csp.ui.activity.login;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import jx.csp.Constants.LoginType;
import jx.csp.R;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.sp.SpApp;
import jx.csp.sp.SpUser;
import jx.csp.ui.activity.main.MainActivity;
import jx.csp.util.UISetter;
import jx.csp.util.Util;
import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.RegexUtil;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseActivity;

/**
 * @auther WangLan
 * @since 2017/9/29
 */

abstract public class BaseYaYaLoginActivity extends BaseActivity {

    private final int KIdAuthorizeLogin = 0;
    private final int KIdLogin = 1;
    private final int KIdBind = 2;

    public EditText mEtUsername;
    private EditText mEtPwd;
    private CheckBox mCbVisiblePwd;

    private ImageView mIvUsernameClean;
    private ImageView mIvPwdClean;

    private TextView mTvLogin;

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_yaya_login;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addCloseIcon(bar, getString(R.string.yaya_authorization_login), this);
    }

    @Override
    public void findViews() {
        mEtUsername = findView(R.id.yaya_username);
        mEtPwd = findView(R.id.yaya_pwd);
        mCbVisiblePwd = findView(R.id.yaya_visible_pwd);

        mIvUsernameClean = findView(R.id.yaya_username_clean);
        mIvPwdClean = findView(R.id.yaya_pwd_clean);
        mTvLogin = findView(R.id.yaya_login);
    }

    @Override
    public void setViews() {
        mTvLogin.setEnabled(false);
        mTvLogin.setText(getSetText());

        setPwdVisible(mEtPwd, mCbVisiblePwd);

        buttonChanged(mEtUsername, mIvUsernameClean);
        buttonChanged(mEtPwd, mIvPwdClean);

        setOnClickListener(R.id.yaya_username);
        setOnClickListener(R.id.yaya_pwd);
        setOnClickListener(R.id.yaya_visible_pwd);
        setOnClickListener(R.id.yaya_username_clean);
        setOnClickListener(R.id.yaya_pwd_clean);
        setOnClickListener(R.id.yaya_login);
    }

    private void buttonChanged(EditText et, ImageView iv) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(" ")) {
                    et.setText(s.toString().replaceAll(" ", ""));
                    et.setSelection(start);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = getUserName();
                //用户名有效，手机或者邮箱
                boolean nameValid = TextUtil.isNotEmpty(name)
                        && (RegexUtil.isMobileCN(name) || RegexUtil.isEmail(name));

                if (nameValid && TextUtil.isNotEmpty(getPwd())) {
                    mTvLogin.setEnabled(true);
                } else {
                    mTvLogin.setEnabled(false);
                }
                if (et.hasFocus() && TextUtil.isNotEmpty(s)) {
                    showView(iv);
                } else {
                    goneView(iv);
                }
            }
        });

        et.setOnFocusChangeListener((v, hasFocus) -> {
            // iv是否显示
            if (hasFocus && TextUtil.isNotEmpty(Util.getEtString(et))) {
                showView(iv);
            } else {
                goneView(iv);
            }
        });
    }

    private void setPwdVisible(EditText et, CheckBox cb) {
        UISetter.setPwdRange(et);
        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                et.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            // 把光标设置到当前文本末尾
            et.setSelection(et.length());
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yaya_username_clean: {
                mEtUsername.setText("");
                SpApp.inst().saveUserName("");
            }
            break;
            case R.id.yaya_pwd_clean: {
                mEtPwd.setText("");
            }
            break;
            case R.id.yaya_login: {
                refresh(RefreshWay.dialog);
                exeNetworkReq(KIdAuthorizeLogin, UserAPI.yayaLogin(getUserName(), getPwd()).build());
            }
            break;
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == KIdBind) {
            return JsonParser.error(r.getText());
        } else {
            return JsonParser.ev(r.getText(), Profile.class);
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        stopRefresh();
        Result<Profile> r = (Result<Profile>) result;
        Profile profile = r.getData();
        if (id == KIdAuthorizeLogin) {
            if (r.isSucceed()) {
                SpApp.inst().saveUserName(getUserName());
                if (Profile.inst().isLogin()) {
                    exeNetworkReq(KIdBind, UserAPI.bindAccountStatus()
                            .thirdPartyId(LoginType.yaya_login)
                            .uniqueId(profile.getString(TProfile.uid))
                            .nickName(profile.getString(TProfile.nickName))
                            .gender(profile.getString(TProfile.gender))
                            .avatar(profile.getString(TProfile.avatar))
                            .build());
                } else {
                    exeNetworkReq(KIdLogin, UserAPI.login(LoginType.yaya_login)
                            .uniqueId(profile.getString(TProfile.uid))
                            .email(profile.getString(TProfile.email))
                            .mobile(profile.getString(TProfile.mobile))
                            .nickName(profile.getString(TProfile.nickName))
                            .country(profile.getString(TProfile.country))
                            .province(profile.getString(TProfile.province))
                            .city(profile.getString(TProfile.city))
                            .build());
                }
            } else {
                onNetworkError(id, r.getError());
            }
        } else if (id == KIdLogin) {
            if (r.isSucceed()) {
                // FIXME: 2017/10/30 不能全部按字段名保存
                Profile.inst().put(TProfile.nickName, profile.getString(TProfile.nickName));
                Profile.inst().put(TProfile.avatar, profile.getString(TProfile.avatar));
                updateBindYaYa();
                startActivity(MainActivity.class);
            } else {
                onNetworkError(id, r.getError());
            }
            finish();
        } else if (id == KIdBind) {
            if (r.isSucceed()) {
                updateBindYaYa();
            } else {
                onNetworkError(id, r.getError());
            }
            finish();
        }
    }

    private void updateBindYaYa() {
        Profile.inst().put(TProfile.jingxin, getUserName());
        Profile.inst().saveToSp();
        SpUser.inst().updateProfileRefreshTime();
        notify(NotifyType.profile_change, getUserName());
        notify(NotifyType.bind_yaya, getUserName());
    }

    public String getUserName() {
        return Util.getEtString(mEtUsername);
    }

    public String getPwd() {
        return Util.getEtString(mEtPwd);
    }

    /**
     * 获取按钮文本
     *
     * @return 按钮的文本
     */
    abstract protected CharSequence getSetText();

}
