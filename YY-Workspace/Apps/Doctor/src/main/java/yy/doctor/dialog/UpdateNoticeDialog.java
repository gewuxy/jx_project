package yy.doctor.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.yy.dialog.BaseDialog;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/14
 */
public class UpdateNoticeDialog extends BaseDialog {

    private TextView mTvRemindMeLater;
    private TextView mTvDownloadNow;

    private String mUrl;

    public UpdateNoticeDialog(@NonNull Context context) {
        super(context);
    }

    public UpdateNoticeDialog(@NonNull Context context, String url) {
        super(context);

        mUrl = url;
    }

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_update_notice;
    }

    @Override
    public void findViews() {

        mTvRemindMeLater = findView(R.id.dialog_update_notice_tv_remind_me_later);
        mTvDownloadNow = findView(R.id.dialog_update_notice_tv_download_now);
    }

    @Override
    public void setViews() {

        mTvRemindMeLater.setOnClickListener(this);
        mTvDownloadNow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int id = v.getId();
        switch (id) {
            case R.id.dialog_update_notice_tv_remind_me_later: {
                showToast("稍后提醒");
                dismiss();
            }
            break;
            case R.id.dialog_update_notice_tv_download_now: {
                showToast("现在下载");
                //启动服务下载apk
                try {
                    String str = "market://details?id=" + getContext().getPackageName();
                    Intent localIntent = new Intent(Intent.ACTION_VIEW);
                    localIntent.setData(Uri.parse(str));
                    startActivity(localIntent);
                } catch (Exception e) {
                    // 打开应用商店失败 可能是没有手机没有安装应用市场
                    e.printStackTrace();
                    showToast("打开应用商店失败");
                    // 调用系统浏览器进入商城
                    String url = "";
                    openLinkBySystem(url);
                }
                dismiss();
            }
            break;
        }

    }

    /**
     * 调用系统浏览器打开网页
     *
     * @param url 地址
     */
    private void openLinkBySystem(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

}
