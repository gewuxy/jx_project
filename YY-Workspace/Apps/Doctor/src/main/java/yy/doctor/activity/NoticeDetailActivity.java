package yy.doctor.activity;

import android.content.Context;
import android.content.Intent;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.activity.base.BaseWebViewActivity;
import yy.doctor.Extra;
import yy.doctor.util.Util;

/**
 * 通知详情页面 h5
 *
 * @author CaiXiang
 * @since 2017/6/8
 */

public class NoticeDetailActivity extends BaseWebViewActivity{

    private String mUrl;

    public static void nav(Context context, String url) {
        Intent i = new Intent(context, NoticeDetailActivity.class)
                .putExtra(Extra.KData, url);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mUrl = getIntent().getStringExtra(Extra.KData);
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "通知", this);
    }

    @Override
    protected void onLoadStart() {
        loadUrl(mUrl);
    }

}
