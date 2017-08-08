package yy.doctor.ui.frag.collection;

import java.util.List;

import lib.ys.ui.other.NavBar;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.frag.base.BaseSRListFrag;
import yy.doctor.adapter.data.DataUnitAdapter;
import yy.doctor.model.data.DataUnit;
import yy.doctor.model.data.DataUnit.TDataUnit;
import yy.doctor.network.NetFactory;

/**
 * @auther WangLan
 * @since 2017/7/29
 */

public class CollectionClinicalGuideFrag extends BaseSRListFrag<DataUnit, DataUnitAdapter> {

    private int mType = 3; // type为3，表示临床指南

    @Override
    public void initData() {

    }

    @Override
    public void initNavBar(NavBar bar) {

    }


    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.collection(getOffset(), getLimit(), mType));
    }

    @Override
    public int getLimit() {
        return 500;
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {

        //取消收藏后，收藏列表要删除对应的药品
        if (type == NotifyType.getCancel_collection_thomson) {
            String thomsonId = (String) data;
            List<DataUnit> list = getData();
            for (DataUnit td : list) {
                if (thomsonId.equals(td.getString(TDataUnit.id))) {
                    getData().remove(td);
                    invalidate();
                    return;
                }
            }
        }
    }

    @Override
    public boolean enableInitRefresh() {
        return true;
    }
}
