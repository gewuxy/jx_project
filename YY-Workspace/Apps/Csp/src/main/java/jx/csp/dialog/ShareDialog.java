package jx.csp.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URLEncoder;

import inject.annotation.network.Descriptor;
import jx.csp.BuildConfig;
import jx.csp.R;
import jx.csp.network.NetworkApi;
import jx.csp.sp.SpApp;
import jx.csp.ui.activity.me.ContributePlatformActivity;
import jx.csp.util.Util;
import lib.platform.Platform;
import lib.platform.Platform.Type;
import lib.platform.listener.OnShareListener;
import lib.platform.model.ShareParams;
import lib.ys.YSLog;
import lib.ys.util.PackageUtil;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionChecker;
import lib.ys.util.res.ResLoader;
import lib.yy.dialog.BaseDialog;

import static android.content.Context.CLIPBOARD_SERVICE;
import static lib.ys.util.res.ResLoader.getString;

/**
 * @auther WangLan
 * @since 2017/10/12
 */

public class ShareDialog extends BaseDialog {

    @StringDef({
            LanguageType.en,
            LanguageType.cn_simplified,
            LanguageType.cn,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface LanguageType {
        String en = "en_US"; // 英文
        String cn_simplified = "zh_CN";  // 中文
        String cn = "zh_TW";  // 繁体
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

    private final String KDesKey = "2b3e2d604fab436eb7171de397aee892"; // DES秘钥

    public static final String KShareError = "分享失败";

    private String mShareUrl; // 分享的Url
    private String mShareTitle; // 分享的标题
    private String mCoverUrl; // 分享的图片url

    //剪切板管理工具
    private ClipboardManager mClipboardManager;

    private OnDeleteListener mDeleteListener;
    private OnCopyDuplicateListener mCopyListener;

    private OnClickListener mOnShareClickListener;

    public ShareDialog(Context context, String shareTitle, int courseId) {
        super(context);
        mShareTitle = shareTitle;
        shareSignature(courseId);
    }

    public ShareDialog(Context context, int courseId, String shareTitle, String coverUrl) {
        super(context);
        shareSignature(courseId);
        mShareTitle = shareTitle;
        mCoverUrl = coverUrl;
    }

    @Override
    public void initData() {
        mClipboardManager = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
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
        setOnShareClickListener(R.id.dialog_share_iv_wechat);
        setOnShareClickListener(R.id.dialog_share_iv_moment);
        setOnShareClickListener(R.id.dialog_share_iv_linkedin);
        setOnShareClickListener(R.id.dialog_share_iv_qq);
        setOnShareClickListener(R.id.dialog_share_iv_linkedin);
        setOnShareClickListener(R.id.dialog_share_iv_sina);

        setOnClickListener(R.id.dialog_share_iv_message);
        setOnClickListener(R.id.dialog_share_iv_copy_link);
        setOnClickListener(R.id.dialog_share_tv_contribute);
        setOnClickListener(R.id.dialog_share_tv_copy_replica);
        setOnClickListener(R.id.dialog_share_tv_delete);
        setOnClickListener(R.id.dialog_share_tv_cancel);

        setGravity(Gravity.BOTTOM);

        mOnShareClickListener = v -> {
            ShareParams param = ShareParams.newBuilder()
                    .title(mShareTitle)
                    .text(ResLoader.getString(R.string.share_text))
                    .url(mShareUrl)
                    .imageUrl(mCoverUrl)
                    .build();

            Type type = null;
            switch (v.getId()) {
                case R.id.dialog_share_iv_wechat: {
                    type = Type.wechat;
                }
                break;
                case R.id.dialog_share_iv_moment: {
                    //微信朋友圈的分享text是不显示的，问了客服
                    type = Type.wechat_friend;
                }
                break;
                case R.id.dialog_share_iv_qq: {
                    type = Type.qq;
                    //这是qq必写的参数，否则发不了，短信只能发文字，链接可以写在文字里面
//                    shareParams.setTitleUrl(mShareUrl);
                }
                break;
                case R.id.dialog_share_iv_linkedin: {
                    type = Type.linkedin;
//                    shareParams.setUrl(mShareUrl);
                }
                break;
                case R.id.dialog_share_iv_sina: {
                    type = Type.sina;
                }
                break;
                case R.id.dialog_share_iv_message: {
                    if (PermissionChecker.allow(getContext(), Permission.sms, Permission.storage)) {
                        type = Type.sms;
//                        shareParams.setText(mShareUrl);
                    } else {
                        showToast(getString(R.string.user_message_permission));
                    }
                }
                break;
            }

            Platform.share(type, param, new OnShareListener() {

                @Override
                public void onShareSuccess() {
                    showToast(R.string.share_success);
                }

                @Override
                public void onShareError(String message) {
                    showToast(KShareError.concat(message));
                }

                @Override
                public void onShareCancel() {
                    showToast(R.string.share_cancel);
                }
            });
        };
    }

    private void setOnShareClickListener(@IdRes int id) {
        View v = findView(id);
        if (v != null) {
            v.setOnClickListener(mOnShareClickListener);
        }
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.dialog_share_iv_copy_link: {
                //创建一个新的文本clip对象
                ClipData clipData = ClipData.newPlainText("Simple test", mShareUrl);
                //把clip对象放在剪切板中
                mClipboardManager.setPrimaryClip(clipData);
                showToast(R.string.copy_success);
            }
            break;
            case R.id.dialog_share_tv_contribute: {
                startActivity(ContributePlatformActivity.class);
            }
            break;
            case R.id.dialog_share_tv_copy_replica: {
                if (mCopyListener != null) {
                    mCopyListener.copy();
                }
            }
            break;
            case R.id.dialog_share_tv_delete: {
                if (mDeleteListener != null) {
                    mDeleteListener.delete();
                }
            }
            break;
            case R.id.dialog_share_tv_cancel: {
                //不做处理
            }
            break;
        }
        dismiss();
    }

    public interface OnDeleteListener {

        void delete();

    }

    public interface OnCopyDuplicateListener {
        void copy();

    }

    public void shareSignature(int courseId) {
        // 拼接加密字符串
        String local; // 系统语言
        YSLog.d(TAG, "app language = " + SpApp.inst().getSystemLanguage());
        YSLog.d(TAG, "app country = " + SpApp.inst().getCountry());
        // 简体中文和繁体中文字符串资源要分别放到res/values-zh-rCN和res/values-zh-rTW下
        if ("zh".equals(SpApp.inst().getSystemLanguage())) {
            if ("CN".equals(SpApp.inst().getCountry())) {
                local = LanguageType.cn_simplified;
            } else {
                local = LanguageType.cn;
            }
        } else {
            local = LanguageType.en;
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
        try {
            mShareUrl = http + "meeting/share?signature=" + URLEncoder.encode(Util.encode(KDesKey, param), "utf-8");
            YSLog.d(TAG, "ShareUrl = " + mShareUrl);
        } catch (UnsupportedEncodingException e) {
            YSLog.d(TAG, "Share error = " + e.getMessage());
        }
    }

    public void setDeleteListener(OnDeleteListener listener) {
        mDeleteListener = listener;
    }

    public void setCopyListener(OnCopyDuplicateListener l) {
        mCopyListener = l;
    }

}
