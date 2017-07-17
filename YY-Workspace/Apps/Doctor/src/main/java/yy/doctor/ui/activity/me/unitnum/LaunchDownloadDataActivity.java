package yy.doctor.ui.activity.me.unitnum;

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
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.ui.activity.me.PDFActivity;
import yy.doctor.util.Util;

import static yy.doctor.Constants.FileTypeConstants.KPdf;
import static yy.doctor.Constants.FileTypeConstants.KPpt;
import static yy.doctor.Constants.FileTypeConstants.KPptX;

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
    private String mFileNameHashCode;
    private String mFileNameEncryption;
    private String mType;
    private String mSize;

    public static void nav(Context context, String filePath, String fileNameEncryption, String type, String size, String fileName) {
        Intent i = new Intent(context, LaunchDownloadDataActivity.class)
                .putExtra(Extra.KFilePath, filePath)
                .putExtra(Extra.KName, fileNameEncryption)
                .putExtra(Extra.KType, type)
                .putExtra(Extra.KNum, size)
                .putExtra(Extra.KData, fileName);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mFilePath = getIntent().getStringExtra(Extra.KFilePath);
        mFileNameEncryption = getIntent().getStringExtra(Extra.KName);
        mType = getIntent().getStringExtra(Extra.KType);
        mSize = getIntent().getStringExtra(Extra.KNum);
        mFileName = getIntent().getStringExtra(Extra.KData);

        YSLog.d(TAG, "FileNameEncryption = " + mFileNameEncryption);

        // 恢复文件名
//        StringBuffer sb = new StringBuffer();
//        char[] chars = mFileNameEncryption.toCharArray();
//        for (int i = 0; i < chars.length; i++) {
//            char c = (char) (chars[i] - 5);
//            sb.append(c);
//        }
//        mFileNameHashCode = sb.toString();
//        YSLog.d(TAG, "FileNameHashCode = " + mFileNameHashCode);

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_launch_download_data;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.file_data, this);
    }

    @Override
    public void findViews() {

        mIv = findView(R.id.launch_download_data_ic);
        mTvName = findView(R.id.launch_download_data_tv_name);
        mTvSize = findView(R.id.launch_download_data_tv_size);
    }

    @Override
    public void setViews() {

        if (mType.equals(KPdf)) {
            mIv.setImageResource(R.mipmap.open_data_ic_pdf);
        } else if (mType.equals(KPpt) || mType.equals(KPptX)) {
            mIv.setImageResource(R.mipmap.open_data_ic_ppt);
        } else {
            mIv.setImageResource(R.mipmap.open_data_ic_word);
        }
        mTvName.setText(mFileName);
        mTvSize.setText(mSize);

        setOnClickListener(R.id.open_download_data_tv_btn);

//        if (mType.equals(FileTypeConstants.KPdf)) {
//            //先解密文件
//            Observable.fromCallable(() -> FileCipherUtil.decrypt(new File(mFilePath, mFileNameEncryption).getPath(), null))
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(aBoolean -> {
//                        if (aBoolean) {
//                            setOnClickListener(R.id.open_download_data_tv_btn);
//                        }
//                    });
//        } else {
//            setOnClickListener(R.id.open_download_data_tv_btn);
//        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        if (v.getId() == R.id.open_download_data_tv_btn) {

            Intent intent = null;
            try {
                if (mType.equals(KPdf)) {
                    PDFActivity.nav(this, mFilePath, mFileNameEncryption, mFileName);
                } else if (mType.equals(KPpt) || mType.equals(KPptX)) {
                    intent = getPptFileIntent(mFilePath + mFileNameEncryption);
                    startActivity(intent);
                } else {
                    intent = getWordFileIntent(mFilePath + mFileNameEncryption);
                    startActivity(intent);
                }
            } catch (Exception e) {
                YSLog.d(TAG, " error msg " + e.getMessage());
                showToast(R.string.can_not_find_relevant_software);
            }

            // FIXME: X5   caixiang
            //YSLog.d(TAG, " openFile path = " + mFilePath + mFileNameHashCode);
//            HashMap<String, String> map = new HashMap<>();
//            map.put("local", "false");
//            map.put("style", "1");
//            map.put("menuData", null);
//            QbSdk.openFileReader(this, mFilePath + mFileNameEncryption, map, new ValueCallback<String>() {
//
//                @Override
//                public void onReceiveValue(String s) {
//                    YSLog.d(TAG, " onReceiveValue = " + s);
//                }
//            });

        }
    }

    /**
     * 获取一个用于打开Word文件的intent
     *
     * @param param
     * @return
     */
    public Intent getWordFileIntent(String param) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
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
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
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
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    /**
     * 获取一个用于打开Excel文件的intent
     *
     * @param param
     * @return
     */
    public static Intent getExcelFileIntent(String param) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

}
