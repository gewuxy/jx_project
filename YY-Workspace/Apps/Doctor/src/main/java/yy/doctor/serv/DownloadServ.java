package yy.doctor.serv;

import android.content.Intent;

import lib.ys.YSLog;
import lib.ys.service.ServiceEx;
import lib.yy.DownloadNotifier;
import lib.yy.DownloadNotifier.DownloadNotifyType;
import lib.yy.DownloadNotifier.OnDownloadNotify;
import yy.doctor.Extra;
import yy.doctor.network.NetFactory;

/**
 * @author CaiXiang
 * @since 2017/6/2
 */

public class DownloadServ extends ServiceEx implements OnDownloadNotify {

    private String mUrl;
    private String mFilePath;
    private String mType;
    private String mFileHashCode;

    @Override
    protected void onHandleIntent(Intent intent) {
        DownloadNotifier.inst().add(this);

        mUrl = intent.getStringExtra(Extra.KData);
        mFilePath = intent.getStringExtra(Extra.KFilePath);

        mType = intent.getStringExtra(Extra.KType);
        mFileHashCode = String.valueOf(mUrl.hashCode()) + "." + mType;

        //打乱文件名
        int shift = mFileHashCode.length() / 2;
        StringBuffer sb = new StringBuffer();
        char[] chars = mFileHashCode.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = (char) (chars[i] + shift);
            sb.append(c);
        }
        YSLog.d(TAG, "sb = " + sb.toString());
        mFileHashCode = sb.toString();

        exeNetworkReq(NetFactory.downloadData(mUrl, mFilePath, mFileHashCode));
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
