package yy.doctor.ui.activity.register;

import android.support.annotation.NonNull;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * 获取激活码说明界面
 * <p>
 * 日期 : 2017/4/19
 * 创建人 : guoxuan
 */
public class CapchaActivity extends BaseActivity {

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
        return R.layout.activity_capcha;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.capcha, this);
    }

}