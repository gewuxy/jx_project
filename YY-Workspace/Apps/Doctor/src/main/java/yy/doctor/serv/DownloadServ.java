package yy.doctor.serv;

import android.content.Intent;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.ys.YSLog;
import lib.ys.service.ServiceEx;
import lib.yy.notify.DownloadNotifier;
import lib.yy.notify.DownloadNotifier.DownloadNotifyType;
import lib.yy.notify.DownloadNotifier.OnDownloadNotify;
import yy.doctor.network.NetworkAPISetter.DataAPI;

/**
 * @author CaiXiang
 * @since 2017/6/2
 */
@Route
public class DownloadServ extends ServiceEx implements OnDownloadNotify {

    @Arg
    String mUrl;

    @Arg
    String mFilePath;

    @Arg
    String mType;

    private String mFileNameHashCode;
    private String mFileNameEncryption;

    @Override
    protected void onHandleIntent(Intent intent) {
        DownloadNotifier.inst().add(this);

        mFileNameHashCode = String.valueOf(mUrl.hashCode()) + mType;
        YSLog.d(TAG, " download FileNameHashCode = " + mFileNameHashCode);

        //打乱文件名
        int shift = 5;
        StringBuffer sb = new StringBuffer();
        char[] chars = mFileNameHashCode.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = (char) (chars[i] + shift);
            sb.append(c);
        }
        mFileNameEncryption = sb.toString();
        YSLog.d(TAG, " download FileNameEncryption = " + mFileNameEncryption);

        exeNetworkReq(DataAPI.download(mFilePath, mFileNameEncryption, mUrl).build());

    }

    @Override
    public void onNetworkProgress(int id, float progress, long totalSize) {
        notify(DownloadNotifyType.progress, new Download(progress, totalSize));
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        notify(DownloadNotifyType.complete, null);
    }

    //通知
    @Override
    public void onDownloadNotify(@DownloadNotifyType int type, Object data) {
    }

    protected void notify(@DownloadNotifyType int type, Object data) {
        DownloadNotifier.inst().notify(type, data);
    }

    public class Download {
        private long mTotalSize;
        private float mProgress;

        public Download(float p, long size) {
            mProgress = p;
            mTotalSize = size;
        }

        public long getTotalSize() {
            return mTotalSize;
        }

        public float getProgress() {
            return mProgress;
        }
    }

}
