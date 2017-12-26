package jx.doctor.serv;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

import java.io.File;

import jx.doctor.Extra;
import jx.doctor.network.NetworkApiDescriptor.DataAPI;
import jx.doctor.util.CacheUtil;
import lib.network.model.NetworkError;
import lib.network.model.interfaces.IResult;
import lib.ys.service.ServiceEx;
import lib.ys.util.PackageUtil;
import lib.ys.util.res.ResLoader;
import lib.ys.util.res.ResUtil.ResDefType;

/**
 * 后台下载apk服务
 *
 * @author CaiXiang
 * @since 2017/6/12
 */

public class DownloadApkServ extends ServiceEx {

    //定义notification实用的ID
    private final int NotifyId = 1;
    private final String KFileName = "YaYa医师.apk";

    private String mUrl;
    private Builder mBuilder;
    private NotificationManager mManager;

    @Override
    protected void onHandleIntent(Intent intent) {
        mUrl = intent.getStringExtra(Extra.KData);
        mBuilder = new Builder(this);

        mBuilder.setSmallIcon(ResLoader.getIdentifier(PackageUtil.getMetaValue("APP_ICON"), ResDefType.mipmap));
        mBuilder.setContentTitle("YaYa医师");
        mBuilder.setContentText("正在下载");
        mBuilder.setProgress(100, 0, false);

        mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mManager.notify(NotifyId, mBuilder.build());

        exeNetworkReq(DataAPI.download(CacheUtil.getUploadCacheDir(), KFileName, mUrl).build());
    }

    @Override
    public void onNetworkProgress(int id, float progress, long totalSize) {
        int percent = (int) progress;
        if (percent % 10 == 0) {
            mBuilder.setAutoCancel(false);
            mBuilder.setProgress(100, percent, false);
            mBuilder.setContentText("已下载" + percent + "%");
            mManager.notify(NotifyId, mBuilder.build());
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        //点击安装
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(CacheUtil.getUploadCacheDir() + KFileName)), "application/vnd.android.package-archive");
        PendingIntent intentPend = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(intentPend);

        mBuilder.setContentText("点击安装");
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);//设置通知铃声
        mBuilder.setProgress(0, 0, false);
        mManager.notify(NotifyId, mBuilder.build());
        stopSelf();
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        mBuilder.setAutoCancel(true);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);//设置通知铃声
        mBuilder.setContentText("下载失败");
        mBuilder.setProgress(0, 0, false);
        mManager.notify(NotifyId, mBuilder.build());
        stopSelf();
    }

}
