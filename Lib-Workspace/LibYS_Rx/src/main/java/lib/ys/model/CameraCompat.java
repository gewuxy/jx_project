package lib.ys.model;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build.VERSION_CODES;
import android.support.annotation.NonNull;

/**
 * 系统占用了Camera和camera2
 * 兼容5.0及以上
 *
 * @auther WangLan
 * @since 2017/7/27
 */
public class CameraCompat {

    private Context mContext;
    private Camera mCamera;
    private Camera.Parameters mParameters;

    public CameraCompat(@NonNull Context context) {
        mContext = context;
        if (mContext == null) {
            throw new NullPointerException("context can not be null");
        }
    }

    public void turnOnFlash() {

    }

    @TargetApi(VERSION_CODES.M)
    private void nativeTurnOnFlash2() {
        CameraManager manager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        try {
            manager.setTorchMode("0", true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void turnOffFlash() {

    }
}
