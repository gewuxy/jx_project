package yy.doctor.activity;

import lib.yy.test.BaseTestActivity;
import yy.doctor.dialog.UpdateNoticeDialog;

/**
 * @auther yuansui
 * @since 2017/4/19
 */

public class TestActivity extends BaseTestActivity {

    @Override
    public void initData() {
        add("主页", MainActivity.class);

        add("注册", RegisterActivity.class);
        add("登录", LoginActivity.class);
        add("更新对话框", new UpdateNoticeDialog(this));
    }
}
