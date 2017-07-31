package yy.doctor.ui.frag.collection;

import android.view.View;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.frag.base.BaseSRListFrag;
import yy.doctor.adapter.data.DrugListAdapter;
import yy.doctor.ui.activity.data.DrugDetailActivity;

/**
 * @auther WangLan
 * @since 2017/7/29
 */

public class CollectionDrugListFrag extends BaseSRListFrag<String, DrugListAdapter> {

    @Override
    public void initData() {
        for (int i = 0; i < 12; ++i) {
            addItem(i + "");
        }
    }

    @Override
    public void initNavBar(NavBar bar) {

    }

   /* @Override
    public View createHeaderView() {
        return inflate(R.layout.layout_divider);
    }*/

    @Override
    public void getDataFromNet() {

    }

    @Override
    public void onItemClick(View v, int position) {
        startActivity(DrugDetailActivity.class);
    }

    @Override
    public boolean enableInitRefresh() {
        return false;
    }
}
