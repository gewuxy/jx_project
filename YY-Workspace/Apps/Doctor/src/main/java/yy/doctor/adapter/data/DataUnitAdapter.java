package yy.doctor.adapter.data;

import android.view.View;

import lib.ys.adapter.AdapterEx;
import lib.ys.util.TextUtil;
import yy.doctor.R;
import yy.doctor.adapter.VH.data.DataVH;
import yy.doctor.model.data.DataUnit;
import yy.doctor.model.data.DataUnit.TDataUnit;
import yy.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;
import yy.doctor.util.UISetter;

/**
 * @author CaiXiang
 * @since 2017/7/14
 */

public class DataUnitAdapter extends AdapterEx<DataUnit, DataVH> {

    private int mType;

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_data_item;
    }

    @Override
    protected void refreshView(int position, DataVH holder) {
        if (position != 0) {
            goneView(holder.getDivider());
        } else {
            showView(holder.getDivider());
        }

        DataUnit item = getItem(position);
        holder.getTvName().setText(item.getString(TDataUnit.title));

        //只有数据中心和收藏药品才需要显示生产商
        View detail = holder.getTvDetail();
        if (mType == DataType.drug) {
            boolean is_drug = item.getBoolean(TDataUnit.isFile) || item.getString(TDataUnit.dataFrom).equals("药品目录");
            if (is_drug && !TextUtil.isEmpty(item.getString(TDataUnit.author))) {
                showView(detail);
                holder.getTvDetail().setText(item.getString(TDataUnit.author));
            } else {
                goneView(detail);
            }
        } else {
            goneView(detail);
        }

        UISetter.viewVisibility(item.getString(TDataUnit.author), holder.getTvDetail());
        setOnViewClickListener(position, holder.getRootLayout());
    }

    public void setDataType(@DataType int type) {
        mType = type;
    }

}
