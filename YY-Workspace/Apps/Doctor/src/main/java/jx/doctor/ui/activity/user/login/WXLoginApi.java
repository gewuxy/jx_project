package jx.doctor.ui.activity.user.login;

import android.content.Context;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * @auther : GuoXuan
 * @since : 2017/7/17
 */

public class WXLoginApi {

    private static IWXAPI mApi;

    public static IWXAPI create(Context context, String appId) {
        if (mApi == null) {
            mApi = WXAPIFactory.createWXAPI(context, appId, false);
        }
        return mApi;
    }

    /**
     * 是否安装了微信客户端
     *
     * @return
     */
    public static boolean isWXAppInstalled() {
        if (mApi == null) {
            return false;
        }
        if (mApi.isWXAppInstalled()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 向微信端发起请求
     */
    public static void sendReq(String state) {
        if (mApi == null) {
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        /**
         * 用于保持请求和回调的状态, 授权请求后原样带回给第三方
         * 该参数可用于防止csrf攻击 (跨站请求伪造攻击)
         * 建议第三方带上该参数, 可设置为简单的随机数加session进行校验
         */
        req.state = state; // 作用(分辨来自于哪里?)
        mApi.sendReq(req);
    }

    public static void detach() {
        if (mApi == null) {
            return;
        }
        mApi.detach();
        mApi = null;
    }
}
