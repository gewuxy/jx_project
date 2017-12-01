package jx.doctor.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import lib.jx.dialog.BaseDialog;
import jx.doctor.Extra;
import jx.doctor.R;
import jx.doctor.serv.DownloadApkServ;

/**
 * 更新提醒的dialog
 *
 * @author CaiXiang
 * @since 2017/4/14
 */
public class UpdateNoticeDialog extends BaseDialog {

    private String mUrl; // 下载新版本的Url

    public UpdateNoticeDialog(@NonNull Context context, String url) {
        super(context);

        mUrl = url;
    }

    @Override
    public void initData(Bundle state) {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_update_notice;
    }

    @Override
    public void findViews() {
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.dialog_update_notice_tv_remind_me_later); // 稍后提醒
        setOnClickListener(R.id.dialog_update_notice_tv_download_now); // 立即更新
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_update_notice_tv_download_now: {
                //启动服务下载apk
                Intent intent = new Intent(getContext(), DownloadApkServ.class);
                intent.putExtra(Extra.KData, mUrl);
                startService(intent);
            }
            break;
        }
        dismiss();
    }

}
