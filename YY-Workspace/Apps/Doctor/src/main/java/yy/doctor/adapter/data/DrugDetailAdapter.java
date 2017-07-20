package yy.doctor.adapter.data;

import android.view.View;

import lib.ys.adapter.GroupAdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.data.DrugDetailVH;
import yy.doctor.model.data.GroupDrugDetail;

/**
 * @author CaiXiang
 * @since 2017/7/14
 */

public class DrugDetailAdapter extends GroupAdapterEx<GroupDrugDetail, String, DrugDetailVH> {

    @Override
    public int getGroupConvertViewResId() {
        return R.layout.layout_drug_detail_group;
    }

    @Override
    public void refreshGroupView(int groupPosition, boolean isExpanded, DrugDetailVH holder) {
        if (isExpanded) {
            holder.getIv().setSelected(true);
        } else {
            holder.getIv().setSelected(false);
        }
    }

    @Override
    public void onGroupViewClick(int groupPosition, View v) {

    }

    @Override
    public int getChildConvertViewResId() {
        return R.layout.layout_drug_detail_item;
    }

    @Override
    public void refreshChildView(int groupPosition, int childPosition, boolean isLastChild, DrugDetailVH holder) {

    }

}
