package yy.doctor.adapter;

import java.util.List;

import lib.ys.adapter.AdapterEx;
import lib.ys.util.TimeUtil;
import lib.ys.util.TimeUtil.TimeFormat;
import yy.doctor.R;
import yy.doctor.adapter.VH.EpnDetailsVH;
import yy.doctor.model.me.EpnDetails;
import yy.doctor.model.me.EpnDetails.TEpnDetails;

/**
 * @author CaiXiang
 * @since 2017/4/26
 */
public class EpnDetailsAdapter extends AdapterEx<EpnDetails, EpnDetailsVH> {

    @Override
    public int getConvertViewResId() {
        return R.layout.layout_epn_details_item;
    }

    @Override
    protected void refreshView(int position, EpnDetailsVH holder) {

        List<EpnDetails> list = getData();
        String strTime = TimeUtil.formatMilli(list.get(position).getLong(TEpnDetails.costTime), TimeFormat.from_y_to_m_24);
        holder.geTvTime().setText(strTime);
        holder.geTvNum().setText(list.get(position).getString(TEpnDetails.cost));
        holder.geTvContent().setText(list.get(position).getString(TEpnDetails.description));

    }

}
