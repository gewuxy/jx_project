package yy.doctor.ui.frag.collection;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseSRListFrag;
import yy.doctor.adapter.data.ThomsonAdapter;
import yy.doctor.model.data.ThomsonDetail;
import yy.doctor.network.NetFactory;

/**
 * @auther WangLan
 * @since 2017/7/29
 */

public class CollectionClinicalGuideFrag extends BaseSRListFrag<ThomsonDetail, ThomsonAdapter> {
    @Override
    public void initData() {

    }

    @Override
    public void initNavBar(NavBar bar) {

    }

  /*  @Override
    public View createHeaderView() {
        return inflate(R.layout.layout_divider);
    }*/

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
        return false;
    }
}
