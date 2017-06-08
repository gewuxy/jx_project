package yy.doctor.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.activity.base.BaseWebViewActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.dialog.ShareDialog;
import yy.doctor.util.Util;

/**
 * Created by XPS on 2017/5/16.
 */

public class BannerActivity extends BaseWebViewActivity {

    private String mUrl;

    public static void nav(Context context, String url) {
        Intent i = new Intent(context, BannerActivity.class);
        i.putExtra(Extra.KData, url);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mUrl = getIntent().getStringExtra(Extra.KData);
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, "详情", this);
        bar.addViewRight(R.mipmap.nav_bar_ic_share, new OnClickListener() {

            @Override
            public void onClick(View v) {
                ShareDialog shareDialog = new ShareDialog(BannerActivity.this, mUrl);
                shareDialog.show();
            }
        });
    }

    @Override
    protected void onLoadStart() {
        getWebView().loadUrl(mUrl);
    }

}
