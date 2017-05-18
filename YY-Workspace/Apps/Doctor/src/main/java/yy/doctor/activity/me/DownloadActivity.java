package yy.doctor.activity.me;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.util.Util;
import yy.doctor.view.CircleProgressView;

/**
 * Created by XPS on 2017/5/16.
 */

public class DownloadActivity extends BaseActivity {

    private CircleProgressView mProgressBar;
    private TextView mTvNum;
    private TextView mTvTotal;
    private TextView mTvStatus;
    private ImageView mIvDownload;
    private boolean mIsDownload = false;

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_download;
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

        mProgressBar.setProgress(25);
        mIvDownload.setOnClickListener(this);

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
                    startActivity(OpenDataActivity.class);
                } else {
                    mIvDownload.setImageResource(R.mipmap.download_ic_pause);
                    mTvStatus.setText("正在下载...");
                }
                mIsDownload = !mIsDownload;
            }
            break;
        }

    }
}
