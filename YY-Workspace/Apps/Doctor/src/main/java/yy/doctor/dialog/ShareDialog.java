package yy.doctor.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;

import java.util.HashMap;

import cn.jiguang.share.android.api.JShareInterface;
import cn.jiguang.share.android.api.PlatActionListener;
import cn.jiguang.share.android.api.Platform;
import cn.jiguang.share.android.api.ShareParams;
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

    /**
     * 数值不能更改
     */
    private final int KIdWeChat = 0;
    private final int KIdWeChatMoments = 1;
    private final int KIdSinaWeiBo = 3;

    private String mShareUrl; // 分享的Url
    private String mShareTitle; // 分享的标题

    private PlatActionListener mPlatActionListener;

    public ShareDialog(Context context, String shareUrl, String shareTitle) {
        super(context);

        mShareUrl = shareUrl;
        mShareTitle = shareTitle;
    }

    @Override
    public void initData() {
        mPlatActionListener = new PlatActionListener() {

            @Override
            public void onComplete(Platform platform, int action, HashMap<String, Object> data) {
                showToast(KShareSuccess);
            }

            @Override
            public void onError(Platform platform, int action, int errorCode, Throwable error) {
                showToast(KShareError.concat(error.getMessage()));
            }

            @Override
            public void onCancel(Platform platform, int action) {
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

        String platName;
        switch (v.getId()) {
            case R.id.dialog_share_iv_wechat_friend_cicle: {
                platName = JShareInterface.getPlatformList().get(KIdWeChatMoments);
                JShareInterface.share(platName, shareParams, mPlatActionListener);
            }
            break;
            case R.id.dialog_share_iv_wechat_friends: {
                platName = JShareInterface.getPlatformList().get(KIdWeChat);
                JShareInterface.share(platName, shareParams, mPlatActionListener);
            }
            break;
            case R.id.dialog_share_iv_sina: {
                platName = JShareInterface.getPlatformList().get(KIdSinaWeiBo);
                JShareInterface.share(platName, shareParams, mPlatActionListener);
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
