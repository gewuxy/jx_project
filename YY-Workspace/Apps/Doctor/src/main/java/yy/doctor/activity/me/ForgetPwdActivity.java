package yy.doctor.activity.me;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import lib.ys.ex.NavBar;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * 忘记密码
 *
 * @author CaiXiang
 * @since 2017/4/20
 */
public class ForgetPwdActivity extends BaseActivity {

    private EditText mEt;

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

        int id = v.getId();
        switch (id) {
            case R.id.forget_pwd_tv: {
                showToast("123");
            }
            break;
        }
    }
}
