package yy.doctor.activity.me;

import android.view.View;

import org.json.JSONException;

import java.util.List;

import lib.network.model.interfaces.IListResult;
import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseSRListActivity;
import lib.yy.network.ListResult;
import yy.doctor.R;
import yy.doctor.adapter.EpcAdapter;
import yy.doctor.model.me.Epc;
import yy.doctor.model.me.Epc.TEpc;
import yy.doctor.network.JsonParser;
import yy.doctor.network.NetFactory;

/**
 * 象城
 *
 * @author CaiXiang
 * @since 2017/4/26
 */
public class EpcActivity extends BaseSRListActivity<Epc, EpcAdapter> {

    private List<Epc> list;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.addBackIcon(R.mipmap.nav_bar_ic_back, "象城", this);
        bar.addTextViewRight("订单", v -> startActivity(OrderActivity.class));
    }

    @Override
    public void onItemClick(View v, int position) {

        EpcDetailActivity.nav(EpcActivity.this, list.get(position).getInt(TEpc.id), list.get(position).getString(TEpc.name));
    }

    @Override
    public void getDataFromNet() {

        exeNetworkReq(NetFactory.epc());
    }

    @Override
    public IListResult<Epc> parseNetworkResponse(int id, String text) throws JSONException {

        ListResult<Epc> r = (ListResult<Epc>) JsonParser.evs(text, Epc.class);
        if (r.isSucceed()) {
            list = r.getData();
        }
        return r;
    }

    @Override
    protected String getEmptyText() {
        return "暂时没有相关内容";
    }
}
