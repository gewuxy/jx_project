package jx.csp.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import inject.annotation.network.Descriptor;
import jx.csp.BuildConfig;
import jx.csp.R;
import jx.csp.adapter.share.SharePlatformAdapter;
import jx.csp.constant.AppType;
import jx.csp.constant.Constants;
import jx.csp.constant.LangType;
import jx.csp.constant.SharePlatform;
import jx.csp.constant.ShareType;
import jx.csp.model.main.Meet;
import jx.csp.model.main.Meet.TMeet;
import jx.csp.model.meeting.Course.CourseType;
import jx.csp.model.meeting.Live.LiveState;
import jx.csp.network.NetworkApi;
import jx.csp.sp.SpApp;
import jx.csp.ui.activity.CommonWebViewActivityRouter;
import jx.csp.ui.activity.edit.EditMeetActivityRouter;
import jx.csp.ui.activity.share.ContributePlatformActivityRouter;
import jx.csp.ui.activity.share.WatchPwdActivityRouter;
import jx.csp.util.UISetter;
import jx.csp.util.Util;
import lib.jx.dialog.BaseDialog;
import lib.platform.Platform;
import lib.platform.Platform.Type;
import lib.platform.listener.OnShareListener;
import lib.platform.model.ShareParams;
import lib.ys.YSLog;
import lib.ys.util.res.ResLoader;

import static android.content.Context.CLIPBOARD_SERVICE;
import static lib.ys.util.res.ResLoader.getString;

/**
 * @auther WangLan
 * @since 2017/10/12
 */
public class ShareDialog extends BaseDialog {

    private final String KDesKey = "2b3e2d604fab436eb7171de397aee892"; // DES秘钥

    private boolean mShareView; //新手指引进入为false，其他均为true
    private Meet mMeet;
    private View mLayout;
    private String mShareUrl; // 分享的Url
    private String mShareTitle; // 分享的标题要拼接
    private String mCourseId;  // 会议id
    private String mCoverUrl; // 分享的图片url

    private int mCourseType;  //播放类型, 根据类型改变第二个gridView的视图

    private int mLiveState;  //直播状态, 根据状态改变第一个gridView的视图
    //剪切板管理工具
    private ClipboardManager mClipboardManager;

    public ShareDialog(Context context, Meet meet) {
        this(context, meet, true);
    }

    public ShareDialog(Context context, Meet meet, boolean b) {
        super(context);

        mMeet = meet;
        mShareView = b;
        mCourseId = meet.getString(TMeet.id);
        mShareTitle = String.format(getString(R.string.share_title), meet.getString(TMeet.title));
        mCoverUrl = meet.getString(TMeet.coverUrl);
        mCourseType = meet.getInt(TMeet.playType);
        mLiveState = meet.getInt(TMeet.liveState);

        shareSignature();
        if (mShareView) {
            getPlatform();
            getPlatform2();
        } else {
            getPlatform();
            goneView(mLayout);
        }
    }

    @Override
    public void initData() {
        mClipboardManager = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
    }

    @Override
    public int getContentViewId() {
        return R.layout.dialog_share;
    }

    @Override
    public void findViews() {
        mLayout = findView(R.id.share_layout);
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.dialog_share_tv_cancel);
        setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onClick(View v) {
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

        StringBuilder paramBuffer = new StringBuilder();
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
        RecyclerView recyclerView = findView(R.id.share_rv);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);

        SharePlatformAdapter adapter = new SharePlatformAdapter();
        List<SharePlatform> sharePlatformList = new ArrayList<>();

        if (mShareView) {
            if (Util.checkAppCn()) {
                sharePlatformList.add(SharePlatform.wechat);
                sharePlatformList.add(SharePlatform.wechat_moment);
                sharePlatformList.add(SharePlatform.qq);
                sharePlatformList.add(SharePlatform.linkedin);
                sharePlatformList.add(SharePlatform.dingding);
                sharePlatformList.add(SharePlatform.sina);
                sharePlatformList.add(SharePlatform.sms);
            } else {
                sharePlatformList.add(SharePlatform.twitter);
                sharePlatformList.add(SharePlatform.whatsapp);
                sharePlatformList.add(SharePlatform.line);
                sharePlatformList.add(SharePlatform.linkedin);
                sharePlatformList.add(SharePlatform.facebook);
                sharePlatformList.add(SharePlatform.wechat);
                sharePlatformList.add(SharePlatform.sms);
            }

            if (mCourseType == CourseType.reb) {
                sharePlatformList.add(SharePlatform.contribute);
            } else {
                //直播开始不可投稿
                if (mLiveState == LiveState.un_start) {
                    sharePlatformList.add(SharePlatform.contribute);
                } else {
                    sharePlatformList.add(SharePlatform.unContribute);
                }
            }

        } else {
            if (Util.checkAppCn()) {
                sharePlatformList.add(SharePlatform.wechat);
                sharePlatformList.add(SharePlatform.wechat_moment);
                sharePlatformList.add(SharePlatform.qq);
                sharePlatformList.add(SharePlatform.linkedin);
                sharePlatformList.add(SharePlatform.dingding);
                sharePlatformList.add(SharePlatform.sina);
                sharePlatformList.add(SharePlatform.sms);
                sharePlatformList.add(SharePlatform.copy_link);
            } else {
                sharePlatformList.add(SharePlatform.twitter);
                sharePlatformList.add(SharePlatform.whatsapp);
                sharePlatformList.add(SharePlatform.line);
                sharePlatformList.add(SharePlatform.linkedin);
                sharePlatformList.add(SharePlatform.facebook);
                sharePlatformList.add(SharePlatform.wechat);
                sharePlatformList.add(SharePlatform.sms);
                sharePlatformList.add(SharePlatform.copy_link);
            }
        }

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

        adapter.setData(sharePlatformList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(position -> {
            int type = adapter.getItem(position).type();
            Type t = null;
            ShareParams param;
            if (type == ShareType.sms) {
                param = ShareParams.newBuilder()
                        .title(mShareTitle)
                        .text(ResLoader.getString(R.string.share_text))
                        .url(mShareUrl)
                        .build();
            } else {
                param = ShareParams.newBuilder()
                        .title(mShareTitle)
                        .text(ResLoader.getString(R.string.share_text))
                        .url(mShareUrl)
                        .imageUrl(mCoverUrl)
                        .build();
            }
            if (type == ShareType.contribute) {
                if (adapter.getItem(position).isClick()) {
                    ContributePlatformActivityRouter.create(mCourseId).route(getContext());
                }
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
                        /**
                         * @deprecated 暂无图片故不需要短信权限
                         */
                        t = Type.sms;
                    }
                    break;
                    case ShareType.dingding: {
                        t = Type.dingding;
                    }
                    break;
                    case ShareType.copy_link: {
                        //创建一个新的文本clip对象
                        ClipData clipData = ClipData.newPlainText("Simple test", mShareUrl);
                        //把clip对象放在剪切板中
                        mClipboardManager.setPrimaryClip(clipData);
                        showToast(R.string.copy_success);
                    }
                    break;
                }
                if (t != null) {
                    Platform.share(t, param, listener);
                }
            }
        });
    }

    private void getPlatform2() {
        RecyclerView recyclerView = findView(R.id.share_rv2);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);

        SharePlatformAdapter adapter = new SharePlatformAdapter();
        List<SharePlatform> list = new ArrayList<>();

        //录播有预览, 直播没有预览
        if (mCourseType == CourseType.reb) {
            list.add(SharePlatform.preview);
            list.add(SharePlatform.watch_pwd);
            list.add(SharePlatform.editor);
            list.add(SharePlatform.delete);
            list.add(SharePlatform.copy_link);
        } else {
            list.add(SharePlatform.watch_pwd);
            list.add(SharePlatform.delete);
            list.add(SharePlatform.copy_link);

        }

        adapter.setData(list);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(position -> {
            int type = adapter.getItem(position).type();
            switch (type) {
                case ShareType.preview: {
                    CommonWebViewActivityRouter.create(mShareUrl).route(getContext());
                }
                break;
                case ShareType.watch_pwd: {
                    WatchPwdActivityRouter.create(mCourseId).route(getContext(), 0);
                }
                break;
                case ShareType.copy_link: {
                    //创建一个新的文本clip对象
                    ClipData clipData = ClipData.newPlainText("Simple test", mShareUrl);
                    //把clip对象放在剪切板中
                    mClipboardManager.setPrimaryClip(clipData);
                    showToast(R.string.copy_success);
                }
                break;
                case ShareType.delete: {
                    UISetter.showDeleteMeet(mCourseId, getContext());
                    dismiss();
                }
                break;
                case ShareType.editor: {
                    EditMeetActivityRouter.create(mCourseId)
                            .previewUrl(mMeet.getString(TMeet.coverUrl))
                            .route(getContext());
                    dismiss();
                }
                break;
            }
        });
    }

}