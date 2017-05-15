package lib.network.provider;

import lib.network.error.NetError;

/**
 * 内部网络监听
 */
public interface NativeNetworkListener {
    void onProgress(IBuilder b, float progress, long totalSize);

    void onError(IBuilder b, NetError error);

    void onSuccess(IBuilder b, Object obj);
}