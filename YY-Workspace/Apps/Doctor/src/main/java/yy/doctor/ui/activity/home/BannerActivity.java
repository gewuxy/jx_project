package yy.doctor.ui.activity.home;

import router.annotation.AutoIntent;
import router.annotation.Extra;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseWebViewActivity;
import yy.doctor.R;
import yy.doctor.dialog.ShareDialog;
import yy.doctor.util.Util;

/**
 * Created by CaiXiang on 2017/5/16.
 */
@AutoIntent
public class BannerActivity extends BaseWebViewActivity {

    @Extra
    String mUrl;

    @Extra
    String mTitle;

    private ShareDialog mShareDialog;

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, R.string.title_detail, this);
        bar.addViewRight(R.mipmap.nav_bar_ic_share, v -> {
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
