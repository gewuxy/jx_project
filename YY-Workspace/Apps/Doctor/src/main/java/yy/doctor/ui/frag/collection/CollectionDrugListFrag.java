package yy.doctor.ui.frag.collection;

import android.view.View;

import java.util.List;

import lib.ys.ui.other.NavBar;
import lib.yy.notify.Notifier.NotifyType;
import lib.yy.ui.frag.base.BaseSRListFrag;
import yy.doctor.adapter.CollectionDrugAdapter;
import yy.doctor.model.data.ThomsonDetail;
import yy.doctor.model.data.ThomsonDetail.TThomsonDetail;
import yy.doctor.network.NetFactory;
import yy.doctor.ui.activity.data.DrugDetailActivity;

/**
 * @auther WangLan
 * @since 2017/7/29
 */

public class CollectionDrugListFrag extends BaseSRListFrag<ThomsonDetail, CollectionDrugAdapter> {

    private int mType = 2; // type为2，表示药品目录

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
    public void onItemClick(View v, int position) {
        ThomsonDetail item = getItem(position);
        String dataFileId = item.getString(TThomsonDetail.id);
        String fileName= item.getString(TThomsonDetail.title);
        DrugDetailActivity.nav(getContext(),dataFileId,fileName);
    }

    @Override
    public void onNotify(@NotifyType int type, Object data) {

        //取消收藏后，收藏列表要删除对应的药品
        if (type == NotifyType.getCancel_collection_drug) {
            String drugId = (String) data;
            List<ThomsonDetail> list = getData();
            for (ThomsonDetail td : list) {
                if (drugId.equals(td.getString(TThomsonDetail.id))) {
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
