package yy.doctor.activity.me;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.util.Util;
import yy.doctor.view.CircleProgressView;

/**
 * 资料下载页面
 *
 * @author CaiXiang
 * @since 2017/5/17
 */

public class DownloadDataActivity extends BaseActivity {

    private CircleProgressView mProgressBar;
    private TextView mTvNum;
    private TextView mTvTotal;
    private TextView mTvStatus;
    private ImageView mIvDownload;
    private boolean mIsDownload = false;

    private String mName;
    private String mUrl;
    private String mType;

    public static void nav(Context context, String name,String url, String type) {
        Intent i = new Intent(context, DownloadDataActivity.class)
                .putExtra(Extra.KName, name)
                .putExtra(Extra.KData, url)
                .putExtra(Extra.KType, type);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mName = getIntent().getStringExtra(Extra.KName);
        mUrl = getIntent().getStringExtra(Extra.KData);
        mType = getIntent().getStringExtra(Extra.KType);
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
                    startActivity(OpenDownloadDataActivity.class);
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
