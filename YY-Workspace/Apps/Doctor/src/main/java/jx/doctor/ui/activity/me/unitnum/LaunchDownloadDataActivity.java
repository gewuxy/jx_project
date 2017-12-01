package jx.doctor.ui.activity.me.unitnum;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.ys.YSLog;
import lib.ys.action.IntentAction;
import lib.ys.model.FileSuffix;
import lib.ys.ui.other.NavBar;
import lib.jx.ui.activity.base.BaseActivity;
import jx.doctor.R;
import jx.doctor.ui.activity.data.PDFActivityRouter;
import jx.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;
import jx.doctor.util.Util;


/**
 * 打开下载资料
 *
 * @author CaiXiang
 * @since 2017/5/17
 */
@Route
public class LaunchDownloadDataActivity extends BaseActivity {

    private ImageView mIv;
    private TextView mTvName;
    private TextView mTvSize;

    @Arg(opt = true)
    String mFilePath;
    @Arg(opt = true)
    String mFileName;
    @Arg(opt = true)
    String mFileSuffix;
    @Arg(opt = true)
    String mSize;
    @Arg(opt = true)
    String mDataFileId;
    @Arg(opt = true)
    String mFileNameEncryption;
    @Arg(opt = true)
    @DataType
    int mDataType;

    //private String mFileNameHashCode;

    @Override
    public void initData(Bundle state) {
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

        if (mFileSuffix.equals(FileSuffix.pdf)) {
            mIv.setImageResource(R.drawable.open_data_ic_pdf);
            PDFActivityRouter.create(
                    mFilePath,
                    mFileNameEncryption,
                    mFileName,
                    mDataFileId,
                    mDataType
            ).route(this);
            finish();
        } else if (mFileSuffix.equals(FileSuffix.ppt) || mFileSuffix.equals(FileSuffix.pptx)) {
            mIv.setImageResource(R.drawable.open_data_ic_ppt);
        } else {
            mIv.setImageResource(R.drawable.open_data_ic_word);
        }
        mTvName.setText(mFileName);
        mTvSize.setText(mSize);

        setOnClickListener(R.id.open_download_data_tv_btn);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        if (v.getId() == R.id.open_download_data_tv_btn) {

            try {
                if (mFileSuffix.equals(FileSuffix.ppt) || mFileSuffix.equals(FileSuffix.pptx)) {
                    IntentAction.ppt()
                            .filePath(mFilePath + mFileNameEncryption)
                            .alert("没有打开PPT类应用")
                            .launch();
                } else {
                    IntentAction.word()
                            .filePath(mFilePath + mFileNameEncryption)
                            .alert("没有打开Word类应用")
                            .launch();
                }
            } catch (Exception e) {
                YSLog.d(TAG, " error msg " + e.getMessage());
                showToast(R.string.can_not_find_relevant_software);
            }
        }
    }

}
