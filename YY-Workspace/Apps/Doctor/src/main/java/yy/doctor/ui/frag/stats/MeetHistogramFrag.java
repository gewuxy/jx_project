package yy.doctor.ui.frag.stats;

import lib.network.model.NetworkReq;
import yy.doctor.R;
import yy.doctor.network.NetFactory;

/**
 * @auther : GuoXuan
 * @since : 2017/7/27
 */
public class MeetHistogramFrag extends BaseHistogramFrag {

    @Override
    protected int getColor() {
        return R.color.text_0882e7;
    }

    @Override
    protected NetworkReq getNetReq() {
        return NetFactory.statsMeet(getOffset());
    }

}
