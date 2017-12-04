package jx.doctor.ui.activity.data;

import android.os.Bundle;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.ui.other.NavBar;
import lib.jx.network.Result;
import lib.jx.ui.activity.base.BaseSRGroupListActivity;
import jx.doctor.adapter.data.DataUnitDetailAdapter;
import jx.doctor.model.data.DataUnitDetail;
import jx.doctor.model.data.DataUnitDetail.TDataUnitDetail;
import jx.doctor.model.data.DataUnitDetails;
import jx.doctor.model.data.DataUnitDetails.TDataUnitDetails;
import jx.doctor.model.data.GroupDataUnitDetail;
import jx.doctor.network.JsonParser;
import jx.doctor.network.NetworkApiDescriptor.DataAPI;
import jx.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;
import jx.doctor.util.Util;

/**
 * 药品详情
 *
 * @author CaiXiang
 * @since 2017/7/14
 */
@Route
public class DataUnitDetailActivity extends BaseSRGroupListActivity<GroupDataUnitDetail, DataUnitDetail, DataUnitDetailAdapter> {

    @Arg
    String mDataFileId;

    @Arg
    String mFileName;

    @Arg(defaultInt = DataType.un_know)
    @DataType
    int mType;

    private DataUnitDetails mData;

    private ICollectionView mCollectionView;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, mFileName, this);
    }

    @Override
    public void setViews() {
        super.setViews();

        mCollectionView = new CollectionViewImpl(getNavBar(), mDataFileId, mType, this);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(ICollectionView.KIdDetail, DataAPI.collectionDetail(mDataFileId).build());
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        if (id == ICollectionView.KIdDetail) {
            return super.onNetworkResponse(id, resp);
        } else {
            return mCollectionView.onNetworkResponse(id, resp);
        }
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (id == ICollectionView.KIdDetail) {
            super.onNetworkSuccess(id, r);
        } else {
            mCollectionView.onNetworkSuccess(id, r);
        }
    }

    @Override
    public Result<GroupDataUnitDetail> parseNetworkResponse(int id, String text) throws JSONException {
        Result<DataUnitDetails> dataResult = JsonParser.ev(text, DataUnitDetails.class);
        Result<GroupDataUnitDetail> result = new Result<>();
        result.setCode(dataResult.getCode());

        if (dataResult.isSucceed()) {
            mData = dataResult.getData();

            List<DataUnitDetail> details = mData.getList(TDataUnitDetails.detailList);
            if (details != null) {
                for (DataUnitDetail detail : details) {
                    GroupDataUnitDetail group = new GroupDataUnitDetail();
                    group.setTag(detail.getString(TDataUnitDetail.detailKey));
                    List<DataUnitDetail> children = new ArrayList<>();
                    children.add(detail);
                    group.setChildren(children);
                    result.add(group);
                }
            }
        }

        return result;
    }

    @Override
    public void onNetRefreshSuccess() {
        super.onNetRefreshSuccess();

        mCollectionView.setData(mData);
    }
}
