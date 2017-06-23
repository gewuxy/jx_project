package yy.doctor.activity.me.epc;

import android.view.View;
import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseSRListActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.adapter.OrderAdapter;
import yy.doctor.model.me.Order;
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * 订单
 *
 * @author CaiXiang
 * @since 2017/4/27
 */
public class OrderActivity extends BaseSRListActivity<Order, OrderAdapter> {

    private boolean isTvShow = false;
    private TextView mTv;

    @Override
    public void initData() {
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_order;
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, R.string.order, this);
        bar.addViewRight(R.mipmap.nav_bar_ic_i, v -> {
            if (isTvShow) {
                mTv.setVisibility(View.GONE);
            } else {
                mTv.setVisibility(View.VISIBLE);
            }
            isTvShow = !isTvShow;
        });
    }

    @Override
    public void findViews() {
        super.findViews();

        mTv = findView(R.id.order_tv);
    }

    @Override
    public void setViews() {
        super.setViews();

        setDividerHeight(0);
        setOnClickListener(R.id.order_tv);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int id = v.getId();
        switch (id) {
            case R.id.order_tv: {
                mTv.setVisibility(View.GONE);
                isTvShow = !isTvShow;
            }
            break;
        }
    }

    @Override
    public void getDataFromNet() {
        exeNetworkReq(NetFactory.order(getOffset(), getLimit()));
    }

    @Override
    public int getLimit() {
        return Extra.KPageSize;
    }
}
