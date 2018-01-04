package jx.csp.serv;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.io.File;

import jx.csp.Extra;
import jx.csp.R;
import jx.csp.network.NetworkApiDescriptor.CommonAPI;
import jx.csp.util.CacheUtil;
import lib.network.model.NetworkError;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.service.ServiceEx;

/**
 * 后台下载apk服务
 *
 * @author CaiXiang
 * @since 2017/6/12
 */

public class DownloadApkServ extends ServiceEx {

    //定义notification实用的ID
    private final int NotifyId = 0;
    private final String KFileName = "CSP.apk";

    private String mUrl;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mManager;

    @Override
    protected void onHandleIntent(Intent intent) {
        mUrl = intent.getStringExtra(Extra.KData);
        mBuilder = new NotificationCompat.Builder(this);

        mBuilder.mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(getString(R.string.app_name));
        mBuilder.setContentText(getString(R.string.download_ing));
        mBuilder.setProgress(100, 0, false);
        mBuilder.setAutoCancel(true);

        mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mManager.notify(NotifyId, mBuilder.build());

        exeNetworkReq(CommonAPI.downloadApk(CacheUtil.getApkCacheDir(), KFileName, mUrl).build());
    }

    @Override
    public void onNetworkProgress(int id, float progress, long totalSize) {
        int percent = (int) progress;
        YSLog.d(TAG, "onNetworkProgress = " + percent);
        if (percent % 10 == 0) {
            mBuilder.setProgress(100, percent, false);
            mBuilder.setContentText(String.format(getString(R.string.already_download), percent));
            mManager.notify(NotifyId, mBuilder.build());
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(CacheUtil.getApkCacheDir() + KFileName)), "application/vnd.android.package-archive");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);//设置通知铃声
        mBuilder.setProgress(0, 0, false);
        mBuilder.setContentText(getString(R.string.click_install));
        mManager.notify(NotifyId, mBuilder.build());
        YSLog.d(TAG, "onNetworkSuccess");
        stopSelf();
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        YSLog.d(TAG, "onNetworkError");
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);//设置通知铃声
        mBuilder.setContentText(getString(R.string.download_fail));
        mBuilder.setProgress(100, 0, false);
        mManager.notify(NotifyId, mBuilder.build());
        stopSelf();
    }
}
