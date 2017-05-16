package yy.doctor.activity;

import android.view.View;
import android.view.View.OnClickListener;

import lib.ys.ui.other.NavBar;
import lib.yy.activity.base.BaseWebViewActivity;
import yy.doctor.R;
import yy.doctor.util.Util;

/**
 * Created by XPS on 2017/5/16.
 */

public class BannerActivity extends BaseWebViewActivity {

    @Override
    public void initData() {

    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, "详情", this);
        bar.addViewRight(R.mipmap.nav_bar_ic_share, new OnClickListener() {
            @Override
            public void onClick(View v) {

                showToast("852");

            }
        });

    }

    @Override
    protected void onLoadStart() {

    }

}
