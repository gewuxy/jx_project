package yy.doctor.activity;

import android.support.annotation.NonNull;
import android.view.View;

import lib.network.model.NetworkResp;
import lib.ys.ui.other.NavBar;
import lib.ys.util.RegexUtil;
import lib.ys.util.TextUtil;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Result;
import yy.doctor.R;
import yy.doctor.dialog.HintDialogMain;
import yy.doctor.model.ForgetPwd;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;
import yy.doctor.view.AutoCompleteEditText;

/**
 * 忘记密码
 *
 * @author CaiXiang
 * @since 2017/4/20
 */
public class ForgetPwdActivity extends BaseActivity {

    private AutoCompleteEditText mEt;
    private HintDialogMain mDialog;

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_forget_pwd;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "忘记密码", this);
    }

    @Override
    public void findViews() {

        mEt = findView(R.id.forget_pwd_et);
    }

    @Override
    public void setViews() {

        setOnClickListener(R.id.forget_pwd_tv);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.forget_pwd_tv: {
                if (TextUtil.isEmpty(mEt.getText().toString().trim())) {
                    showToast("请输入电子邮箱");
                    return;
                }
                //检查邮箱
                if (!RegexUtil.isEmail(mEt.getText().toString().trim())) {
                    showToast("请输入正确邮箱");
                    return;
                }
                exeNetworkReq(NetFactory.forgetPwd(mEt.getText().toString().trim()));
            }
            break;
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.ev(r.getText(), ForgetPwd.class);
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {

        Result<ForgetPwd> r = (Result<ForgetPwd>) result;
        if (r.isSucceed()) {
            if (mDialog == null) {
                mDialog = new HintDialogMain(ForgetPwdActivity.this);
                mDialog.setHint("重置密码的邮件已经发送至您的邮箱");
                mDialog.addButton("知道了", "#0682e6", v -> {
                    mDialog.dismiss();
                    finish();
                });
            }
            mDialog.show();
        } else {
            showToast(r.getError());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

}
