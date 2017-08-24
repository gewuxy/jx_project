package yy.doctor.adapter.me;

import lib.ys.adapter.AdapterEx;
import lib.ys.util.TimeUtil;
import yy.doctor.R;
import yy.doctor.adapter.VH.me.EpnDetailsVH;
import yy.doctor.model.me.EpnDetails;
import yy.doctor.model.me.EpnDetails.EpnDetailType;
import yy.doctor.model.me.EpnDetails.TEpnDetails;

import static lib.ys.util.res.ResLoader.getString;

/**
 * 象数详情的adapter
 *
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

        EpnDetails item = getItem(position);
        String strTime = TimeUtil.formatMilli(item.getLong(TEpnDetails.costTime), "yyyy/MM/dd HH:mm");
        holder.geTvTime().setText(strTime);
        holder.geTvNum().setText(String.format(getString(R.string.epn_unit), item.getInt(TEpnDetails.cost)));
        holder.geTvContent().setText(item.getString(TEpnDetails.description));

        // 判断 奖励,支付,充值
        int resId = 0;
        switch (item.getInt(TEpnDetails.type)) {
            case EpnDetailType.pay: {
                resId = R.string.pay;
            }
            break;
            case EpnDetailType.recharge: {
                resId = R.string.epn_detail_recharge;
            }
            break;
            case EpnDetailType.award: {
                resId = R.string.award;
            }
            break;
        }
        holder.geTvType().setText(resId);
    }

}
