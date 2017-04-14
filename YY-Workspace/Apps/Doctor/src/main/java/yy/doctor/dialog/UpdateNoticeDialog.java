package yy.doctor.dialog;

import android.content.Context;
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

    public UpdateNoticeDialog(@NonNull Context context) {
        super(context);
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

        mTvRemindMeLater=findView(R.id.dialog_update_notice_tv_remind_me_later);
        mTvDownloadNow=findView(R.id.dialog_update_notice_tv_download_now);

    }

    @Override
    public void setViewsValue() {

        mTvRemindMeLater.setOnClickListener(this);
        mTvDownloadNow.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        
        int id=v.getId();
        switch (id){
            case R.id.dialog_update_notice_tv_remind_me_later: {
                showToast("稍后提醒");
                dismiss();
            }
            break;
            case R.id.dialog_update_notice_tv_download_now: {
                showToast("现在下载");
                dismiss();
            }
            break;
        }
        
    }
}
