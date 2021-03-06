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

import java.util.ArrayList;
import java.util.List;

import jx.csp.R;
import jx.csp.constant.BindId;
import jx.csp.constant.Constants;
import jx.csp.model.BindInfo;
import jx.csp.model.BindInfo.TBindInfo;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.network.JsonParser;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.sp.SpApp;
import jx.csp.ui.activity.MainActivity;
import jx.csp.util.UISetter;
import jx.csp.util.Util;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.activity.base.BaseActivity;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.RegexUtil;
import lib.ys.util.TextUtil;

/**
 * @auther WangLan
 * @since 2017/9/29
 */

public class YaYaAuthorizeActivity extends BaseActivity {

    private final int KIdAuthorizeLogin = 0;
    private final int KIdLogin = 1;
    private final int KIdBind = 2;

    public EditText mEtUsername;
    private EditText mEtPwd;
    private CheckBox mCbVisiblePwd;

    private ImageView mIvUsernameClean;
    private ImageView mIvPwdClean;

    private TextView mTvLogin;
    private String mNickName;

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_login_jx;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addCloseIcon(bar, null, this);
        bar.setBackgroundResource(R.color.white);
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
        if (Profile.inst().isLogin()) {
            mTvLogin.setText(R.string.account_confirm_bind);
        } else {
            mTvLogin.setText(R.string.authorization_login);
            mEtUsername.setText(SpApp.inst().getUserName());
            mEtUsername.setSelection(SpApp.inst().getUserName().length());
            // 清空用户信息
            Profile.inst().clear();
        }

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

    @Override
    protected boolean enableHideKeyboardWhenChangeFocus() {
        return true;
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
                et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                et.setTransformationMethod(PasswordTransformationMethod.getInstance());
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
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), Profile.class);
    }

    @Override
    public boolean interceptNetSuccess(int id, IResult r) {
        if (r.getCode() == Constants.KAccountFrozen) {
            stopRefresh();
            UISetter.showFrozenDialog(r.getMessage(), YaYaAuthorizeActivity.this);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        stopRefresh();
        Profile profile = (Profile) r.getData();
        if (id == KIdAuthorizeLogin) {
            if (r.isSucceed()) {
                mNickName = profile.getString(TProfile.nickName);
                SpApp.inst().saveUserName(getUserName());
                if (Profile.inst().isLogin()) {
                    exeNetworkReq(KIdBind, UserAPI.bindAccountStatus()
                            .thirdPartyId(BindId.yaya)
                            .uniqueId(profile.getString(TProfile.uid))
                            .nickName(mNickName)
                            .gender(profile.getString(TProfile.gender))
                            .avatar(profile.getString(TProfile.avatar))
                            .build());
                } else {
                    exeNetworkReq(KIdLogin, UserAPI.login(BindId.yaya)
                            .uniqueId(profile.getString(TProfile.uid))
                            .email(profile.getString(TProfile.email))
                            .mobile(profile.getString(TProfile.mobile))
                            .nickName(mNickName)
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
//                getBindNickName(mNickName);
                Profile.inst().update(profile);
                notify(NotifyType.login);
                startActivity(MainActivity.class);
            } else {
                onNetworkError(id, r.getError());
            }
            finish();

        } else if (id == KIdBind) {
            if (r.isSucceed()) {
                getBindNickName(mNickName);
            } else {
                onNetworkError(id, r.getError());
            }
            finish();
        }
    }

    private void getBindNickName(String nickName) {
        List<BindInfo> infoList = Profile.inst().getList(TProfile.bindInfoList);
        if (infoList == null) {
            infoList = new ArrayList<>();
        }
        boolean flag = true;
        for (BindInfo info : infoList) {
            if (info.getInt(TBindInfo.thirdPartyId) == BindId.yaya) {
                info.put(TBindInfo.nickName, nickName);
                flag = false;
            }
        }
        if (flag) {
            BindInfo bindInfoList = new BindInfo();
            bindInfoList.put(TBindInfo.thirdPartyId, BindId.yaya);
            bindInfoList.put(TBindInfo.nickName, nickName);
            infoList.add(bindInfoList);
        }
        Profile.inst().put(TProfile.bindInfoList, infoList);
        Profile.inst().saveToSp();
        notify(NotifyType.bind_yaya, Profile.inst().getBindNickName(BindId.yaya));
    }

    public String getUserName() {
        return Util.getEtString(mEtUsername);
    }

    public String getPwd() {
        return Util.getEtString(mEtPwd);
    }

}
