package yy.doctor.activity.me;

import android.view.View;
import android.widget.TextView;

import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseSRListActivity;
import yy.doctor.R;
import yy.doctor.adapter.OrderAdapter;
import yy.doctor.model.me.Order;
import yy.doctor.network.NetFactory;

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

        bar.addBackIcon(R.mipmap.nav_bar_ic_back, "订单", this);
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
        exeNetworkReq(NetFactory.order());
    }

    @Override
    protected String getEmptyText() {
        return "暂时没有相关内容";
    }

}
