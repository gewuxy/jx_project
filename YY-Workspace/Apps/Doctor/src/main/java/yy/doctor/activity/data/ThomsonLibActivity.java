package yy.doctor.activity.data;

import android.view.View;
import android.view.View.OnClickListener;

import lib.ys.activity.WebViewActivityEx;
import lib.ys.ex.NavBar;
import lib.yy.activity.base.BaseWebViewActivity;
import yy.doctor.R;
import yy.doctor.activity.MainActivity;
import yy.doctor.util.Util;

/**
 * @author CaiXiang
 * @since 2017/4/27
 */
public class ThomsonLibActivity extends BaseWebViewActivity {

    private WebViewActivityEx mWv;

    @Override
    public void initData() {

    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, "ABCD", this);
        bar.addViewRight(R.mipmap.nav_bar_ic_home, new OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(MainActivity.class);

            }
        });

    }

    @Override
    protected void onLoadStart() {

    }

}
