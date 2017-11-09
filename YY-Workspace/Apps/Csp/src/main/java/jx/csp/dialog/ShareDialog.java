package jx.csp.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inject.annotation.network.Descriptor;
import jx.csp.BuildConfig;
import jx.csp.R;
import jx.csp.constant.AppType;
import jx.csp.constant.Constants;
import jx.csp.constant.LangType;
import jx.csp.constant.MetaValue;
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

    private final String KDesKey = "2b3e2d604fab436eb7171de397aee892"; // DES秘钥

    private String mShareUrl; // 分享的Url
    private String mShareTitle; // 分享的标题
    private String mCoverUrl; // 分享的图片url
//    private GridView mGridView;

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
        judge();
    }


    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_share;
    }

    @Override
    public void findViews() {
//        mGridView = findView(R.id.share_gridview);
    }

    @Override
    public void setViews() {

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
                }
                break;
                case R.id.dialog_share_iv_linkedin: {
                    type = Type.linkedin;
                }
                break;
                case R.id.dialog_share_iv_sina: {
                    type = Type.sina;
                }
                break;
                case R.id.dialog_share_iv_message: {
                    if (PermissionChecker.allow(getContext(), Permission.sms, Permission.storage)) {
                        type = Type.sms;
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
                    showToast(R.string.share_fail);
                }

                @Override
                public void onShareCancel() {
                    showToast(R.string.share_cancel);
                }
            });
        };
        setOnShareClickListener(R.id.dialog_share_iv_wechat);
        setOnShareClickListener(R.id.dialog_share_iv_moment);
        setOnShareClickListener(R.id.dialog_share_iv_linkedin);
        setOnShareClickListener(R.id.dialog_share_iv_qq);
        setOnShareClickListener(R.id.dialog_share_iv_sina);
        setOnShareClickListener(R.id.dialog_share_iv_message);
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
        LangType type = SpApp.inst().getLangType(); // 系统语言
        YSLog.d(TAG, "app app_type = " + type);
        // 简体中文和繁体中文字符串资源要分别放到res/values-zh-rCN和res/values-zh-rTW下
        @AppType int appType;  // 国内版 国外版
        if (Constants.KAppTypeCn.equals(PackageUtil.getMetaValue(MetaValue.app_type))) {
            appType = AppType.inland;
        } else {
            appType = AppType.overseas;
        }

        StringBuffer paramBuffer = new StringBuffer();
        paramBuffer.append("id=")
                .append(courseId)
                .append("&_local=")
                .append(type.define())
                .append("&abroad=")
                .append(appType);
        Descriptor des = NetworkApi.class.getAnnotation(Descriptor.class);
        String http = BuildConfig.TEST ? des.hostDebuggable() : des.host();
        try {
            mShareUrl = http + "meeting/share?signature=" + URLEncoder.encode(Util.encode(KDesKey, paramBuffer.toString()), Constants.KEncoding_utf8);
            YSLog.d(TAG, "ShareUrl = " + mShareUrl);
        } catch (UnsupportedEncodingException e) {
            YSLog.e(TAG, "shareSignature", e);
            // TODO: url= 官网??
        }
    }

    public void judge() {
        if (SpApp.inst().getLangType() != LangType.en) {
            List<Map<String, Object>> dataCn;
            SimpleAdapter adapter;
            GridView mGridView = findView(R.id.share_gridview);
            int[] icon = {R.drawable.share_ic_wechat,
                    R.drawable.share_ic_moment,
                    R.drawable.share_ic_qq,
                    R.drawable.share_ic_linkedin,
                    R.drawable.share_ic_weibo,
                    R.drawable.share_ic_message,
                    R.drawable.share_ic_copy};
            String[] platform = {getString(R.string.wechat),
                    getString(R.string.moment),
                    getString(R.string.QQ),
                    getString(R.string.linkedin),
                    getString(R.string.weibo),
                    getString(R.string.message),
                    getString(R.string.moment)};
            dataCn = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < icon.length; i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("image", icon[i]);
                map.put("text", platform);
                dataCn.add(map);
            }
            String[] from = {"image", "text"};
            int[] to = {R.id.dialog_share_iv_wechat, R.id.dialog_share_tv_wechat};
            adapter = new SimpleAdapter(getContext(), dataCn, R.layout.activty_dialog_share_pltatform_item, from, to);
            mGridView.setAdapter(adapter);
            mGridView.setOnItemClickListener((adapterView, view, i, l) -> {
                switch (i) {
                    case 0: {

                    }
                    break;
                }
            });
        } else {

        }
    }

    public void setDeleteListener(OnDeleteListener listener) {
        mDeleteListener = listener;
    }

    public void setCopyListener(OnCopyDuplicateListener l) {
        mCopyListener = l;
    }
}
