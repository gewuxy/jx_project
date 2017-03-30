package lib.um.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

public class U_Share {

    private static UMShareListener mUMListener;

    private static UMShareAPI mApi;

    /**
     * 初始化友盟
     *
     * @param context
     */
    public static void init(Context context, String appKey) {
        UMShareAPI.init(context, appKey);
        mApi = UMShareAPI.get(context);
    }

    public static void configWX(String id, String secret) {
        PlatformConfig.setWeixin(id, secret);
    }

    public static void configQZone(String id, String key) {
        PlatformConfig.setQQZone(id, key);
    }

    public static void configSinaBlog(String key, String secret) {
        PlatformConfig.setSinaWeibo(key, secret);
    }

    public static void setListener(final OnShareListener l) {
        mUMListener = new UMShareListener() {

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                l.onShareSuccess();
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                l.onShareError();
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                l.onShareCancel();
            }
        };
    }

    public static void share(SHARE_MEDIA media, Activity act, String title, String text, String url, int logoResId) {
        new ShareAction(act)
                .setPlatform(media)
                .withText(text)
                .withTitle(title)
                .withTargetUrl(url)
                .withExtra(new UMImage(act, logoResId))
                .setCallback(mUMListener)
                .share();
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        mApi.onActivityResult(requestCode, resultCode, data);
    }
}
