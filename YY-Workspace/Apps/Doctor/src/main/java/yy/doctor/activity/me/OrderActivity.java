package yy.doctor.activity.me;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.ex.NavBar;
import lib.yy.activity.base.BaseListActivity;
import yy.doctor.R;
import yy.doctor.adapter.OrderAdapter;

/**
 * 订单
 *
 * @author CaiXiang
 * @since 2017/4/27
 */
public class OrderActivity extends BaseListActivity<String> {

    private boolean isTvShow = false;
    private TextView mTv;

    @Override
    public void initData() {

        for (int i = 0; i < 5; ++i) {
            addItem(i + " ");
        }

    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_order;
    }

    @Override
    public void initNavBar(NavBar bar) {

        bar.addBackIcon(R.mipmap.nav_bar_ic_back, "订单", this);
        bar.addViewRight(R.mipmap.nav_bar_ic_i, new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isTvShow) {
                    mTv.setVisibility(View.GONE);
                } else {
                    mTv.setVisibility(View.VISIBLE);
                }
                isTvShow = !isTvShow;
            }
        });

    }

    @Override
    public void findViews() {
        super.findViews();

    }

    @Override
    public void setViews() {
        super.setViews();

        mTv = findView(R.id.order_tv);

        mTv.setOnClickListener(this);

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
    public MultiAdapterEx<String, ? extends ViewHolderEx> createAdapter() {
        return new OrderAdapter();
    }

}
