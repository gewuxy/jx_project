package yy.doctor.ui.activity.data;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.yy.notify.DownloadNotifier;
import lib.yy.notify.DownloadNotifier.DownloadNotifyType;
import lib.yy.notify.DownloadNotifier.OnDownloadNotify;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.serv.DownloadServ.Download;
import yy.doctor.serv.DownloadServRouter;
import yy.doctor.ui.activity.me.unitnum.LaunchDownloadDataActivityRouter;
import yy.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;
import yy.doctor.util.UISetter;
import yy.doctor.util.Util;
import yy.doctor.view.CircleProgressView;

/**
 * 资料下载页面
 *
 * @author CaiXiang
 * @since 2017/5/17
 */
@Route
public class DownloadFileActivity extends BaseActivity implements OnDownloadNotify {

    private static final String KByteSymbol = "K";

    private CircleProgressView mProgressBar;
    private TextView mTvNum;
    private TextView mTvTotal;
    private TextView mTvStatus;
    private ImageView mIvDownload;
    private boolean mIsDownload = false;

    @Arg(opt = true)
    String mFileName;

    @Arg(opt = true)
    String mUrl;

    @Arg(opt = true)
    String mFileSuffix;

    @Arg(opt = true)
    long mFileSize;

    @Arg(opt = true)
    String mDataFileId;

    @Arg(opt = true)
    String mFileNameEncryption;

    @Arg(opt = true)
    String mFilePath;

    @Arg(opt = true)
    @DataType
    int mDataType;

    private String mFileSizeKB;

    private String mFileNameHashCode;

    @Override
    public void initData(Bundle state) {
        DownloadNotifier.inst().add(this);

        mFileSizeKB = String.valueOf(mFileSize / 1024) + KByteSymbol;
        mFileNameHashCode = String.valueOf((mUrl.hashCode() + mFileSuffix));

        //打乱文件名
        int shift = 5;
        StringBuffer sb = new StringBuffer();
        char[] chars = mFileNameHashCode.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = (char) (chars[i] + shift);
            sb.append(c);
        }
        mFileNameEncryption = sb.toString();

        //先判断文件是否已经存在  通过url的hashcode
        File f = new File(mFilePath, mFileNameEncryption);
        if (f.exists()) {
            LaunchDownloadDataActivityRouter.create()
                    .filePath(mFilePath)
                    .fileName(mFileName)
                    .fileNameEncryption(mFileNameEncryption)
                    .fileSuffix(mFileSuffix)
                    .dataType(mDataType)
                    .size(mFileSizeKB)
                    .dataFileId(mDataFileId)
                    .route(this);
            finish();
        }

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_download_data;
    }

    @Override
    public void initNavBar(NavBar bar) {
        UISetter.setNavBarMidText(bar, mFileName, this);
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
        mTvTotal.setText(mFileSizeKB);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int id = v.getId();
        switch (id) {
            case R.id.download_iv: {
                // 无网
                if (Util.noNetwork()) {
                    return;
                }
                if (TextUtil.isEmpty(mUrl)) {
                    showToast("文件不存在");
                    return;
                }
                if (mIsDownload) {
                    mIvDownload.setImageResource(R.drawable.download_ic_start);
                    mTvStatus.setText(R.string.download_pause);
                } else {
                    mIvDownload.setImageResource(R.drawable.download_ic_pause);
                    mTvStatus.setText(R.string.download_ing);
                    DownloadServRouter.create(mUrl, mFilePath, mFileSuffix).route(this);
                    //现在不提供断点下载 点击下载按钮后就不能点击暂停
                    mIvDownload.setClickable(false);
                }
                mIsDownload = !mIsDownload;
            }
            break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DownloadServRouter.stop(this);

        DownloadNotifier.inst().remove(this);
    }

    //通知
    @Override
    public void onDownloadNotify(@DownloadNotifyType int type, Object data) {
        if (type == DownloadNotifyType.progress) {
            Download download = (Download) data;
            int progress = (int) download.getProgress();
            mProgressBar.setProgress(progress);

            long totalSize = download.getTotalSize();
            mFileSize = totalSize / 1024;
            mTvTotal.setText(mFileSize + KByteSymbol);

            long downloadSize = (long) (mFileSize * ((float) progress / 100f));
            mTvNum.setText(downloadSize + KByteSymbol);
        } else if (type == DownloadNotifyType.complete) {
            LaunchDownloadDataActivityRouter.create()
                    .filePath(mFilePath)
                    .fileName(mFileName)
                    .fileNameEncryption(mFileNameEncryption)
                    .fileSuffix(mFileSuffix)
                    .dataType(mDataType)
                    .size(mTvTotal.getText().toString())
                    .dataFileId(mDataFileId)
                    .route(this);
            finish();
        }
    }

    protected void notify(@DownloadNotifyType int type, Object data) {
        DownloadNotifier.inst().notify(type, data);
    }

    protected void notify(@DownloadNotifyType int type) {
        DownloadNotifier.inst().notify(type);
    }

}
