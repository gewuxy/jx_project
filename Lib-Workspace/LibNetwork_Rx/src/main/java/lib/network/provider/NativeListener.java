package lib.network.provider;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import lib.network.NetworkLog;
import lib.network.model.NetworkError;
import lib.network.model.interfaces.OnNetworkListener;

/**
 * @auther yuansui
 * @since 2017/6/11
 */

public class NativeListener {

    private static NativeListener mInst;

    private NativeListener() {
    }

    public static synchronized NativeListener inst() {
        if (mInst == null) {
            mInst = new NativeListener();
        }
        return mInst;
    }

    public void onSuccess(int id, Object result, OnNetworkListener l) {
        Observable.just(l)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lsn -> lsn.onNetworkSuccess(id, result));
    }

    public void onProgress(int id, float progress, long totalSize, OnNetworkListener l) {
        if (NetworkLog.isDebug()) {
            NetworkLog.d("progress = " + progress);
            NetworkLog.d("contentLength = " + totalSize);
            NetworkLog.d("=====================");
        }

        Observable.just(l)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lsn -> lsn.onNetworkProgress(id, progress, totalSize));
    }

    public void onError(int id, NetworkError error, OnNetworkListener l) {
        Observable.just(l)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lsn -> lsn.onNetworkError(id, error));
    }
}
