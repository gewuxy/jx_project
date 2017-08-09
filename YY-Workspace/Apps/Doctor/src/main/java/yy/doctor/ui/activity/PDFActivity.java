package yy.doctor.ui.activity;

import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

import lib.network.model.NetworkResp;
import lib.processor.annotation.AutoIntent;
import lib.processor.annotation.Extra;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.res.ResLoader;
import lib.yy.network.Result;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.model.data.DataDetail;
import yy.doctor.model.data.DataDetail.TDrugDetailData;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * PDF
 *
 * @author CaiXiang
 * @since 2017/7/12
 */
@AutoIntent
public class PDFActivity extends BaseActivity {

    private final String KType = "1";
    private final int KCollectionState = 0;
    private final int KCollectionDetail = 1;

    @Extra
    String mFilePath;
    @Extra
    String mFileEncryptionName;
    @Extra
    String mFileName;
    @Extra
    String mDataFileId;

    private PDFView mPDFView;
    private ImageView mIvCollection;
    private boolean mStoredState;

    private DataDetail mData;

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
            mData.put(TDrugDetailData.favorite,mStoredState);
            exeNetworkReq(KCollectionState,NetFactory.collectionStatus(mDataFileId, KType));
            mIvCollection.setSelected(mStoredState);
            showToast(mStoredState ? R.string.collect_finish : R.string.cancel_collect);
            if (!mStoredState) {
                notify(NotifyType.getCancel_collection_thomson, mDataFileId);
            }
        });
        mIvCollection = Util.getBarView(group, ImageView.class);
    }

    @Override
    public void findViews() {
        mPDFView = findView(R.id.pdf_view);
    }

    @Override
    public void setViews() {
        refresh(RefreshWay.dialog);
        exeNetworkReq(KCollectionDetail, NetFactory.collectionDetail(mDataFileId));

        mPDFView.setBackgroundColor(ResLoader.getColor(R.color.app_bg));
        File file = new File(mFilePath, mFileEncryptionName);
        mPDFView.fromFile(file)
                .spacing(8)
                .load();
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == KCollectionDetail) {
            return JsonParser.ev(r.getText(), DataDetail.class);
        } else {
            return super.onNetworkResponse(id, r);
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == KCollectionDetail) {
            Result<DataDetail> r = (Result) result;
            mData = r.getData();
            if (r.isSucceed()) {
                stopRefresh();
                mStoredState = mData.getBoolean(TDrugDetailData.favorite);
                YSLog.d(TAG, "StoredState = " + mStoredState);
                mIvCollection.setSelected(mStoredState);
            }
        }
    }

}
