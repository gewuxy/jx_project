package yy.doctor.ui.activity.me;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.notify.DownloadNotifier;
import lib.yy.notify.DownloadNotifier.DownloadNotifyType;
import lib.yy.notify.DownloadNotifier.OnDownloadNotify;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.serv.DownloadServ;
import yy.doctor.serv.DownloadServ.Download;
import yy.doctor.ui.activity.me.unitnum.LaunchDownloadDataActivity;
import yy.doctor.util.Util;
import yy.doctor.view.CircleProgressView;

/**
 * 资料下载页面
 *
 * @author CaiXiang
 * @since 2017/5/17
 */

public class DownloadDataActivity extends BaseActivity implements OnDownloadNotify {

    private static final String KByteSymbol = "K";
    private static final String KDot = ".";

    private CircleProgressView mProgressBar;
    private TextView mTvNum;
    private TextView mTvTotal;
    private TextView mTvStatus;
    private ImageView mIvDownload;
    private boolean mIsDownload = false;

    private String mFileName;
    private String mUrl;
    private String mType;
    private long mFileSize;
    private String mFileSizeKB;
    private String mFileNameHashCode;
    private String mFileNameEncryption;
    private String mFilePath;
    private Intent mDownloadServ;

    public static void nav(Context context, String filePath, String name, String url, String type, long size) {
        Intent i = new Intent(context, DownloadDataActivity.class)
                .putExtra(Extra.KFilePath, filePath)
                .putExtra(Extra.KName, name)
                .putExtra(Extra.KData, url)
                .putExtra(Extra.KType, type)
                .putExtra(Extra.KNum, size);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        DownloadNotifier.inst().add(this);

        mFilePath = getIntent().getStringExtra(Extra.KFilePath);
        mFileName = getIntent().getStringExtra(Extra.KName);
        mUrl = getIntent().getStringExtra(Extra.KData);
        mType = getIntent().getStringExtra(Extra.KType);
        mFileSize = getIntent().getLongExtra(Extra.KNum, 0);

        mFileSizeKB = String.valueOf(mFileSize / 1024) + KByteSymbol;
        mFileNameHashCode = String.valueOf((mUrl.hashCode() + KDot + mType));

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
            LaunchDownloadDataActivity.nav(this, mFilePath, mFileNameEncryption, mType, mFileSizeKB, mFileName);
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
        Util.addBackIcon(bar, R.string.download_data, this);
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
                if (mIsDownload) {
                    mIvDownload.setImageResource(R.mipmap.download_ic_start);
                    mTvStatus.setText(R.string.download_pause);
                } else {
                    mIvDownload.setImageResource(R.mipmap.download_ic_pause);
                    mTvStatus.setText(R.string.download_ing);
                    mDownloadServ = new Intent(this, DownloadServ.class);
                    mDownloadServ.putExtra(Extra.KData, mUrl)
                            .putExtra(Extra.KFilePath, mFilePath)
                            .putExtra(Extra.KType, mType);
                    startService(mDownloadServ);
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

        if (mDownloadServ != null) {
            stopService(mDownloadServ);
        }
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

            LaunchDownloadDataActivity.nav(this, mFilePath, mFileNameEncryption, mType, mTvTotal.getText().toString(), mFileName);
            finish();

//            if (mType.equals(FileTypeConstants.KPdf)) {
//                // 下载完成先加密  加密完成跳转
//                Observable.fromCallable(() -> FileCipherUtil.encrypt(mFilePath + mFileNameEncryption, FileCipherUtil.KEncrypt))
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(aBoolean -> {
//                            if (aBoolean) {
//                                LaunchDownloadDataActivity.nav(this, mFilePath, mFileNameEncryption, mType, mTvTotal.getText().toString(), mFileName);
//                                finish();
//                            }
//                        });
//            } else {
//                //加载完成跳转
//                LaunchDownloadDataActivity.nav(this, mFilePath, mFileNameEncryption, mType, mTvTotal.getText().toString(), mFileName);
//                finish();
//            }
        }
    }

    protected void notify(@DownloadNotifyType int type, Object data) {
        DownloadNotifier.inst().notify(type, data);
    }

    protected void notify(@DownloadNotifyType int type) {
        DownloadNotifier.inst().notify(type);
    }

}
