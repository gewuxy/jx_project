package yy.doctor.adapter.data;

import android.view.View;

import lib.ys.adapter.AdapterEx;
import lib.ys.util.TextUtil;
import yy.doctor.R;
import yy.doctor.adapter.VH.data.DataVH;
import yy.doctor.model.data.DataUnit;
import yy.doctor.model.data.DataUnit.FileOpenType;
import yy.doctor.model.data.DataUnit.TDataUnit;
import yy.doctor.util.UISetter;

/**
 * @author CaiXiang
 * @since 2017/7/14
 */

public class DataUnitAdapter extends AdapterEx<DataUnit, DataVH> {

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

        View detail = holder.getTvDetail();
        if (item.getBoolean(TDataUnit.isFile)
                && item.getInt(TDataUnit.openType) == FileOpenType.pdf
                && !TextUtil.isEmpty(item.getString(TDataUnit.author))) {
            showView(detail);
            holder.getTvDetail().setText(item.getString(TDataUnit.author));
        } else {
            goneView(detail);
        }

        UISetter.viewVisibility(item.getString(TDataUnit.author), holder.getTvDetail());

        setOnViewClickListener(position, holder.getRootLayout());
    }

}
