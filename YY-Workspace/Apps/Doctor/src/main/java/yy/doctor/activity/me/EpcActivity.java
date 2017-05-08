package yy.doctor.activity.me;

import android.view.View;
import android.view.View.OnClickListener;

import org.json.JSONException;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.network.resp.IListResponse;
import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseSRListActivity;
import yy.doctor.R;
import yy.doctor.adapter.EpcAdapter;
import yy.doctor.model.me.Epc;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;

/**
 * 象城
 *
 * @author CaiXiang
 * @since 2017/4/26
 */
public class EpcActivity extends BaseSRListActivity<Epc> {

    @Override
    public void initData() {

    }

    @Override
    public void initNavBar(NavBar bar) {

        bar.addBackIcon(R.mipmap.nav_bar_ic_back, "象城", this);
        bar.addTextViewRight("订单", new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(OrderActivity.class);
            }
        });
    }

    @Override
    public MultiAdapterEx<Epc, ? extends ViewHolderEx> createAdapter() {
        return new EpcAdapter();
    }

    @Override
    public void onItemClick(View v, int position) {
        super.onItemClick(v, position);

        startActivity(EpcDetailActivity.class);
    }

    @Override
    public void getDataFromNet() {

        exeNetworkRequest(0, NetFactory.epc());
    }

    @Override
    public IListResponse<Epc> parseNetworkResponse(int id, String text) throws JSONException {
        return JsonParser.evs(text, Epc.class);
    }
}
