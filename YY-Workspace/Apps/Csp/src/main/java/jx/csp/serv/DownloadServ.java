package jx.csp.serv;

import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.sp.SpApp;
import jx.csp.util.CacheUtil;
import lib.network.model.NetworkError;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.service.ServiceEx;

/**
 * 下载专用
 *
 * @auther GuoXuan
 * @since 2017/11/10
 */
@Route
public class DownloadServ extends ServiceEx {

    @Arg
    @DownReqType
    int mType;
    @Arg
    String mUrl;
    @Arg
    String mFileName;

    @Arg(opt = true)
    int mNewVersion;

    @IntDef({
            DownReqType.login_video,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DownReqType {
        int login_video = 0;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        switch (mType) {
            case DownReqType.login_video: {
                exeNetworkReq(mType, UserAPI.downLoad(CacheUtil.getVideoCacheDir(), mFileName, mUrl).build());
            }
            break;
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        switch (id) {
            case DownReqType.login_video: {
                SpApp.inst().saveLoginVideoVersion(mNewVersion);
            }
        }
        stopSelf();
        YSLog.d(TAG, "下载成功");
    }

    @Override
    public void onNetworkError(int id, NetworkError error) {
        super.onNetworkError(id, error);

        YSLog.d(TAG, "重新下载");
        retryNetworkRequest(id);
    }
}
