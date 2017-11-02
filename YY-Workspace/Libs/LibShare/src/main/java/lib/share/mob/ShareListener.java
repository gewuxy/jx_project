package lib.share.mob;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/**
 * @auther WangLan
 * @since 2017/10/31
 */

public class ShareListener implements PlatformActionListener {

    private ShareCallback mShareCallback;

    public ShareListener(ShareCallback callback) {
        mShareCallback = callback;
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        mShareCallback.shareComplete();
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        mShareCallback.shareError();
    }

    @Override
    public void onCancel(Platform platform, int i) {
        mShareCallback.shareCancel();
    }
}
