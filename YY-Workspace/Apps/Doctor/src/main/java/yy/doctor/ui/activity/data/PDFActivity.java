package yy.doctor.ui.activity.data;

import android.support.annotation.NonNull;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.res.ResLoader;
import lib.yy.network.Result;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.R;
import yy.doctor.model.data.DataUnitDetails;
import yy.doctor.model.data.DataUnitDetails.TDataUnitDetails;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;
import yy.doctor.util.Util;


/**
 * PDF
 *
 * @author CaiXiang
 * @since 2017/7/12
 */
@Route
public class PDFActivity extends BaseActivity {

    private PDFView mPDFView;

    @Arg
    String mFilePath;
    @Arg
    String mFileEncryptionName;
    @Arg
    String mFileName;
    @Arg
    String mDataFileId;

    @Arg(defaultInt = DataType.un_know)
    @DataType
    int mType;

    private DataUnitDetails mData;

    private ICollectionView mCollectionView;


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
    }

    @Override
    public void findViews() {
        mPDFView = findView(R.id.pdf_view);
    }

    @Override
    public void setViews() {
        mCollectionView = new CollectionViewImpl(getNavBar(), mDataFileId, mType, this);

        refresh(RefreshWay.dialog);
        exeNetworkReq(ICollectionView.KIdDetail, NetFactory.collectionDetail(mDataFileId));

        mPDFView.setBackgroundColor(ResLoader.getColor(R.color.app_bg));
        File file = new File(mFilePath, mFileEncryptionName);
        mPDFView.fromFile(file)
                .spacing(8)
                .load();
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == ICollectionView.KIdDetail) {
            return JsonParser.ev(r.getText(), DataUnitDetails.class);
        } else {
            return mCollectionView.onNetworkResponse(id, r);
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == ICollectionView.KIdDetail) {
            Result<DataUnitDetails> r = (Result) result;
            mData = r.getData();
            mCollectionView.setData(mData);

            stopRefresh();
            if (r.isSucceed()) {
                boolean state = mData.getBoolean(TDataUnitDetails.favorite);
                YSLog.d(TAG, "StoredState = " + state);
                mCollectionView.setState(state);
            }
        } else {
            mCollectionView.onNetworkSuccess(id, result);
        }
    }
}
