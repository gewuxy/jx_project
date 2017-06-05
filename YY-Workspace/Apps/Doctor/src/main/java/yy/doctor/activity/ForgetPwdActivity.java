package yy.doctor.activity;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import lib.network.model.NetworkResp;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.activity.base.BaseActivity;
import lib.yy.network.Result;
import yy.doctor.R;
import yy.doctor.dialog.CommonOneDialog;
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
        super.onClick(v);

        mEt.setFocusable(false);
        int id = v.getId();
        switch (id) {
            case R.id.forget_pwd_tv: {
                if (TextUtil.isEmpty(mEt.getText().toString())) {
                    showToast("请输入电子邮箱");
                } else {
                    exeNetworkReq(NetFactory.forgetPwd(mEt.getText().toString().trim()));
                }
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
            CommonOneDialog dialog = new CommonOneDialog(ForgetPwdActivity.this) {

                @Override
                public void setTvSecondary(TextView tvSecondary) {
                    tvSecondary.setVisibility(View.GONE);
                }
            };
            dialog.setTvMainHint("重置密码的邮已经发送至您的邮箱")
                    .setTvMainColor(ResLoader.getColor(R.color.text_666))
                    .setTvMainSize(fitDp(15))
                    .setTvSureText("知道了")
                    .setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    })
                    .show();
        } else {
            showToast(r.getError());
        }
    }

}
