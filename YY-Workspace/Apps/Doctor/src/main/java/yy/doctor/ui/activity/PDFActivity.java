package yy.doctor.ui.activity;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

import lib.annotation.Extra;
import lib.annotation.AutoIntent;
import lib.ys.ui.other.NavBar;
import lib.ys.util.res.ResLoader;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * PDF
 *
 * @author CaiXiang
 * @since 2017/7/12
 */

@AutoIntent
public class PDFActivity extends BaseActivity {

    private PDFView mPDFView;

    @Extra
    String mFilePath;

    @Extra
    String mFileEncryptionName;

    @Extra
    String mFileName;

    private ImageView mIvCollection;
    private boolean mStoredState = false;  // 默认没有收藏


    @Override
    public void initData() {
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
            //exeNetworkReq(NetFactory.collectMeeting(mMeetId, mStoredState ? CollectType.collect : CollectType.cancel));
        });
        mIvCollection = Util.getBarView(group, ImageView.class);
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
