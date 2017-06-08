package lib.network.provider;

import lib.network.LogNetwork;
import lib.network.model.NetworkReq;
import lib.network.model.err.NetError;
import lib.network.model.interfaces.OnNetworkListener;

/**
 * @author yuansui
 */
abstract public class BaseProvider implements NativeNetworkListener {

    protected String TAG = getClass().getSimpleName();

    private Delivery mDelivery;
    private Object mTag;

    public BaseProvider(Object tag) {
        mTag = tag;
        mDelivery = new Delivery(this);
    }

    @Override
    public void onProgress(IBuilder b, float progress, long totalSize) {
        if (LogNetwork.isDebug()) {
            LogNetwork.d("progress = " + progress);
            LogNetwork.d("contentLength = " + totalSize);
            LogNetwork.d("=====================");
        }
        if (b.getListener() != null) {
            b.getListener().onNetworkProgress(b.getId(), progress, totalSize);
        }
    }

    @Override
    public void onSuccess(IBuilder b, Object obj) {
        if (b.getListener() != null) {
            b.getListener().onNetworkSuccess(b.getId(), obj);
        }
    }

    @Override
    public void onError(IBuilder b, NetError error) {
        if (b.getListener() != null) {
            b.getListener().onNetworkError(b.getId(), error);
        }
    }


    protected Object tag() {
        return mTag;
    }

    protected Delivery getDelivery() {
        return mDelivery;
    }

    abstract public void load(NetworkReq networkReq, int id, OnNetworkListener lsn);

    abstract public void cancel(int id);

    abstract public void cancelAll();
}
