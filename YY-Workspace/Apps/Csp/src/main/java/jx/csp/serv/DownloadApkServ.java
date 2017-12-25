package jx.csp.serv;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

import java.io.File;

import jx.csp.Extra;
import jx.csp.R;
import jx.csp.network.NetworkApiDescriptor.CommonAPI;
import jx.csp.util.CacheUtil;
import lib.network.model.NetworkError;
import lib.network.model.interfaces.IResult;
import lib.ys.service.ServiceEx;

/**
 * 后台下载apk服务
 *
 * @author CaiXiang
 * @since 2017/6/12
 */

public class DownloadApkServ extends ServiceEx {

    //定义notification实用的ID
    private final int NO_3 = 3;
    private final String KFileName = getString(R.string.app_name) + ".apk";

    private String mUrl;
    private Intent mIntent;
    private Builder mBuilder;
    private NotificationManager mManager;

    @Override
    protected void onHandleIntent(Intent intent) {
        mUrl = intent.getStringExtra(Extra.KData);
        mBuilder = new Builder(this);

        mIntent = new Intent(Intent.ACTION_VIEW);
        mIntent.setDataAndType(Uri.fromFile(new File(CacheUtil.getApkCacheDir() + KFileName)), "application/vnd.android.package-archive");
        //利用PendingIntent来包装我们的intent对象
        PendingIntent intentPend = PendingIntent.getActivity(this, 0, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(intentPend);

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
        mBuilder.setAutoCancel(false);
        mBuilder.setProgress(100, percent, false);
        mBuilder.setContentText(String.format(getString(R.string.already_download), percent));
        mManager.notify(NO_3, mBuilder.build());
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        mBuilder.setContentText(getString(R.string.click_install));
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);//设置通知铃声
        mBuilder.setProgress(100, 100, true);
        mManager.notify(NO_3, mBuilder.build());
        stopSelf();
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        mBuilder.setAutoCancel(true);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);//设置通知铃声
        mBuilder.setContentText(getString(R.string.download_fail));
        mBuilder.setProgress(0, 0, true);
        mManager.notify(NO_3, mBuilder.build());
        stopSelf();
    }
}
