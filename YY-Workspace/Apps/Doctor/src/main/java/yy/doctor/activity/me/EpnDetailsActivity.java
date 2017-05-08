package yy.doctor.activity.me;

import org.json.JSONException;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.network.resp.IListResponse;
import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseSRListActivity;
import yy.doctor.R;
import yy.doctor.adapter.EpnDetailsAdapter;
import yy.doctor.model.me.EpnDetails;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;

/**
 * 象数明细
 *
 * @author CaiXiang
 * @since 2017/4/26
 */
public class EpnDetailsActivity extends BaseSRListActivity<EpnDetails> {

    @Override
    public void initData() {

    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addBackIcon(R.mipmap.nav_bar_ic_back, "明细", this);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkRequest(0, NetFactory.epnDedails());
    }

    @Override
    public IListResponse<EpnDetails> parseNetworkResponse(int id, String text) throws JSONException {
        return JsonParser.evs(text, EpnDetails.class);
    }

    @Override
    public MultiAdapterEx<EpnDetails, ? extends ViewHolderEx> createAdapter() {
        return new EpnDetailsAdapter();
    }

}
