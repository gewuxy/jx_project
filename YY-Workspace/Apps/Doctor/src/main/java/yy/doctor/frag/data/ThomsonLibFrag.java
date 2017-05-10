package yy.doctor.frag.data;

import android.view.View;

import org.json.JSONException;

import lib.ys.network.resp.IListResponse;
import lib.ys.ui.other.NavBar;
import lib.ys.util.view.ViewUtil;
import lib.yy.frag.base.BaseSRListFrag;
import yy.doctor.R;
import yy.doctor.activity.data.ThomsonLibActivity;
import yy.doctor.adapter.ThomsonLibAdapter;

/**
 * @author CaiXiang
 * @since 2017/4/24
 */
public class ThomsonLibFrag extends BaseSRListFrag<String, ThomsonLibAdapter> {

    @Override
    public void initData() {

        for (int i = 0; i < 12; ++i) {
            addItem(i + "");
        }

    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    public View createHeaderView() {
        return ViewUtil.inflateSpaceViewDp(12);
    }

    @Override
    public void getDataFromNet() {
    }

    @Override
    public boolean enableAutoRefresh() {
        return false;
    }

    @Override
    public View getFooterEmptyView() {
        return inflate(R.layout.frag_data_empty);
    }

    @Override
    public void onItemClick(View v, int position) {
        super.onItemClick(v, position);

        startActivity(ThomsonLibActivity.class);
    }
}
