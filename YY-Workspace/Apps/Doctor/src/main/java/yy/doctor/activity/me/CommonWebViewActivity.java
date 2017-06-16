package yy.doctor.activity.me;

import android.content.Context;
import android.content.Intent;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.activity.base.BaseWebViewActivity;
import yy.doctor.Extra;
import yy.doctor.util.Util;

/**
 * WebView页面
 *
 * @author CaiXiang
 * @since 2017/6/16
 */

public class CommonWebViewActivity extends BaseWebViewActivity {

    private String mName;
    private String mUrl;

    public static void nav(Context context, String goodName, String url) {
        Intent i = new Intent(context, CommonWebViewActivity.class);
        i.putExtra(Extra.KName, goodName);
        i.putExtra(Extra.KUrl, url);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mName = getIntent().getStringExtra(Extra.KName);
        mUrl = getIntent().getStringExtra(Extra.KUrl);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, mName, this);
    }

    @Override
    protected void onLoadStart() {
        loadUrl(mUrl);
    }

}
