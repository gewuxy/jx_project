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
        Util.addBackIcon(bar, R.string.change_pwd, this);
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
                    showToast(R.string.input_old_pwd);
                    return;
                } else if (TextUtil.isEmpty(newPwd)) {
                    showToast(R.string.input_new_pwd);
                    return;
                } else if (TextUtil.isEmpty(confirmPwd)) {
                    showToast(R.string.input_confirm_pwd);
                    return;
                } else if (!newPwd.equals(confirmPwd)) {
                    showToast(R.string.confirm_no_equal_new);
                    return;
                } else if (newPwd.length() < 6 || newPwd.length() > 18) {
                    showToast(R.string.pwd_places_no_right);
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
            showToast(R.string.pwd_change_success);
            finish();
        } else {
            showToast(r.getError());
        }
    }
}
