package yy.doctor.dialog;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.yy.dialog.BaseDialog;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.serv.DownloadApkServ;

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

        int id = v.getId();
        switch (id) {
            case R.id.dialog_update_notice_tv_remind_me_later: {
                dismiss();
            }
            break;
            case R.id.dialog_update_notice_tv_download_now: {
                //启动服务下载apk
                Intent intent = new Intent(getContext(), DownloadApkServ.class);
                intent.putExtra(Extra.KData, mUrl);
                startService(intent);
                dismiss();
            }
            break;
        }
    }

}
