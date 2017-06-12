package yy.doctor.adapter;

import lib.ys.adapter.AdapterEx;
import yy.doctor.R;
import yy.doctor.adapter.VH.ThomsonVH;
import yy.doctor.model.data.ThomsonDetail;
import yy.doctor.model.data.ThomsonDetail.TThomsonDetail;

/**
 * @author CaiXiang
 * @since 2017/4/27
 */
public class ThomsonAdapter extends AdapterEx<ThomsonDetail, ThomsonVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_thomson_item;
    }

    @Override
    protected void refreshView(int position, ThomsonVH holder) {

        ThomsonDetail item = getItem(position);
        holder.getTv().setText(getItem(position).getString(TThomsonDetail.title));
        String size = item.getLong(TThomsonDetail.fileSize) + "K";
        holder.getTvSize().setText(size);
    }

}
