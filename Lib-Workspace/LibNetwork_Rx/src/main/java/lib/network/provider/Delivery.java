package lib.network.provider;

import android.os.Handler;
import android.os.Looper;

import lib.network.error.NetError;

/**
 * @author yuansui
 */
public class Delivery {

    private Handler mHandler;
    private NativeNetworkListener mLsn;

    public Delivery(NativeNetworkListener l) {
        mHandler = new Handler(Looper.getMainLooper());
        mLsn = l;
    }

    public void deliverSuccess(final IBuilder builder, final Object obj) {
        if (mLsn != null) {
            mHandler.post(() -> mLsn.onSuccess(builder, obj));
        }
    }

    public void deliverProgress(final IBuilder builder, final float progress, final long contentLength) {
        if (mLsn != null) {
            mHandler.post(() -> mLsn.onProgress(builder, progress, contentLength));
        }
    }

    public void deliverError(IBuilder builder, NetError error) {
        if (mLsn != null) {
            mHandler.post(() -> mLsn.onError(builder, error));
        }
    }
}
