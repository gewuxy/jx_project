package yy.doctor.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * PDF
 *
 * @author CaiXiang
 * @since 2017/7/12
 */

public class PDFActivity extends BaseActivity {

    private PDFView mPDFView;
    private String mFilePath;
    private String mFileEncryptionName;
    private String mFileName;
    private String mDataFileId;
    private String mNetType = 1+"";

    private ImageView mIvCollection;
    private boolean mStoredState = true;  // 默认没有收藏

    public static void nav(Context context, String filePath, String fileEncryptionName, String name,String id) {
        Intent i = new Intent(context, PDFActivity.class)
                .putExtra(Extra.KFilePath, filePath)
                .putExtra(Extra.KData, fileEncryptionName)
                .putExtra(Extra.KName, name)
                .putExtra(Extra.KId,id);

        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mFilePath = getIntent().getStringExtra(Extra.KFilePath);
        mFileEncryptionName = getIntent().getStringExtra(Extra.KData);
        mFileName = getIntent().getStringExtra(Extra.KName);
        mDataFileId = getIntent().getStringExtra(Extra.KId);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_pdf;
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, mFileName, this);
        // 收藏
        ViewGroup group = bar.addViewRight(R.drawable.collection_selector, v -> {
            mStoredState = !mStoredState;
            mIvCollection.setSelected(mStoredState);
            showToast(mStoredState ? R.string.collect_finish : R.string.cancel_collect);
            exeNetworkReq(NetFactory.collectionStatus(mDataFileId,mNetType));
            if (!mStoredState) {
                notify(NotifyType.getCancel_collection_thomson,mDataFileId);
            }
        });
        mIvCollection = Util.getBarView(group, ImageView.class);
        mIvCollection.setSelected(true);
    }

    @Override
    public void findViews() {
        mPDFView = findView(R.id.pdf_view);
    }

    @Override
    public void setViews() {
        mPDFView.setBackgroundColor(ResLoader.getColor(R.color.app_bg));
        File file = new File(mFilePath, mFileEncryptionName);
        mPDFView.fromFile(file)
                .spacing(8)
                .load();
    }

}
