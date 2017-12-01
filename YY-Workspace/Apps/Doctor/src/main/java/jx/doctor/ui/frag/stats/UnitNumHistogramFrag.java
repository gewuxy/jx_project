package jx.doctor.ui.frag.stats;

import lib.network.model.NetworkReq;
import jx.doctor.R;
import jx.doctor.network.NetworkApiDescriptor.MeetAPI;

/**
 * @auther : GuoXuan
 * @since : 2017/7/27
 */
public class UnitNumHistogramFrag extends BaseHistogramFrag {

    @Override
    protected int getColor() {
        return R.color.text_e6600e;
    }

    @Override
    protected NetworkReq getNetReq() {
        return MeetAPI.statsUnitNum(getOffset()).build();
    }
}
