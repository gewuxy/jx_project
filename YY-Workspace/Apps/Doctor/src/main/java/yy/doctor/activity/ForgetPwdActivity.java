package yy.doctor.activity;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.dialog.CommonOneDialog;
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

        int id = v.getId();
        switch (id) {
            case R.id.forget_pwd_tv: {
                if (TextUtil.isEmpty(mEt.getText().toString())) {
                    showToast("请输入电子邮箱");
                } else {

                    new CommonOneDialog(ForgetPwdActivity.this){
                        @Override
                        public void setTvSecondary(TextView tvSecondary) {
                            tvSecondary.setVisibility(View.GONE);
                        }
                    }
                            .setTvMainHint("重置密码的邮已经发送至您的邮箱")
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
                }
            }
            break;
        }
    }
}
