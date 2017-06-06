package yy.doctor.serv;

import android.content.Intent;

import lib.ys.service.ServiceEx;
import lib.yy.DownloadNotifier;
import lib.yy.DownloadNotifier.DownloadNotifyType;
import lib.yy.DownloadNotifier.OnDownloadNotify;
import yy.doctor.Extra;
import yy.doctor.network.NetFactory;
import yy.doctor.util.CacheUtil;

/**
 * @author CaiXiang
 * @since 2017/6/2
 */

public class DownloadServ extends ServiceEx implements OnDownloadNotify {

    private boolean isNotifyTotalSize = false;
    private String mUrl;
    private String mFilePath;
    private String mType;
    private String mFileHashCode;

    @Override
    protected void onHandleIntent(Intent intent) {
        DownloadNotifier.inst().add(this);

        mUrl = intent.getStringExtra(Extra.KData);

        int id = intent.getIntExtra(Extra.KUnitNumId, 0);
        mFilePath = CacheUtil.getUnitNumCacheDir(id);

        mType = intent.getStringExtra(Extra.KType);
        mFileHashCode = String.valueOf(mUrl.hashCode()) + "." + mType;
        exeNetworkReq(NetFactory.downloadData(mUrl, mFilePath, mFileHashCode));
    }

//    @Override
//    public Object onNetworkResponse(int id, NetworkResp r) throws JSONException {
//        File f = new File(CacheUtil.getDownloadCacheDir(), mFileHashCode);
//        return FileUtil.saveFile(f, r.getText());
//    }

    @Override
    public void onNetworkProgress(int id, float progress, long totalSize) {
        if (!isNotifyTotalSize) {
            DownloadServ.this.notify(DownloadNotifyType.totalSize, totalSize);
            isNotifyTotalSize = true;
        }
        DownloadServ.this.notify(DownloadNotifyType.progress, progress);
    }

    //通知
    @Override
    public void onDownloadNotify(@DownloadNotifyType int type, Object data) {
    }

    protected void notify(@DownloadNotifyType int type, Object data) {
        DownloadNotifier.inst().notify(type, data);
    }

    protected void notify(@DownloadNotifyType int type) {
        DownloadNotifier.inst().notify(type);
    }

}
