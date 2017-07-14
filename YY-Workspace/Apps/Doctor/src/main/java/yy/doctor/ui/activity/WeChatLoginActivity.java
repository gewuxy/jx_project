package yy.doctor.ui.activity;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.RegexUtil;
import lib.ys.util.TextUtil;
import lib.yy.network.Result;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.UISetter;
import yy.doctor.util.Util;

/**
 * @auther Huoxuyu
 * @since 2017/7/12
 */

public class WeChatLoginActivity extends BaseActivity {

    private EditText mEtName;
    private EditText mEtPwd;
    private CheckBox mCbVisible;
    private TextView mTvRegister;
    private String mRequest;

    @Override
    public void initData() {
        mRequest = getIntent().getStringExtra(Extra.KData);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_login_wechat;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.wechat_binding, this);
    }

    @Override
    public void findViews() {

        mEtName = findView(R.id.wechat_login_et_name);
        mEtPwd = findView(R.id.wechat_login_et_pwd);
        mCbVisible = findView(R.id.wechat_login_cb_visible_pwd);
        mTvRegister = findView(R.id.wechat_login_tv);
    }

    @Override
    public void setViews() {
        //清空用户信息
        Profile.inst().clear();
        // 设置密码输入范围
        UISetter.setPwdRange(mEtPwd);

        setOnClickListener(R.id.wechat_login_tv);

        textChanged();
    }

    @Override
    public void onClick(View v) {

        String strName = mEtName.getText().toString().trim();
        String strPwd = mEtPwd.getText().toString().trim();

        if (TextUtil.isEmpty(strName)) {
            showToast(R.string.input_name);
            return;
        }
        if (TextUtil.isEmpty(strPwd)) {
            showToast(R.string.input_pwd);
            return;
        }
        refresh(RefreshWay.dialog);
        exeNetworkReq(NetFactory.login(strName, strPwd));

        mCbVisible.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mEtPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                //把光标设置到当前文本末尾
                mEtPwd.setSelection(mEtPwd.getText().length());
            }else {
                mEtPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                mEtPwd.setSelection(mEtPwd.getText().length());
            }
        });
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), Profile.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        stopRefresh();

        Result<Profile> r = (Result<Profile>) result;
    }

    public void textChanged() {

        mTvRegister.setEnabled(false);
        mTvRegister.setBackgroundResource(R.color.text_97cbf2);
        mEtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (RegexUtil.isMobileCN(mEtName.getText().toString()) || RegexUtil.isEmail(mEtName.getText().toString())) {
                    mTvRegister.setEnabled(true);
                    mTvRegister.setBackgroundResource(R.drawable.btn_selector_blue);
                } else {
                    mTvRegister.setEnabled(false);
                    mTvRegister.setBackgroundResource(R.color.text_97cbf2);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEtPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtil.isEmpty(mEtName.getText()) || TextUtil.isEmpty(mEtPwd.getText())) {
                    mTvRegister.setEnabled(false);
                    mTvRegister.setBackgroundResource(R.color.text_97cbf2);
                } else {
                    mTvRegister.setEnabled(true);
                    mTvRegister.setBackgroundResource(R.drawable.btn_selector_blue);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
