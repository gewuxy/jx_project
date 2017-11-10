package yy.doctor.ui.activity;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.network.model.NetworkResp;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.yy.network.Result;
import lib.yy.ui.activity.base.BaseWebViewActivity;
import yy.doctor.model.data.DataUnitDetails;
import yy.doctor.model.data.DataUnitDetails.TDataUnitDetails;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetworkApiDescriptor.DataAPI;
import yy.doctor.ui.activity.data.CollectionViewImpl;
import yy.doctor.ui.activity.data.ICollectionView;
import yy.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;
import yy.doctor.util.UISetter;

/**
 * WebView页面
 *
 * @author CaiXiang
 * @since 2017/6/16
 */
@Route
public class CommonWebViewActivity extends BaseWebViewActivity {

    @Arg()
    String mName;

    @Arg()
    String mUrl;

    @Arg(opt = true)
    @DataType
    int mType;

    @Arg(opt = true)
    String mFileId;

    private ICollectionView mCollectionView;
    private DataUnitDetails mData;

    @Override
    public void initData(Bundle savedInstanceState) {
    }

    @Override
    public void initNavBar(NavBar bar) {
        UISetter.setNavBarMidText(bar, mName, this);
    }

    @Override
    public void setViews() {
        super.setViews();

        if (mFileId != null) {
            mCollectionView = new CollectionViewImpl(getNavBar(), mFileId, mType, this);
            exeNetworkReq(ICollectionView.KIdDetail, DataAPI.collectionDetail(mFileId).build());
        }

    }

    @Override
    protected void onLoadStart() {
        loadUrl(mUrl);
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
