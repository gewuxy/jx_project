package jx.csp.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.GridView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import inject.annotation.network.Descriptor;
import jx.csp.BuildConfig;
import jx.csp.R;
import jx.csp.adapter.main.SharePlatformAdapter;
import jx.csp.constant.AppType;
import jx.csp.constant.Constants;
import jx.csp.constant.LangType;
import jx.csp.constant.SharePlatform;
import jx.csp.constant.ShareType;
import jx.csp.network.NetworkApi;
import jx.csp.sp.SpApp;
import jx.csp.ui.activity.WatchPwdActivityRouter;
import jx.csp.ui.activity.me.ContributePlatformActivityRouter;
import jx.csp.util.Util;
import lib.jx.dialog.BaseDialog;
import lib.platform.Platform;
import lib.platform.Platform.Type;
import lib.platform.listener.OnShareListener;
import lib.platform.model.ShareParams;
import lib.ys.YSLog;
import lib.ys.util.permission.Permission;
import lib.ys.util.permission.PermissionChecker;
import lib.ys.util.res.ResLoader;

import static android.content.Context.CLIPBOARD_SERVICE;
import static lib.ys.util.res.ResLoader.getString;

/**
 * @auther WangLan
 * @since 2017/10/12
 */

public class ShareDialog extends BaseDialog {

    private final String KDesKey = "2b3e2d604fab436eb7171de397aee892"; // DES秘钥

    private String mShareUrl; // 分享的Url
    private String mShareTitle; // 分享的标题要拼接
    private String mCourseId;  // 会议id
    private String mTitle; // 会议标题
    private String mCoverUrl; // 分享的图片url
//    private Bitmap mCoverBmp;

    //剪切板管理工具
    private ClipboardManager mClipboardManager;

    public ShareDialog(Context context, String courseId, String title, String coverUrl) {
        super(context);

        mCourseId = courseId;
        // 复制的会议标题默认加  _复制
        mTitle = title + getString(R.string.duplicate);
        mShareTitle = String.format(title);
        mCoverUrl = coverUrl;
//        mCoverBmp = Util.getBitMBitmap(mCoverUrl);

        shareSignature();
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
        getPlatform();
        getPlatform2();
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.dialog_share_tv_cancel);

        setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_share_tv_cancel: {

            }
            break;
        }
        dismiss();
    }

    /**
     * 分享的url加密处理
     */
    private void shareSignature() {
        // 拼接加密字符串
        LangType type = SpApp.inst().getLangType(); // 系统语言
        YSLog.d(TAG, "app app_type = " + type);
        // 简体中文和繁体中文字符串资源要分别放到res/values-zh-rCN和res/values-zh-rTW下
        @AppType int appType;  // 国内版 国外版
        if (Util.checkAppCn()) {
            appType = AppType.inland;
        } else {
            appType = AppType.overseas;
        }

        StringBuffer paramBuffer = new StringBuffer();
        paramBuffer.append("id=")
                .append(mCourseId)
                .append("&_local=")
                .append(type.define())
                .append("&abroad=")
                .append(appType);
        Descriptor des = NetworkApi.class.getAnnotation(Descriptor.class);
        String http = BuildConfig.DEBUG_NETWORK ? des.hostDebuggable() : des.host();
        try {
            mShareUrl = http + "meeting/share?signature=" + URLEncoder.encode(Util.encode(KDesKey, paramBuffer.toString()), Constants.KEncoding_utf8);
            YSLog.d(TAG, "ShareUrl = " + mShareUrl);
        } catch (UnsupportedEncodingException e) {
            YSLog.e(TAG, "shareSignature", e);
            mShareUrl = http + "meeting/share?signature=";
        }
    }

    /**
     * 根据海内外不同平台动态添加
     */
    private void getPlatform() {
        GridView gridView = findView(R.id.share_gridview);
        SharePlatformAdapter adapter = new SharePlatformAdapter();
        List<SharePlatform> sharePlatformList = new ArrayList<>();
        if (Util.checkAppCn()) {
            sharePlatformList.add(SharePlatform.wechat);
            sharePlatformList.add(SharePlatform.wechat_moment);
            sharePlatformList.add(SharePlatform.qq);
            sharePlatformList.add(SharePlatform.linkedin);
            sharePlatformList.add(SharePlatform.sina);
            sharePlatformList.add(SharePlatform.sms);
            sharePlatformList.add(SharePlatform.dingding);
            sharePlatformList.add(SharePlatform.contribute);
        } else {
            sharePlatformList.add(SharePlatform.wechat);
            sharePlatformList.add(SharePlatform.overseas_facebook);
            sharePlatformList.add(SharePlatform.overseas_twitter);
            sharePlatformList.add(SharePlatform.overseas_whatsapp);
            sharePlatformList.add(SharePlatform.overseas_line);
            sharePlatformList.add(SharePlatform.overseas_linkedin);
            sharePlatformList.add(SharePlatform.overseas_sms);
            sharePlatformList.add(SharePlatform.overseas_contribute);
        }

        adapter.setData(sharePlatformList);
        gridView.setAdapter(adapter);

        OnShareListener listener = new OnShareListener() {

            @Override
            public void onShareSuccess() {
                showToast(R.string.share_success);
            }

            @Override
            public void onShareError(String message) {
                showToast(R.string.share_fail);
            }

            @Override
            public void onShareCancel() {
                showToast(R.string.share_cancel);
            }
        };

        gridView.setOnItemClickListener((adapterView, view, position, l) -> {
            ShareParams param = ShareParams.newBuilder()
                    .title(mShareTitle)
                    .text(ResLoader.getString(R.string.share_text))
                    .url(mShareUrl)
                    .imageUrl(mCoverUrl)
                    .build();
            int type = adapter.getItemViewType(position);
            Type t = null;
            if (type == ShareType.contribute) {
                ContributePlatformActivityRouter.create(mCourseId).route(getContext());
            } else {
                switch (type) {
                    case ShareType.wechat: {
                        t = Type.wechat;
                    }
                    break;
                    case ShareType.wechat_friend: {
                        t = Type.wechat_friend;
                    }
                    break;
                    case ShareType.qq: {
                        t = Type.qq;
                    }
                    break;
                    case ShareType.linkedin: {
                        t = Type.linkedin;
                    }
                    break;
                    case ShareType.sina: {
                        t = Type.sina;
                    }
                    break;
                    case ShareType.facebook: {
                        t = Type.facebook;
                    }
                    break;
                    case ShareType.twitter: {
                        t = Type.twitter;
                    }
                    break;
                    case ShareType.whatsapp: {
                        t = Type.whatsapp;
                    }
                    break;
                    case ShareType.line: {
                        t = Type.line;
                    }
                    break;
                    case ShareType.sms: {
                        if (PermissionChecker.allow(getContext(), Permission.sms, Permission.storage)) {
                            t = Type.sms;
                        } else {
                            showToast(getString(R.string.user_message_permission));
                            return;
                        }
                    }
                    break;
                    case ShareType.dingding: {
                        t = Type.dingding;
                    }
                    break;
                }
                Platform.share(t, param, listener);
            }
        });
    }

    private void getPlatform2() {
        GridView gridView = findView(R.id.share_gridview2);
        SharePlatformAdapter shareAdapter = new SharePlatformAdapter();
        List<SharePlatform> list = new ArrayList<>();

        list.add(SharePlatform.preview);
        list.add(SharePlatform.pwd);
        list.add(SharePlatform.copy);
        list.add(SharePlatform.delete);

        shareAdapter.setData(list);
        gridView.setAdapter(shareAdapter);

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            int type = shareAdapter.getItemViewType(position);
            switch (type) {
                case ShareType.preview: {

                }
                break;
                case ShareType.wathc_pwd: {
                    WatchPwdActivityRouter.create(Integer.valueOf(mCourseId)).route(getContext());
                }
                break;
                case ShareType.copy: {
                    //创建一个新的文本clip对象
                    ClipData clipData = ClipData.newPlainText("Simple test", mShareUrl);
                    //把clip对象放在剪切板中
                    mClipboardManager.setPrimaryClip(clipData);
                    showToast(R.string.copy_success);
                }
                break;
                case ShareType.delete: {
                    Util.deleteMeet(mCourseId, getContext());
                }
                break;
            }
        });
    }


}