package lib.ys.model.camera;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.support.annotation.NonNull;

/**
 * 系统占用了Camera和camera2
 * 兼容5.0及以上
 *
 * @auther WangLan
 * @since 2017/7/27
 */
public class CameraCompat implements ICamera {

    private ICamera mCamera;

    public CameraCompat(@NonNull Context context) {
        if (context == null) {
            throw new NullPointerException("context can not be null");
        }

        int sdk = Build.VERSION.SDK_INT;
        if (sdk < VERSION_CODES.LOLLIPOP) {
            // 5.0以下
            mCamera = new CameraImpl();
        } else if (sdk >= VERSION_CODES.LOLLIPOP
                && sdk < VERSION_CODES.M) {
            // 5.0
            mCamera = new CameraImpl21();
        } else {
            // 6.0以上
            mCamera = new CameraImpl23(context);
        }
    }

    @Override
    public void turnOnFlash() {
        mCamera.turnOnFlash();
    }

    @Override
    public void turnOffFlash() {
        mCamera.turnOffFlash();
    }
}
