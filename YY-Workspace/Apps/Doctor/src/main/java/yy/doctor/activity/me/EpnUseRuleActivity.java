package yy.doctor.activity.me;

import android.content.Context;
import android.content.Intent;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.activity.base.BaseWebViewActivity;
import yy.doctor.Extra;
import yy.doctor.util.Util;

/**
 * 象数使用规则
 *
 * @author CaiXiang
 * @since 2017/4/20
 */
public class EpnUseRuleActivity extends BaseWebViewActivity {

    private String mUrl;

    public static void nav(Context context, String url) {
        Intent i = new Intent(context, EpnUseRuleActivity.class);
        i.putExtra(Extra.KData, url);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, "象数使用规则", this);
    }


    @Override
    protected void onLoadStart() {

    }

}
