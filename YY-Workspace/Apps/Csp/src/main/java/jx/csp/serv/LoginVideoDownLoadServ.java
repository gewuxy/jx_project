package jx.csp.serv;

import android.content.Intent;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import jx.csp.network.NetworkApiDescriptor.UserAPI;
import jx.csp.util.CacheUtil;
import lib.ys.service.ServiceEx;

/**
 * @auther WangLan
 * @since 2017/11/6
 */
@Route
public class LoginVideoDownLoadServ extends ServiceEx {

    public final String KFileName = "login_background_video.mp4";

    @Arg
    String mUrl;

    @Override
    protected void onHandleIntent(Intent intent) {
        exeNetworkReq(UserAPI.downLoad(CacheUtil.getAudioCacheDir(), KFileName, mUrl).build());
    }
}
