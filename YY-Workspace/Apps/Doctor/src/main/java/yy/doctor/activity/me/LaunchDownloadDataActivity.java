package yy.doctor.activity.me;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * 打开下载资料
 *
 * @author CaiXiang
 * @since 2017/5/17
 */

public class LaunchDownloadDataActivity extends BaseActivity {

    private ImageView mIv;
    private TextView mTvName;
    private TextView mTvSize;

    private String mFilePath;
    private String mFileName;
    private String mHashCodeName;
    private String mType;
    private String mSize;

    public static void nav(Context context, String filePath, String hashCodeName, String type, String num, String fileName) {
        Intent i = new Intent(context, LaunchDownloadDataActivity.class)
                .putExtra(Extra.KFilePath, filePath)
                .putExtra(Extra.KName, hashCodeName)
                .putExtra(Extra.KType, type)
                .putExtra(Extra.KNum, num)
                .putExtra(Extra.KData, fileName);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mFilePath = getIntent().getStringExtra(Extra.KFilePath);
        mHashCodeName = getIntent().getStringExtra(Extra.KName);
        mType = getIntent().getStringExtra(Extra.KType);
        mSize = getIntent().getStringExtra(Extra.KNum);
        mFileName = getIntent().getStringExtra(Extra.KData);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_launch_download_data;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "资料", this);
    }

    @Override
    public void findViews() {

        mIv = findView(R.id.launch_download_data_ic);
        mTvName = findView(R.id.launch_download_data_tv_name);
        mTvSize = findView(R.id.launch_download_data_tv_size);
    }

    @Override
    public void setViews() {

        if (mType.equals("pdf")) {
            mIv.setImageResource(R.mipmap.open_data_ic_pdf);
        } else if (mType.equals("ppt")) {
            mIv.setImageResource(R.mipmap.open_data_ic_ppt);
        } else {
            mIv.setImageResource(R.mipmap.open_data_ic_word);
        }
        mTvName.setText(mFileName);
        mTvSize.setText(mSize);
        setOnClickListener(R.id.open_download_data_tv_btn);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        if (v.getId() == R.id.open_download_data_tv_btn) {
            Intent intent = null;
            try {
                if (mType.equals("pdf")) {
                    intent = getPdfFileIntent(mFilePath + mHashCodeName);
                } else if (mType.equals("ppt")) {
                    intent = getPptFileIntent(mFilePath + mHashCodeName);
                } else {
                    intent = getWordFileIntent(mFilePath + mHashCodeName);
                }
                startActivity(intent);
            } catch (Exception e) {
                YSLog.d(TAG, " error msg " + e.getMessage());
                showToast("没有安装相应的软件");
            }
        }
    }

    /**
     * 获取一个用于打开Word文件的intent
     *
     * @param param
     * @return
     */
    public Intent getWordFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    /**
     * 获取一个用于打开PDF文件的intent
     *
     * @param param
     * @return
     */
    public Intent getPdfFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    /**
     * 获取一个用于打开PPT文件的intent
     *
     * @param param
     * @return
     */
    public static Intent getPptFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    /**
     * 获取一个用于打开Excel文件的intent
     * @param param
     * @return
     */
    public static Intent getExcelFileIntent( String param ){

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param ));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

}
