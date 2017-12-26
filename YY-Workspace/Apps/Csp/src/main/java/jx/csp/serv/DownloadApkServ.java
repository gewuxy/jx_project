package jx.csp.serv;

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
    private final int NO_3 = 0;
    private final String KFileName = "CSP.apk";

    private String mUrl;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mManager;

    @Override
    protected void onHandleIntent(Intent intent) {
        mUrl = intent.getStringExtra(Extra.KData);
        mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(getString(R.string.app_name));
        mBuilder.setContentText(getString(R.string.download_ing));
        mBuilder.setProgress(100, 0, false);

        mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mManager.notify(NO_3, mBuilder.build());

        exeNetworkReq(CommonAPI.downloadApk(CacheUtil.getApkCacheDir(), KFileName, mUrl).build());
    }

    @Override
    public void onNetworkProgress(int id, float progress, long totalSize) {
        int percent = (int) progress;
        YSLog.d(TAG, "onNetworkProgress = " + percent);
        if (percent == 100) {
            YSLog.d(TAG, "onNetworkProgress == 100 啦");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(CacheUtil.getApkCacheDir() + KFileName)), "application/vnd.android.package-archive");
            // 利用PendingIntent来包装我们的intent对象
            PendingIntent intentPend = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            mBuilder.setContentIntent(intentPend);
            // mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);//设置通知铃声
            mBuilder.setProgress(100, 100, false);
            mBuilder.setContentText(getString(R.string.click_install));
        } else {
            mBuilder.setAutoCancel(false);
            mBuilder.setProgress(100, percent, false);
            mBuilder.setContentText(String.format(getString(R.string.already_download), percent));
        }
        mManager.notify(NO_3, mBuilder.build());
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        YSLog.d(TAG, "onNetworkSuccess");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(CacheUtil.getApkCacheDir() + KFileName)), "application/vnd.android.package-archive");
        // 利用PendingIntent来包装我们的intent对象
        PendingIntent intentPend = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(intentPend);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);//设置通知铃声
        mBuilder.setProgress(100, 100, false);
        mBuilder.setContentText(getString(R.string.click_install));
        mManager.notify(NO_3, mBuilder.build());
        stopSelf();
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        YSLog.d(TAG, "onNetworkError");
        mBuilder.setAutoCancel(true);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);//设置通知铃声
        mBuilder.setContentText(getString(R.string.download_fail));
        mBuilder.setProgress(100, 0, false);
        mManager.notify(NO_3, mBuilder.build());
        stopSelf();
    }
}
