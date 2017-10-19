package jx.csp.ui.activity.main;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseVPFrag;

/**
 * @auther WangLan
 * @since 2017/10/17
 */

public class MainSlideFrag extends BaseVPFrag {

    @Override
    public void initData() {
        add(new MainMeetingFrag());
        add(new MainMeetingFrag());
        add(new MainMeetingFrag());
        add(new MainMeetingFrag());
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

}
