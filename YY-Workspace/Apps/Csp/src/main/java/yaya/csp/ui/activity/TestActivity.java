package yaya.csp.ui.activity;

import android.content.Intent;

import lib.yy.test.BaseTestActivity;
import yaya.csp.ui.activity.me.MeActivity;

/**
 * @auther yuansui
 * @since 2017/9/20
 */

public class TestActivity extends BaseTestActivity {

    @Override
    public void initData() {

        add("个人中心", new Intent(this, MeActivity.class));
    }
}
