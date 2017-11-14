package yy.doctor.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import lib.ys.util.bmp.BmpUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.dialog.BaseDialog;
import yy.doctor.R;

/**
 * 分享的dialog
 *
 * @author CaiXiang
 * @since 2017/4/19
 */
public class ShareDialog extends BaseDialog {

    private final String KShareText = "YaYa医师欢迎您，这里有您感兴趣的学术会议，寻找探讨专业知识的群体";

    public static final String KShareSuccess = "分享成功";
    public static final String KShareError = "分享失败";
    public static final String KShareCancel = "分享取消";
    

    private String mShareUrl; // 分享的Url
    private String mShareTitle; // 分享的标题

    private PlatformActionListener mPlatFormActionListener;


    public ShareDialog(Context context, String shareUrl, String shareTitle) {
        super(context);

        mShareUrl = shareUrl;
        mShareTitle = shareTitle;
    }

    @Override
    public void initData(Bundle state) {
        mPlatFormActionListener = new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                showToast(KShareSuccess);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                showToast(KShareError.concat(throwable.getMessage()));
            }

            @Override
            public void onCancel(Platform platform, int i) {
                showToast(KShareCancel);
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
        setOnClickListener(R.id.dialog_share_iv_wechat_friend_cicle);
        setOnClickListener(R.id.dialog_share_iv_wechat_friends);
        setOnClickListener(R.id.dialog_share_iv_sina);
        setOnClickListener(R.id.dialog_share_tv_cancel);

        setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onClick(View v) {
        ShareParams shareParams = new ShareParams();
        shareParams.setShareType(Platform.SHARE_WEBPAGE);
        shareParams.setImageData(BmpUtil.drawableToBitmap(ResLoader.getDrawable(R.mipmap.ic_launcher)));
        shareParams.setTitle(mShareTitle);
        shareParams.setText(KShareText);
        shareParams.setUrl(mShareUrl);
        Platform platform;

        switch (v.getId()) {
            case R.id.dialog_share_iv_wechat_friend_cicle: {
                platform = ShareSDK.getPlatform(Wechat.NAME);
                shareParams.setShareType(Platform.SHARE_WEBPAGE);
                platform.setPlatformActionListener(mPlatFormActionListener);
                platform.share(shareParams);
            }
            break;
            case R.id.dialog_share_iv_wechat_friends: {
                platform = ShareSDK.getPlatform(WechatMoments.NAME);
                shareParams.setShareType(Platform.SHARE_WEBPAGE);
                platform.setPlatformActionListener(mPlatFormActionListener);
                platform.share(shareParams);
            }
            break;
            case R.id.dialog_share_iv_sina: {
                platform = ShareSDK.getPlatform(SinaWeibo.NAME);
                platform.setPlatformActionListener(mPlatFormActionListener);
                platform.share(shareParams);
            }
            break;
            case R.id.dialog_share_tv_cancel: {
                // do nothing
            }
            break;
        }
        dismiss();
    }

}
