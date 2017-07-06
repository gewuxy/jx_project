package yy.doctor.ui.frag.data;

import android.view.View;

import lib.ys.ui.other.NavBar;
import lib.ys.util.view.ViewUtil;
import lib.yy.ui.frag.base.BaseSRListFrag;
import yy.doctor.adapter.ThomsonAdapter;
import yy.doctor.model.data.ThomsonDetail;
import yy.doctor.network.NetFactory;

/**
 * 汤森路透
 *
 * @author CaiXiang
 * @since 2017/4/24
 */
public class ThomsonFrag extends BaseSRListFrag<ThomsonDetail, ThomsonAdapter> {

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public View createHeaderView() {
        return ViewUtil.inflateSpaceViewDp(8);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.thomsonAll(getOffset(), getLimit()));
    }

    @Override
    public int getLimit() {
        return 500;
    }

    @Override
    public boolean enableInitRefresh() {
        return true;
    }

}
