package yy.doctor.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import cn.jiguang.share.android.api.JShareInterface;
import cn.jiguang.share.android.api.PlatActionListener;
import cn.jiguang.share.android.api.Platform;
import cn.jiguang.share.android.api.ShareParams;
import lib.ys.YSLog;
import lib.ys.util.bmp.BmpUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.dialog.BaseDialog;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/19
 */
public class ShareDialog extends BaseDialog {

    private static final int KIdWechat = 0;
    private static final int KIdWechatMoments = 1;
    private static final int KIdWechatFavorite = 2;
    private static final int KIdSinaWeibo = 3;
    private ImageView mIvWechatFriendCircle;
    private ImageView mIvWechatFriends;
    private ImageView mIvSina;

    private TextView mTvCancel;

    private List<String> mPlatformList;
    private String mShareUrl ;
    private String mShareText = "YaYa医师欢迎您，这里有您感兴趣的学术会议，寻找探讨专业知识的群体";
    private String mShareTitle ;
    private String mPlatName;

    public ShareDialog(Context context) {
        super(context);
    }

    public ShareDialog(Context context, String mShareUrl, String mShareTitle) {
        super(context);
        this.mShareUrl = mShareUrl;
        this.mShareTitle = mShareTitle;
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            String toastMsg = (String) msg.obj;
            showToast(toastMsg);
        }
    };

    private PlatActionListener mPlatActionListener = new PlatActionListener() {

        @Override
        public void onComplete(Platform platform, int action, HashMap<String, Object> data) {
            Message message = handler.obtainMessage();
            message.obj = "分享成功";
            handler.sendMessage(message);
        }

        @Override
        public void onError(Platform platform, int action, int errorCode, Throwable error) {
            Message message = handler.obtainMessage();
            message.obj = "分享失败:" + error.getMessage();
            handler.sendMessage(message);
        }

        @Override
        public void onCancel(Platform platform, int action) {
            Message message = handler.obtainMessage();
            message.obj = "分享取消";
            handler.sendMessage(message);
        }
    };

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_share;
    }

    @Override
    public void findViews() {

        mIvWechatFriendCircle = findView(R.id.dialog_share_iv_wechat_friend_cicle);
        mIvWechatFriends = findView(R.id.dialog_share_iv_wechat_friends);
        mIvSina = findView(R.id.dialog_share_iv_sina);
        mTvCancel = findView(R.id.dialog_share_tv_cancel);
    }

    @Override
    public void setViews() {
        mIvWechatFriendCircle.setOnClickListener(this);
        mIvWechatFriends.setOnClickListener(this);
        mIvSina.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
        setGravity(Gravity.BOTTOM);

        mPlatformList = JShareInterface.getPlatformList();
        YSLog.d(TAG, "mPlatformList = " + mPlatformList.size());
        for (int i = 0; i < mPlatformList.size(); ++i) {
            YSLog.d(TAG, "platname = " + mPlatformList.get(i));
        }
    }

    @Override
    public void onClick(View v) {

        ShareParams shareParams = new ShareParams();
        shareParams.setShareType(Platform.SHARE_WEBPAGE);
        shareParams.setImageData(BmpUtil.drawableToBitmap(ResLoader.getDrawable(R.mipmap.ic_launcher_48)));
        shareParams.setTitle(mShareTitle);
        shareParams.setText(mShareText);
        shareParams.setUrl(mShareUrl);

        int id = v.getId();
        switch (id) {
            case R.id.dialog_share_iv_wechat_friend_cicle: {
                mPlatName = mPlatformList.get(KIdWechatMoments);
                JShareInterface.share(mPlatName, shareParams, mPlatActionListener);
            }
            break;
            case R.id.dialog_share_iv_wechat_friends: {
                mPlatName = mPlatformList.get(KIdWechat);
                JShareInterface.share(mPlatName, shareParams, mPlatActionListener);
            }
            break;
            case R.id.dialog_share_iv_sina: {
                mPlatName = mPlatformList.get(KIdSinaWeibo);
                JShareInterface.share(mPlatName, shareParams, mPlatActionListener);
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
