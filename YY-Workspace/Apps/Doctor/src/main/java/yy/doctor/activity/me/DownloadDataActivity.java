package yy.doctor.activity.me;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.DownloadNotifier;
import lib.yy.DownloadNotifier.DownloadNotifyType;
import lib.yy.DownloadNotifier.OnDownloadNotify;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.serv.DownloadServ;
import yy.doctor.util.Util;
import yy.doctor.view.CircleProgressView;

/**
 * 资料下载页面
 *
 * @author CaiXiang
 * @since 2017/5/17
 */

public class DownloadDataActivity extends BaseActivity implements OnDownloadNotify {

    private CircleProgressView mProgressBar;
    private TextView mTvNum;
    private TextView mTvTotal;
    private TextView mTvStatus;
    private ImageView mIvDownload;
    private boolean mIsDownload = false;

    private String mFileName = "测试文件.pdf";
    private String mUrl = "http://www.medcn.com/upload/temp/service_certificate.pdf";
    private String mType = "pdf";
    private long mFileSize;

    public static void nav(Context context, String name, String url, String type) {
        Intent i = new Intent(context, DownloadDataActivity.class)
                .putExtra(Extra.KName, name)
                .putExtra(Extra.KData, url)
                .putExtra(Extra.KType, type);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        DownloadNotifier.inst().add(this);
        /*mFileName = getIntent().getStringExtra(Extra.KName);
        mUrl = getIntent().getStringExtra(Extra.KData);
        mType = getIntent().getStringExtra(Extra.KType);*/
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_download_data;
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, "资料下载", this);
    }

    @Override
    public void findViews() {

        mProgressBar = findView(R.id.download_progress_bar);
        mTvNum = findView(R.id.download_tv_num);
        mTvTotal = findView(R.id.dowmload_tv_total);
        mTvStatus = findView(R.id.download_tv_status);
        mIvDownload = findView(R.id.download_iv);
    }

    @Override
    public void setViews() {

        mIvDownload.setOnClickListener(this);
        mProgressBar.setProgress(0);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int id = v.getId();
        switch (id) {
            case R.id.download_iv: {
                if (mIsDownload) {
                    mIvDownload.setImageResource(R.mipmap.download_ic_start);
                    mTvStatus.setText("暂停下载");
                } else {
                    mIvDownload.setImageResource(R.mipmap.download_ic_pause);
                    mTvStatus.setText("正在下载...");
                    Intent intent = new Intent(this, DownloadServ.class);
                    intent.putExtra(Extra.KData, mUrl)
                            .putExtra(Extra.KName, mFileName);
                    startService(intent);
                }
                mIsDownload = !mIsDownload;
            }
            break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadNotifier.inst().remove(this);
    }

    //通知
    @Override
    public void onDownloadNotify(@DownloadNotifyType int type, Object data) {
        if (type == DownloadNotifyType.progress) {
            float pro = (float) data;
            int progress = (int) pro;
            mProgressBar.setProgress(progress);
            long downloadSize = (long) (mFileSize * ((float) progress / 100.0));
            mTvNum.setText(downloadSize + "KB");
            //下载完成跳转
            if (progress == 100) {
                OpenDownloadDataActivity.nav(this, mFileName, "pdf", "90k");
            }
        } else if (type == DownloadNotifyType.totalSize) {
            long totalSize = (long) data;
            mFileSize = totalSize / 1024;
            mTvTotal.setText(mFileSize + "KB");
            mTvNum.setText("0KB");
        }
    }

    protected void notify(@DownloadNotifyType int type, Object data) {
        DownloadNotifier.inst().notify(type, data);
    }

    protected void notify(@DownloadNotifyType int type) {
        DownloadNotifier.inst().notify(type);
    }

}
