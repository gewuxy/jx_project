package lib.network.provider;

import android.os.Handler;
import android.os.Looper;

import lib.network.error.NetError;

/**
 * @author yuansui
 */
public class Delivery {

    private Handler mHandler;
    private IDeliveryCallback mCallback;

    public Delivery(IDeliveryCallback callback) {
        mHandler = new Handler(Looper.getMainLooper());
        mCallback = callback;
    }

    public void deliverSuccess(final IRequestBuilder builder, final Object obj) {
        if (mCallback != null) {
            mHandler.post(() -> mCallback.deliverSuccess(builder, obj));
        }
    }

    public void deliverProgress(final IRequestBuilder builder, final float progress, final long contentLength) {
        if (mCallback != null) {
            mHandler.post(() -> mCallback.deliverProgress(builder, progress, contentLength));
        }
    }

    public void deliverError(IRequestBuilder builder, NetError error) {
        if (mCallback != null) {
            mHandler.post(() -> mCallback.deliverError(builder, error));
        }
    }
}
