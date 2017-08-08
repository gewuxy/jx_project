package yy.doctor.ui.activity.me.epc;

import android.view.View;

import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseSRListActivity;
import yy.doctor.R;
import yy.doctor.adapter.me.EpcAdapter;
import yy.doctor.model.me.Epc;
import yy.doctor.model.me.Epc.TEpc;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 象城
 *
 * @author CaiXiang
 * @since 2017/4/26
 */
public class EpcActivity extends BaseSRListActivity<Epc, EpcAdapter> {

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.epc, this);
        bar.addTextViewRight(R.string.order, v -> startActivity(OrderActivity.class));
    }

    @Override
    public void onItemClick(View v, int position) {
        Epc item = getItem(position);
        EpcDetailActivityIntent.create(
                item.getInt(TEpc.id),
                item.getString(TEpc.name),
                item.getString(TEpc.picture)
        )
                .start(this);
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.epc(getOffset(), getLimit()));
    }
}
