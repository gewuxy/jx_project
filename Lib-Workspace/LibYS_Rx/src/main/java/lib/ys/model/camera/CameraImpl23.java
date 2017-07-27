package lib.ys.model.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build.VERSION_CODES;

import lib.ys.YSLog;

/**
 * @auther yuansui
 * @since 2017/7/27
 */

@TargetApi(VERSION_CODES.M)
public class CameraImpl23 implements ICamera {

    private static final String TAG = CameraImpl23.class.getSimpleName();

    private CameraManager mManager;

    public CameraImpl23(Context context) {
        mManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    @Override
    public void turnOnFlash() {
        try {
            mManager.setTorchMode("0", true);
        } catch (CameraAccessException e) {
            YSLog.e(TAG, "turnOnFlash", e);
        }
    }

    @Override
    public void turnOffFlash() {
        try {
            mManager.setTorchMode("0", false);
        } catch (CameraAccessException e) {
            YSLog.e(TAG, "turnOnFlash", e);
        }
    }
}
