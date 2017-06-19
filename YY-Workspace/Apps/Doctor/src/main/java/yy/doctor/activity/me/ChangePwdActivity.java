package yy.doctor.activity.me;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import lib.network.model.NetworkResp;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Result;
import yy.doctor.R;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 修改密码
 *
 * @author CaiXiang
 * @since 2017/4/20
 */
public class ChangePwdActivity extends BaseActivity {

    private EditText mEtOldPwd;
    private EditText mEtNewPwd;
    private EditText mEtConfirmPwd;

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_change_pwd;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "修改密码", this);
    }

    @Override
    public void findViews() {

        mEtOldPwd = findView(R.id.change_pwd_et_old);
        mEtNewPwd = findView(R.id.change_pwd_et_new);
        mEtConfirmPwd = findView(R.id.change_pwd_et_confirm);
    }

    @Override
    public void setViews() {

        setOnClickListener(R.id.change_pwd_tv);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.change_pwd_tv: {

                String oldPwd = mEtOldPwd.getText().toString();
                String newPwd = mEtNewPwd.getText().toString();
                String confirmPwd = mEtConfirmPwd.getText().toString();
                if (TextUtil.isEmpty(oldPwd)) {
                    showToast("请输入旧密码");
                    return;
                } else if (TextUtil.isEmpty(newPwd)) {
                    showToast("请输入新密码");
                    return;
                } else if (TextUtil.isEmpty(confirmPwd)) {
                    showToast("请输入确认密码");
                    return;
                } else if (!newPwd.equals(confirmPwd)) {
                    showToast("确认密码与新密码不一致！");
                    return;
                } else if (newPwd.length() < 6 || newPwd.length() > 18) {
                    showToast("请输入6~18位密码");
                    return;
                } else {
                    refresh(RefreshWay.dialog);
                    exeNetworkReq(NetFactory.changePwd(oldPwd, newPwd));
                }
            }
            break;
        }
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {

        stopRefresh();
        Result r = (Result) result;
        if (r.isSucceed()) {
            showToast("密码修改成功");
            finish();
        } else {
            showToast(r.getError());
        }
    }
}
