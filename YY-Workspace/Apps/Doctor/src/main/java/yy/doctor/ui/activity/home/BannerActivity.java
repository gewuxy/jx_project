package yy.doctor.ui.activity.home;

import android.os.Bundle;

import inject.annotation.router.Arg;
import inject.annotation.router.Route;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseWebViewActivity;
import yy.doctor.R;
import yy.doctor.dialog.ShareDialog;
import yy.doctor.util.Util;

/**
 * Created by CaiXiang on 2017/5/16.
 */
@Route
public class BannerActivity extends BaseWebViewActivity {

    @Arg
    String mUrl;

    @Arg
    String mTitle;

    private ShareDialog mShareDialog;

    @Override
    public void initData(Bundle state) {
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, R.string.title_detail, this);
        bar.addViewRight(R.drawable.nav_bar_ic_share, v -> {
            mShareDialog = new ShareDialog(BannerActivity.this, mUrl, mTitle);
            mShareDialog.show();
        });
    }

    @Override
    protected void onLoadStart() {
        loadUrl(mUrl);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mShareDialog != null) {
            if (mShareDialog.isShowing()) {
                mShareDialog.dismiss();
            }
            mShareDialog = null;
        }

    }
}
