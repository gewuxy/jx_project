package jx.doctor.ui.activity;

import android.os.Bundle;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.YSLog;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.jx.ui.activity.base.BaseWebViewActivity;
import jx.doctor.model.data.DataUnitDetails;
import jx.doctor.model.data.DataUnitDetails.TDataUnitDetails;
import jx.doctor.network.JsonParser;
import jx.doctor.network.NetworkApiDescriptor.DataAPI;
import jx.doctor.ui.activity.data.CollectionViewImpl;
import jx.doctor.ui.activity.data.ICollectionView;
import jx.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;
import jx.doctor.util.UISetter;

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
    public void initData(Bundle state) {
    }

    @Override
    public void initNavBar(NavBar bar) {
        UISetter.setNavBarMidText(bar, mName, this);
    }

    @Override
    public void setViews() {
        super.setViews();

        if (TextUtil.isNotEmpty(mFileId)) {
            mCollectionView = new CollectionViewImpl(getNavBar(), mFileId, mType, this);
            exeNetworkReq(ICollectionView.KIdDetail, DataAPI.collectionDetail(mFileId).build());
        }

    }

    @Override
    protected void onLoadStart() {
        loadUrl(mUrl);
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        if (id == ICollectionView.KIdDetail) {
            return JsonParser.ev(resp.getText(), DataUnitDetails.class);
        } else {
            return mCollectionView.onNetworkResponse(id, resp);
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (id == ICollectionView.KIdDetail) {
            stopRefresh();
            if (r.isSucceed()) {
                mData = (DataUnitDetails) r.getData();
                mCollectionView.setData(mData);
                boolean state = mData.getBoolean(TDataUnitDetails.favorite);
                YSLog.d(TAG, "StoredState = " + state);
                mCollectionView.setState(state);
            }
        } else {
            mCollectionView.onNetworkSuccess(id, r);
        }
    }

}
