package jx.csp.ui.frag.main;

import inject.annotation.router.Route;
import jx.csp.adapter.main.MeetCardAdapter;

/**
 * 首页卡片的frag
 *
 * @auther : GuoXuan
 * @since : 2018/2/1
 */
@Route
public class MeetCardFrag extends MeetFrag<MeetCardAdapter> {

    @Override
    public void onSwipeRefreshAction() {
        super.onSwipeRefreshAction();

        getAdapter().setNeedShow();
    }

    @Override
    public void invalidate() {
        super.invalidate();

        getAdapter().setNeedShow();
    }

    @Override
    public void thisRefresh() {
        super.thisRefresh();

        getAdapter().setNeedShow();
    }
}
