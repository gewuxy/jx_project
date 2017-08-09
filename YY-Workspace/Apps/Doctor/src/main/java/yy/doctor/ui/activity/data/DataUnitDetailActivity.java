package yy.doctor.ui.activity.data;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IListResult;
import lib.processor.annotation.AutoIntent;
import lib.processor.annotation.Extra;
import lib.ys.ui.other.NavBar;
import lib.yy.network.ListResult;
import lib.yy.network.Result;
import lib.yy.ui.activity.base.BaseSRGroupListActivity;
import yy.doctor.adapter.data.DataUnitDetailAdapter;
import yy.doctor.model.data.DataUnitDetail;
import yy.doctor.model.data.DataUnitDetail.TDataUnitDetail;
import yy.doctor.model.data.DataUnitDetails;
import yy.doctor.model.data.DataUnitDetails.TDataUnitDetails;
import yy.doctor.model.data.GroupDataUnitDetail;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;
import yy.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;
import yy.doctor.util.Util;

/**
 * 药品详情
 *
 * @author CaiXiang
 * @since 2017/7/14
 */
@AutoIntent
public class DataUnitDetailActivity extends BaseSRGroupListActivity<GroupDataUnitDetail, DataUnitDetail, DataUnitDetailAdapter> {

    @Extra
    String mDataFileId;

    @Extra
    String mFileName;

    @Extra(defaultInt = DataType.un_know)
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
        exeNetworkReq(ICollectionView.KIdDetail, NetFactory.collectionDetail(mDataFileId));
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        if (id == ICollectionView.KIdDetail) {
            return super.onNetworkResponse(id, r);
        } else {
            return mCollectionView.onNetworkResponse(id, r);
        }
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        if (id == ICollectionView.KIdDetail) {
            super.onNetworkSuccess(id, result);
        } else {
            mCollectionView.onNetworkSuccess(id, result);
        }
    }

    @Override
    public IListResult<GroupDataUnitDetail> parseNetworkResponse(int id, String text) throws JSONException {
        Result<DataUnitDetails> dataResult = JsonParser.ev(text, DataUnitDetails.class);
        ListResult<GroupDataUnitDetail> result = new ListResult<>();
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
