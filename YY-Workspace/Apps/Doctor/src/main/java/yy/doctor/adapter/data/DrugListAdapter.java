package yy.doctor.adapter.data;

import android.view.View;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.data.DataVH;
import yy.doctor.model.data.ThomsonDetail;
import yy.doctor.model.data.ThomsonDetail.TThomsonDetail;

/**
 * @author CaiXiang
 * @since 2017/7/14
 */

public class DrugListAdapter extends AdapterEx<String, DataVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_data_item;
    }

    @Override
    protected void refreshView(int position, DataVH holder) {
        if (position != 0) {
            goneView(holder.getDivider());
        }
        goneView(holder.getTvDetail());
    }

    @Override
    protected void onViewClick(int position, View v) {
        ThomsonDetail item= new ThomsonDetail();
        String dataFileId = item.getString(TThomsonDetail.id); // 文件id
       // exeNetworkReq(NetFactory.collection(getOffset(), getLimit(),mType));

    }
}
