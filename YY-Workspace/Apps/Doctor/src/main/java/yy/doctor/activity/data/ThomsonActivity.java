package yy.doctor.activity.data;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;

import lib.ys.ui.other.NavBar;
import lib.ys.util.LaunchUtil;
import lib.yy.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.activity.MainActivity;
import yy.doctor.util.Util;

/**
 * 汤森路透详情页面
 *
 * @author CaiXiang
 * @since 2017/4/27
 */
public class ThomsonActivity extends BaseActivity {

    private String mUrl;
    private String mName;

    public static void nav(Context context, String url, String name) {
        Intent i = new Intent(context, ThomsonActivity.class);
        i.putExtra(Extra.KFilePath, url);
        i.putExtra(Extra.KName, name);
        LaunchUtil.startActivity(context, i);
    }

    @Override
    public void initData() {
        mUrl = getIntent().getStringExtra(Extra.KFilePath);
        mName = getIntent().getStringExtra(Extra.KName);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_thomson;
    }

    @Override
    public void initNavBar(NavBar bar) {

        Util.addBackIcon(bar, mName, this);
        bar.addViewRight(R.mipmap.nav_bar_ic_home, new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(MainActivity.class);
            }
        });
    }

    @Override
    public void findViews() {


    }

    @Override
    public void setViews() {


    }


}
