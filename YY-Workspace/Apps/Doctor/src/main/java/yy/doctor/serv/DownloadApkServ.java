package yy.doctor.serv;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

import java.io.File;

import lib.network.model.NetworkError;
import lib.ys.service.ServiceEx;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.network.NetworkAPISetter.DataAPI;
import yy.doctor.util.CacheUtil;

/**
 * 后台下载apk服务
 *
 * @author CaiXiang
 * @since 2017/6/12
 */

public class DownloadApkServ extends ServiceEx {

    //定义notification实用的ID
    private final int NO_3 = 3;
    private final String KFileName = "YaYa医师.apk";

    private String mUrl;
    private Intent mIntent;
    private Builder mBuilder;
    private NotificationManager mManager;

    @Override
    protected void onHandleIntent(Intent intent) {
        mUrl = intent.getStringExtra(Extra.KData);
        mBuilder = new Builder(this);

        //点击安装
        mIntent = new Intent(Intent.ACTION_VIEW);
        mIntent.setDataAndType(Uri.fromFile(new File(CacheUtil.getUploadCacheDir() + KFileName)), "application/vnd.android.package-archive");
        //利用PendingIntent来包装我们的intent对象,使其延迟跳转
        PendingIntent intentPend = PendingIntent.getActivity(this, 0, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(intentPend);

        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("YaYa医师");
        mBuilder.setContentText("正在下载");
        mBuilder.setProgress(100, 0, false);

        mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mManager.notify(NO_3, mBuilder.build());

        exeNetworkReq(DataAPI.download(CacheUtil.getUploadCacheDir(), KFileName, mUrl).build());
    }

    @Override
    public void onNetworkProgress(int id, float progress, long totalSize) {
        int percent = (int) progress;
        mBuilder.setAutoCancel(false);
        mBuilder.setProgress(100, percent, false);
        mBuilder.setContentText("已下载" + percent + "%");
        mManager.notify(NO_3, mBuilder.build());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        mBuilder.setContentText("点击安装");
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);//设置通知铃声
        //设置进度为不确定，用于模拟安装
        mBuilder.setProgress(100, 100, true);
        mManager.notify(NO_3, mBuilder.build());
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        mBuilder.setAutoCancel(true);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);//设置通知铃声
        mBuilder.setContentText("下载失败");
        mBuilder.setProgress(0, 0, true);
        mManager.notify(NO_3, mBuilder.build());
    }

}
