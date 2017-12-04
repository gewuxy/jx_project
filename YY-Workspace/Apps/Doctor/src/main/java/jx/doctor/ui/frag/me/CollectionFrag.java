package jx.doctor.ui.frag.me;

import android.os.Bundle;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.ys.ui.other.NavBar;
import lib.jx.notify.Notifier.NotifyType;
import lib.jx.ui.frag.base.BaseSRListFrag;
import jx.doctor.adapter.data.DataUnitAdapter;
import jx.doctor.model.data.DataUnit;
import jx.doctor.model.data.DataUnit.TDataUnit;
import jx.doctor.network.NetworkApiDescriptor.CollectionAPI;
import jx.doctor.ui.frag.data.BaseDataUnitsFrag.DataFrom;
import jx.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;
import jx.doctor.util.UISetter;

/**
 * @auther yuansui
 * @since 2017/8/9
 */
@Route
public class CollectionFrag extends BaseSRListFrag<DataUnit, DataUnitAdapter> {

    @Arg(defaultInt = DataType.un_know)
    int mType;

    @Override
    public void initData() {
        if (mType == DataType.un_know) {
            throw new IllegalStateException("收藏类型未知");
        }
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public void setViews() {
        super.setViews();

        setOnAdapterClickListener((position, v) -> UISetter.onDataUnitClick(getItem(position), mType, getContext()));
        getAdapter().setDataType(mType, DataFrom.collection);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(CollectionAPI.collection(getOffset(), getLimit(), mType).build());
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {

        boolean removeState = (type == NotifyType.collection_cancel_thomson && mType == DataType.thomson)
                || (type == NotifyType.collection_cancel_drug && mType == DataType.drug)
                || (type == NotifyType.collection_cancel_clinic && mType == DataType.clinic);

        //取消收藏后，收藏列表要删除对应的药品
        if (removeState) {
            String id = (String) data;
            for (DataUnit unit : getData()) {
                if (id.equals(unit.getString(TDataUnit.id))) {
                    getData().remove(unit);
                    invalidate();
                    break;
                }
            }
        }
    }

    protected String getEmptyText() {
        return "暂无收藏内容";
    }
}
