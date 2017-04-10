package yy.doctor.frag;

import org.json.JSONException;

import lib.ys.adapter.recycler.MultiRecyclerAdapterEx;
import lib.ys.adapter.recycler.RecyclerViewHolderEx;
import lib.ys.network.resp.IListResponse;
import lib.yy.frag.base.BaseSRListFrag;
import yy.doctor.adapter.HomeAdapter;
import yy.doctor.model.home.Home;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;

/**
 * @author Administrator
 * @since 2017/4/5
 */
public class HomeFrag extends BaseSRListFrag<Home> {

    @Override
    public void initData() {
    }

    @Override
    public void initTitleBar() {

    }

    @Override
    public MultiRecyclerAdapterEx<Home, ? extends RecyclerViewHolderEx> createAdapter() {
        return new HomeAdapter();
    }

    @Override
    public void getDataFromNet() {
        exeNetworkRequest(0, NetFactory.home());
    }

    @Override
    public IListResponse<Home> parseNetworkResponse(int id, String text) throws JSONException {
        return JsonParser.home(text);
    }

}
