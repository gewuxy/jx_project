package jx.csp.dialog;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.view.Gravity;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.linkedin.LinkedIn;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import inject.annotation.network.Descriptor;
import jx.csp.BuildConfig;
import jx.csp.R;
import jx.csp.network.NetworkApi;
import jx.csp.sp.SpApp;
import jx.csp.util.Util;
import lib.ys.YSLog;
import lib.ys.util.PackageUtil;
import lib.ys.util.bmp.BmpUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.dialog.BaseDialog;

/**
 * @auther WangLan
 * @since 2017/10/12
 */

public class ShareDialog extends BaseDialog {

    @StringDef({
            LanguageType.English,
            LanguageType.China,
            LanguageType.HkMTw,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface LanguageType {
        String English = "en_US"; // 英文
        String China = "zh_CN";  // 中文
        String HkMTw = "zh_TW";  // 繁体
    }

    @IntDef({
            VersionType.inland,
            VersionType.overseas
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface VersionType {
        int inland = 0; // 国内
        int overseas = 1; // 海外
    }

    private final String KShareText = "CSPmeeting，简单易用的会议录制工具。让你的智慧，被更多人看见！";
    private final String KQQTitleUrl = "http://wiki.mob.com/%e5%88%86%e4%ba%ab%e5%88%b0%e6%8c%87%e5%ae%9a%e5%b9%b3%e5%8f%b0/";
    private final String KDesKey = "2b3e2d604fab436eb7171de397aee892"; // DES秘钥

    public static final String KShareError = "分享失败";

    private String mShareUrl; // 分享的Url
    private String mShareTitle; // 分享的标题

    private PlatformActionListener mPlatformActionListener;

    public ShareDialog(Context context, String shareTitle, int courseId) {
        super(context);
        mShareTitle = shareTitle;
        // 拼接加密字符串
        String local; // 系统语言
        YSLog.d(TAG, "app language = " + SpApp.inst().getSystemLanguage());
        YSLog.d(TAG, "app country = " + SpApp.inst().getCountry());
        // 简体中文和繁体中文字符串资源要分别放到res/values-zh-rCN和res/values-zh-rTW下
        if ("zh".equals(SpApp.inst().getSystemLanguage())) {
            if ("CN".equals(SpApp.inst().getCountry())) {
                local = LanguageType.China;
            } else {
                local = LanguageType.HkMTw;
            }
        } else {
            local = LanguageType.English;
        }
        int abroad;  // 国内版 国外版
        if ("cn".equals(PackageUtil.getMetaValue("JX_LANGUAGE"))) {
            abroad = VersionType.inland;
        } else {
            abroad = VersionType.overseas;
        }
        String param = "id=" + courseId + "&_local=" + local + "&abroad=" + abroad;
        Descriptor des = NetworkApi.class.getAnnotation(Descriptor.class);
        String http = BuildConfig.TEST ? des.hostDebuggable() : des.host();
        mShareUrl = http + "meeting/share?signature=" + Util.encode(KDesKey, param);
        YSLog.d(TAG, "ShareUrl = " + mShareUrl);
    }

    public ShareDialog(Context context, String shareUrl, String shareTitle) {
        super(context);
        mShareUrl = shareUrl;
        mShareTitle = shareTitle;
    }

    @Override
    public void initData() {
        mPlatformActionListener = new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                showToast(R.string.share_success);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                showToast(KShareError.concat(throwable.getMessage()));
            }

            @Override
            public void onCancel(Platform platform, int i) {
                showToast(R.string.share_cancel);
            }
        };
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_share;
    }

    @Override
    public void findViews() {
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.dialog_share_iv_wechat);
        setOnClickListener(R.id.dialog_share_iv_moment);
        setOnClickListener(R.id.dialog_share_iv_qq);
        setOnClickListener(R.id.dialog_share_iv_linkedin);
        setOnClickListener(R.id.dialog_share_iv_weibo);
        setOnClickListener(R.id.dialog_share_iv_message);
        setOnClickListener(R.id.dialog_share_iv_copy_link);
        setOnClickListener(R.id.dialog_share_tv_contribute);
        setOnClickListener(R.id.dialog_share_tv_copy_replica);
        setOnClickListener(R.id.dialog_share_tv_delete);
        setOnClickListener(R.id.dialog_share_tv_cancel);

        setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onClick(View v) {
        ShareParams shareParams = new ShareParams();
        shareParams.setTitle(mShareTitle);
        Platform platform;
        switch (v.getId()) {
            case R.id.dialog_share_iv_wechat: {
                platform = ShareSDK.getPlatform(Wechat.NAME);
                //微信的字段
                shareParams.setShareType(Platform.SHARE_WEBPAGE);
                //Fixme:分享内容的图片，还没有,用的丫丫医师
                shareParams.setImageData(BmpUtil.drawableToBitmap(ResLoader.getDrawable(R.mipmap.ic_launcher)));
                shareParams.setUrl(mShareUrl);
                shareParams.setTitle(mShareTitle);
                shareParams.setText("9999");
                platform.setPlatformActionListener(mPlatformActionListener);
                platform.share(shareParams);
            }
            break;
            case R.id.dialog_share_iv_moment: {
                platform = ShareSDK.getPlatform(WechatMoments.NAME);
                //微信的字段
                shareParams.setShareType(Platform.SHARE_WEBPAGE);
                //Fixme:分享内容的图片，还没有,用的丫丫医师
                shareParams.setImageData(BmpUtil.drawableToBitmap(ResLoader.getDrawable(R.mipmap.ic_launcher)));
                shareParams.setUrl(mShareUrl);
                shareParams.setTitle(mShareTitle);
                shareParams.setText("9999");
                platform.setPlatformActionListener(mPlatformActionListener);
                platform.share(shareParams);
            }
            break;
            case R.id.dialog_share_iv_qq: {
                platform = ShareSDK.getPlatform(QQ.NAME);
                //这是qq必写的参数，否则发不了，短信只能发文字，链接可以写在文字里面
                shareParams.setTitleUrl(mShareUrl);
                platform.setPlatformActionListener(mPlatformActionListener);
                platform.share(shareParams);
            }
            break;
            case R.id.dialog_share_iv_linkedin: {
                platform = ShareSDK.getPlatform(LinkedIn.NAME);
                shareParams.setUrl(mShareUrl);
                platform.setPlatformActionListener(mPlatformActionListener);
                platform.share(shareParams);
            }
            break;
            case R.id.dialog_share_iv_weibo: {
                //微博包冲突
               /* platform = ShareSDK.getPlatform(SinaWeibo.NAME);
                platform.setPlatformActionListener(mPlatformActionListener);
                platform.share(shareParams);*/
                showToast("微博分享");
            }
            break;
            case R.id.dialog_share_iv_message: {
                platform = ShareSDK.getPlatform(ShortMessage.NAME);
                shareParams.setText(KShareText);
                shareParams.setUrl(mShareUrl);
               // shareParams.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
                platform.setPlatformActionListener(mPlatformActionListener);
                platform.share(shareParams);
            }
            break;
            case R.id.dialog_share_iv_copy_link: {
                //Fixme:提示，实际需求没有，一下皆同，记得删
                showToast("复制链接");
            }
            break;
            case R.id.dialog_share_tv_contribute: {
                showToast("复制链接");
            }
            break;
            case R.id.dialog_share_tv_copy_replica: {
                showToast("复制链接");
            }
            break;
            case R.id.dialog_share_tv_delete: {
                showToast("复制链接");
            }
            break;
            case R.id.dialog_share_tv_cancel: {
                //不做处理
                showToast("复制链接");
            }
            break;
        }
        dismiss();
    }
}
