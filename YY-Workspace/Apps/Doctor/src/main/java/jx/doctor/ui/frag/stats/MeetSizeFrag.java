package jx.doctor.ui.frag.stats;

import jx.doctor.R;

/**
 * @auther : GuoXuan
 * @since : 2017/7/28
 */

public class MeetSizeFrag extends BaseSizeFrag {

    @Override
    protected String getTitle() {
        return "参会数量";
    }

    @Override
    protected BaseHistogramFrag getFragment() {
        return new MeetHistogramFrag();
    }

    @Override
    protected int getTextColor() {
        return R.color.text_0882e7;
    }

}
