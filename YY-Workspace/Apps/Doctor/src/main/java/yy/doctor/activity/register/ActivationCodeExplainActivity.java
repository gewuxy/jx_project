package yy.doctor.activity.register;

import android.support.annotation.NonNull;

import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * 获取激活码说明界面
 * <p>
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */
public class ActivationCodeExplainActivity extends BaseActivity {

    @Override
    public void findViews() {
    }

    @Override
    public void setViews() {
    }

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_activation_code_explain;
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, "获取激活码", this);
    }

}
