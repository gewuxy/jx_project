package yy.doctor.activity.me;

import android.view.View;
import android.view.View.OnClickListener;

import lib.ys.adapter.MultiAdapterEx;
import lib.ys.adapter.ViewHolderEx;
import lib.ys.ex.NavBar;
import lib.yy.activity.base.BaseListActivity;
import yy.doctor.R;
import yy.doctor.adapter.EpcAdapter;

/**
 * 象城
 *
 * @author CaiXiang
 * @since 2017/4/26
 */
public class EpcActivity extends BaseListActivity<String> {

    @Override
    public void initData() {

        for (int i = 0; i < 8; ++i) {
            addItem(" " + i);
        }

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
    public MultiAdapterEx<String, ? extends ViewHolderEx> createAdapter() {
        return new EpcAdapter();
    }

    @Override
    public void onItemClick(View v, int position) {
        super.onItemClick(v, position);

        startActivity(EpcDetailActivity.class);
    }

}
