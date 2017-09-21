package yy.doctor.ui.activity.me;

import android.content.Intent;
import android.support.annotation.NonNull;

import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.sp.SpUser;
import yy.doctor.ui.activity.me.unitnum.UnitNumDetailActivityRouter;
import yy.doctor.ui.activity.user.login.LoginActivity;

/**
 * 桌面快捷方式的启动中间页 判断登录有没有过时
 *
 * @author CaiXiang
 * @since 2017/6/6
 */

public class LaunchTmpActivity extends BaseActivity {

    private static final int KRequestCode = 100;
    private int mUnitNumId;

    @Override
    public void initData() {
        mUnitNumId = getIntent().getIntExtra(Extra.KUnitNumId, 100);
        YSLog.d(TAG, " UnitNumId = " + mUnitNumId);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_launch_temp;
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void findViews() {
    }

    @Override
    public void setViews() {
        // 判断用户登录是否有效
        runOnUIThread(() -> {
            if (SpUser.inst().needUpdateProfile()) {
                //跳转到登录界面
                Intent intent = new Intent(LaunchTmpActivity.this, LoginActivity.class);
                intent.putExtra(Extra.KData, "hello");
                startActivityForResult(intent, KRequestCode);
            } else {
                //跳转
                UnitNumDetailActivityRouter.create(mUnitNumId).route(LaunchTmpActivity.this);
                finish();
            }
        }, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            //跳转
            UnitNumDetailActivityRouter.create(mUnitNumId).route(LaunchTmpActivity.this);
            finish();
        }
    }

    @Override
    protected void startInAnim() {
        // use default
    }
}
