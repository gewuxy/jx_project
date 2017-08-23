package yy.doctor.serv;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

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

    private String mUrl;

    //定义notification实用的ID
    private static final int NO_3 = 3;
    private NotificationCompat.Builder builder;
    private NotificationManager manager;
    private Intent mIntent;

    private String mFileName = "YaYa医师.apk";

    @Override
    protected void onHandleIntent(Intent intent) {

        mUrl = intent.getStringExtra(Extra.KData);
        //点击安装
        mIntent = new Intent(Intent.ACTION_VIEW);
        mIntent.setDataAndType(Uri.fromFile(new File(CacheUtil.getUploadCacheDir() + mFileName)), "application/vnd.android.package-archive");

        builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("YaYa医师");
        builder.setContentText("正在下载");
        //利用PendingIntent来包装我们的intent对象,使其延迟跳转
        PendingIntent intentPend = PendingIntent.getActivity(this, 0, mIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(intentPend);

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder.setProgress(100, 0, false);
        manager.notify(NO_3, builder.build());

        exeNetworkReq(DataAPI.download(CacheUtil.getUploadCacheDir(), mFileName, mUrl).build());
    }

    @Override
    public void onNetworkProgress(int id, float progress, long totalSize) {

        int percent = (int) progress;
        builder.setAutoCancel(false);
        builder.setProgress(100, percent, false);
        builder.setContentText("已下载" + percent + "%");
        manager.notify(NO_3, builder.build());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {

        builder.setContentText("点击安装");
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);//设置通知铃声
        //设置进度为不确定，用于模拟安装
        builder.setProgress(100, 100, true);
        manager.notify(NO_3, builder.build());
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        builder.setAutoCancel(true);
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);//设置通知铃声
        builder.setContentText("下载失败");
        builder.setProgress(0, 0, true);
        manager.notify(NO_3, builder.build());
    }

}
