package yy.doctor.ui.activity;

import android.support.annotation.NonNull;
import android.widget.ToggleButton;

import lib.ys.model.camera.CameraCompat;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.R;

/**
 * @auther WangLan
 * @since 2017/7/17
 */

public class FlashActivity extends BaseActivity {

    private ToggleButton mToggleButton;
    private CameraCompat mCamera;

    @Override
    public void initData() {
        mCamera = new CameraCompat(this);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_flash;
    }

    @Override
    public void initNavBar(NavBar bar) {

    }

    @Override
    public void findViews() {
        mToggleButton = findView(R.id.flash_tb);
    }

    @Override
    public void setViews() {
        mToggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mCamera.turnOnFlash();
            } else {
                mCamera.turnOffFlash();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


}
