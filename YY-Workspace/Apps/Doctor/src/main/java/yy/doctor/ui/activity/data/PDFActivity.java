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
import yy.doctor.network.NetworkApiDescriptor.DataAPI;
import yy.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;
import yy.doctor.util.UISetter;


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
    public void initData(Bundle savedInstanceState) {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_pdf;
    }

    @Override
    public void initNavBar(NavBar bar) {
        UISetter.setNavBarMidText(bar, mFileName, this);
    }

    @Override
    public void findViews() {
        mPDFView = findView(R.id.pdf_view);
    }

    @Override
    public void setViews() {
        mCollectionView = new CollectionViewImpl(getNavBar(), mDataFileId, mType, this);

        refresh(RefreshWay.dialog);

        exeNetworkReq(ICollectionView.KIdDetail, DataAPI.collectionDetail(mDataFileId).build());

        mPDFView.setBackgroundColor(ResLoader.getColor(R.color.app_bg));
        File file = new File(mFilePath, mFileEncryptionName);
        mPDFView.fromFile(file)
                .spacing(8)
                .load();
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp resp) throws Exception {
        if (id == ICollectionView.KIdDetail) {
            return JsonParser.ev(resp.getText(), DataUnitDetails.class);
        } else {
            return mCollectionView.onNetworkResponse(id, resp);
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == ICollectionView.KIdDetail) {
            Result<DataUnitDetails> r = (Result) result;

            stopRefresh();
            if (r.isSucceed()) {
                mData = r.getData();
                mCollectionView.setData(mData);
                boolean state = mData.getBoolean(TDataUnitDetails.favorite);
                YSLog.d(TAG, "StoredState = " + state);
                mCollectionView.setState(state);
            }
        } else {
            mCollectionView.onNetworkSuccess(id, result);
        }
    }
}
