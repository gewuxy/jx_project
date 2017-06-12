package lib.yy;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lib.ys.model.NotifierEx;
import lib.yy.DownloadNotifier.OnDownloadNotify;

/**
 * 下载的通知
 *
 * @author yuansui
 */

public class DownloadNotifier extends NotifierEx<OnDownloadNotify> {

    @IntDef({
            DownloadNotifyType.progress,
            DownloadNotifyType.complete,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DownloadNotifyType {
        int progress = 2;
        int complete = 3;
    }

    @Override
    protected void callback(OnDownloadNotify o, int type, Object data) {
        o.onDownloadNotify(type, data);
    }

    public interface OnDownloadNotify {
        void onDownloadNotify(@DownloadNotifyType int type, Object data);
    }

    private static DownloadNotifier mInst = null;

    public static DownloadNotifier inst() {
        if (mInst == null) {
            synchronized (DownloadNotifier.class) {
                if (mInst == null) {
                    mInst = new DownloadNotifier();
                }
            }
        }
        return mInst;
    }

}
