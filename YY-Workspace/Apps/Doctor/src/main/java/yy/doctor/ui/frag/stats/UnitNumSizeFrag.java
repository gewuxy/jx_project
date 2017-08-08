package yy.doctor.ui.frag.stats;

import yy.doctor.R;

/**
 * @auther : GuoXuan
 * @since : 2017/7/28
 */

public class UnitNumSizeFrag extends BaseSizeFrag {

    @Override
    protected String getTitle() {
        return "我关注的单位号发布的会议数量";
    }

    @Override
    protected BaseHistogramFrag getFragment() {
        return new UnitNumHistogramFrag();
    }

    @Override
    protected int getTextColor() {
        return R.color.text_e6600e;
    }

}

