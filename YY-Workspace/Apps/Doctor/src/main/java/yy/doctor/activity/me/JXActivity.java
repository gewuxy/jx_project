package yy.doctor.activity.me;

import android.support.annotation.NonNull;

import lib.yy.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * 敬信
 *
 * @author CaiXiang
 * @since 2017/4/20
 */
public class JXActivity extends BaseActivity {

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_jx;
    }

    @Override
    public void initNavBar() {

        Util.addBackIcon(getNavBar(), "敬信", this);

    }

    @Override
    public void findViews() {

    }

    @Override
    public void setViewsValue() {

    }

}
