package yy.doctor.activity.data;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.activity.base.BaseWebViewActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.activity.MainActivity;
import yy.doctor.activity.me.UnitNumDetailActivity;
import yy.doctor.util.Util;

/**
 * @author CaiXiang
 * @since 2017/4/27
 */
public class ThomsonLibActivity extends BaseWebViewActivity {

    private String mUrl;
    public static void nav(Context context, String url) {
        Intent i = new Intent(context, UnitNumDetailActivity.class);
        i.putExtra(Extra.KData, url);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mUrl = getIntent().getStringExtra(Extra.KData);
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
    public void setViews() {
        super.setViews();

        //getWebView().loadUrl(mUrl);
    }

    @Override
    protected void onLoadStart() {

    }

}
