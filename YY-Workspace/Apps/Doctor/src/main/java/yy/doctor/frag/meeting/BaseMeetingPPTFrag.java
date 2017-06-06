package yy.doctor.frag.meeting;

import lib.ys.ui.other.NavBar;
import lib.yy.frag.base.BaseFrag;
import yy.doctor.model.meet.Detail;

/**
 * @auther : GuoXuan
 * @since : 2017/6/5
 */

public abstract class BaseMeetingPPTFrag extends BaseFrag {

    protected Detail mDetail;
    protected String mMeetId;

    public void setDetail(Detail detail) {
        mDetail = detail;
    }

    public void setMeetId(String meetId) {
        mMeetId = meetId;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initNavBar(NavBar bar) {

    }

    public void toggle() {

    }

}
