package yy.doctor.frag.data;

import android.view.View;

import org.json.JSONException;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.ex.NavBar;
import lib.ys.network.resp.IListResponse;
import lib.yy.frag.base.BaseSRListFrag;
import yy.doctor.R;
import yy.doctor.adapter.ThomsonLibAdapter;

/**
 * @author CaiXiang
 * @since 2017/4/24
 */
public class ThomsonLibFrag extends BaseSRListFrag<String> {

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
    public MultiAdapterEx<String, ? extends ViewHolderEx> createAdapter() {
        return new ThomsonLibAdapter();
    }

    @Override
    public void getDataFromNet() {
    }

    @Override
    public boolean canAutoRefresh() {
        return false;
    }

    @Override
    public IListResponse<String> parseNetworkResponse(int id, String text) throws JSONException {
        return null;
    }

    @Override
    public View getFooterEmptyView() {
        return inflate(R.layout.frag_data_empty);
    }
}
