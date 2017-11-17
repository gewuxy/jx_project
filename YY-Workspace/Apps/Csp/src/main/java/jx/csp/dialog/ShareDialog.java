package jx.csp.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import inject.annotation.network.Descriptor;
import jx.csp.BuildConfig;
import jx.csp.R;
import jx.csp.adapter.main.ShareAdapter;
import jx.csp.constant.AppType;
import jx.csp.constant.Constants;
import jx.csp.constant.LangType;
import jx.csp.constant.ShareType;
import jx.csp.model.main.Share;
import jx.csp.model.main.Share.TShare;
import jx.csp.network.NetworkApi;
import jx.csp.serv.CommonServ.ReqType;
import jx.csp.serv.CommonServRouter;
import jx.csp.sp.SpApp;
import jx.csp.ui.activity.me.ContributePlatformActivityRouter;
import jx.csp.util.Util;
import lib.platform.Platform;
import lib.platform.Platform.Type;
import lib.platform.listener.OnShareListener;
import lib.platform.model.ShareParams;
import lib.ys.YSLog;
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

    private final String KDesKey = "2b3e2d604fab436eb7171de397aee892"; // DES秘钥

    private String mShareUrl; // 分享的Url
    private String mShareTitle; // 分享的标题要拼接
    private String mCourseId;  // 会议id
    private String mTitle; // 会议标题
    private String mCoverUrl; // 分享的图片url

    //剪切板管理工具
    private ClipboardManager mClipboardManager;

    public ShareDialog(Context context, String courseId, String title, String coverUrl) {
        super(context);

        mCourseId = courseId;
        mTitle = title;
        mShareTitle = title;
        mCoverUrl = coverUrl;

        shareSignature();
    }

    @Override
    public void initData(Bundle state) {
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
    }

    @Override
    public void setViews() {

        setOnClickListener(R.id.dialog_share_tv_contribute);
        setOnClickListener(R.id.dialog_share_tv_copy_replica);
        setOnClickListener(R.id.dialog_share_tv_delete);
        setOnClickListener(R.id.dialog_share_tv_cancel);

        setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.dialog_share_tv_contribute: {
                ContributePlatformActivityRouter.create(mCourseId).route(getContext());
            }
            break;
            case R.id.dialog_share_tv_copy_replica: {
                CommonServRouter.create(ReqType.share_copy).courseId(mCourseId).title(mTitle).route(getContext());
            }
            break;
            case R.id.dialog_share_tv_delete: {
                CommonDialog dialog = new CommonDialog(getContext());
                View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_delete, null);
                dialog.addHintView(view);
                dialog.addBlueButton(R.string.confirm, v1 -> {
                    CommonServRouter.create(ReqType.share_delete_meet).courseId(mCourseId).route(getContext());
                });
                dialog.addGrayButton(R.string.cancel);
                dialog.show();
            }
            break;
            case R.id.dialog_share_tv_cancel: {
                // 不做处理
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
        ShareAdapter adapter = new ShareAdapter();
        int[] icons;
        String[] names;
        int[] types;
        if (Util.checkAppCn()) {
            icons = new int[]{
                    R.drawable.share_ic_wechat,
                    R.drawable.share_ic_moment,
                    R.drawable.share_ic_qq,
                    R.drawable.share_ic_linkedin,
                    R.drawable.share_ic_weibo,
                    R.drawable.share_ic_message,
                    R.drawable.share_ic_copy
            };

            names = new String[]{
                    getString(R.string.wechat),
                    getString(R.string.moment),
                    getString(R.string.QQ),
                    getString(R.string.linkedin),
                    getString(R.string.weibo),
                    getString(R.string.message),
                    getString(R.string.copy_link)
            };

            types = new int[]{
                    ShareType.wechat,
                    ShareType.wechat_friend,
                    ShareType.qq,
                    ShareType.linkedin,
                    ShareType.sina,
                    ShareType.sms,
                    ShareType.copy,
            };
        } else {
            icons = new int[]{
                    R.drawable.share_ic_facebook,
                    R.drawable.share_ic_twitter,
                    R.drawable.share_ic_whatsapp,
                    R.drawable.share_ic_line,
                    R.drawable.share_ic_linkedin,
                    R.drawable.share_ic_sms,
                    R.drawable.share_ic_copy
            };

            names = new String[]{
                    getString(R.string.facebook),
                    getString(R.string.twitter),
                    getString(R.string.whatsapp),
                    getString(R.string.Line),
                    getString(R.string.linkedin),
                    getString(R.string.SMS),
                    getString(R.string.copy_link)
            };

            types = new int[]{
                    ShareType.facebook,
                    ShareType.twitter,
                    ShareType.whatsapp,
                    ShareType.line,
                    ShareType.linkedin,
                    ShareType.sms,
                    ShareType.copy,
            };
        }
        List<Share> shareList = toShare(icons, names, types);
        adapter.setData(shareList);
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
            if (type == ShareType.copy) {
                //创建一个新的文本clip对象
                ClipData clipData = ClipData.newPlainText("Simple test", mShareUrl);
                //把clip对象放在剪切板中
                mClipboardManager.setPrimaryClip(clipData);
                showToast(R.string.copy_success);
            } else {
                Type t = Type.qq;
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
                }
                Platform.share(t, param, listener);
            }
        });
    }

    private List<Share> toShare(int[] icons, String[] names, int[] types) {
        List<Share> list = new ArrayList<>();
        if (icons == null || names == null || types == null) {
            return list;
        }
        int length = Math.min(Math.min(icons.length, names.length), types.length);
        for (int i = 0; i < length; i++) {
            Share share = new Share();
            share.put(TShare.icon, icons[i]);
            share.put(TShare.name, names[i]);
            share.put(TShare.type, types[i]);
            list.add(share);
        }
        return list;
    }
}

