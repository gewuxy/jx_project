package lib.ys.model.camera;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;

/**
 * @auther yuansui
 * @since 2017/7/27
 */

public class CameraImpl implements ICamera {

    private Camera mCamera;
    private Camera.Parameters mParameters;

    public CameraImpl() {
        mCamera = Camera.open();
    }

    @Override
    public void turnOnFlash() {
        mParameters = mCamera.getParameters();
        mParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(mParameters);
        mCamera.startPreview();
    }

    @Override
    public void turnOffFlash() {
        mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(mParameters);
        mCamera.stopPreview();
        mCamera.release();
    }
}
