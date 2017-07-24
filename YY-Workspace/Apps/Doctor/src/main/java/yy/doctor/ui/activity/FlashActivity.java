package yy.doctor.ui.activity;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.NonNull;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.R;

/**
 * @auther WangLan
 * @since 2017/7/17
 */

public class FlashActivity extends BaseActivity {

    ToggleButton mToggleButton;
    private CameraManager mManager;
    private Camera mCamera;
    private Camera.Parameters mParameters;
    public static boolean kaiguan; // 定义开关状态，状态为false，打开状态，状态为true，关闭状

    @Override
    public void initData() {
        kaiguan = true;
        mManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
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
        mManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    }

    @Override
    public void setViews() {
       // setOnClickListener(mFlashBtn);
        mToggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(VERSION.SDK_INT >= VERSION_CODES.M){
                    try {
                        mManager.setTorchMode("0",isChecked);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }else {
                    if (isChecked) {
                        mCamera = Camera.open();
                        mParameters = mCamera.getParameters();
                        mParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
                        mCamera.setParameters(mParameters);
                        mCamera.startPreview();
                    }else {
                        mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                        mCamera.setParameters(mParameters);
                        mCamera.stopPreview();
                        mCamera.release();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }



}
