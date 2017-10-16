package jx.csp.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;

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
import jx.csp.R;
import lib.ys.util.bmp.BmpUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.dialog.BaseDialog;

/**
 * @auther WangLan
 * @since 2017/10/12
 */

public class ShareDialog extends BaseDialog {

    private final String KShareText = "CSPmeeting，简单易用的会议录制工具。让你的智慧，被更多人看见！";
    private final String KQQTitleUrl = "http://wiki.mob.com/%e5%88%86%e4%ba%ab%e5%88%b0%e6%8c%87%e5%ae%9a%e5%b9%b3%e5%8f%b0/";

    public static final String KShareError = "分享失败";

    private String mShareUrl; // 分享的Url
    private String mShareTitle; // 分享的标题

    private PlatformActionListener mPlatformActionListener;

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

        Platform platform;
        switch (v.getId()) {
            case R.id.dialog_share_iv_wechat: {
                platform = ShareSDK.getPlatform(Wechat.NAME);
                //微信的字段
                shareParams.setShareType(Platform.SHARE_WEBPAGE);
                //Fixme:分享内容的图片，还没有,用的丫丫医师
                shareParams.setImageData(BmpUtil.drawableToBitmap(ResLoader.getDrawable(R.mipmap.ic_launcher)));
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
                platform.setPlatformActionListener(mPlatformActionListener);
                platform.share(shareParams);
            }
            break;
            case R.id.dialog_share_iv_qq: {
                platform = ShareSDK.getPlatform(QQ.NAME);
                //这是qq必写的参数，否则发不了，短信只能发文字，链接可以写在文字里面
                shareParams.setTitleUrl(KQQTitleUrl);
                shareParams.setTitle(mShareTitle);
                platform.setPlatformActionListener(mPlatformActionListener);
                platform.share(shareParams);
            }
            break;
            case R.id.dialog_share_iv_linkedin: {
                platform = ShareSDK.getPlatform(LinkedIn.NAME);
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
                shareParams.setTitle(mShareTitle);
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
