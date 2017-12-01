package jx.doctor.adapter.data;

import android.view.View;

import lib.ys.adapter.AdapterEx;
import lib.ys.util.TextUtil;
import jx.doctor.R;
import jx.doctor.adapter.VH.data.DataVH;
import jx.doctor.model.data.DataUnit;
import jx.doctor.model.data.DataUnit.TDataUnit;
import jx.doctor.ui.frag.data.BaseDataUnitsFrag.DataFrom;
import jx.doctor.ui.frag.data.BaseDataUnitsFrag.DataType;
import jx.doctor.util.UISetter;

/**
 * @author CaiXiang
 * @since 2017/7/14
 */

public class DataUnitAdapter extends AdapterEx<DataUnit, DataVH> {

    private int mType;
    private int mFrom;

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_data_item;
    }

    @Override
    protected void refreshView(int position, DataVH holder) {

        DataUnit item = getItem(position);
        holder.getTvName().setText(item.getString(TDataUnit.title));

        //只有数据中心和收藏药品才需要显示生产商  这两者的数据结构（字段）不太一致
        View detail = holder.getTvDetail();
        if (mType == DataType.drug) {
            boolean isDrug = item.getBoolean(TDataUnit.isFile) || mFrom == DataFrom.collection;
            if (isDrug && !TextUtil.isEmpty(item.getString(TDataUnit.author))) {
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

    public void setDataType(@DataType int type, @DataFrom int from) {
        mType = type;
        mFrom = from;
    }

}
