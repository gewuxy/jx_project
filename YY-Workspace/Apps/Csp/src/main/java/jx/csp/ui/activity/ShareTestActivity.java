package jx.csp.ui.activity;

import android.support.annotation.NonNull;
import android.view.View;

import jx.csp.R;
import jx.csp.dialog.ShareDialog;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseActivity;

/**
 * @auther WangLan
 * @since 2017/10/12
 */

public class ShareTestActivity extends BaseActivity {
    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_share_test;
    }

    @Override
    public void initNavBar(NavBar bar) {

    }

    @Override
    public void onClick(View v) {
        //Fixme:传个假的url
        ShareDialog shareDialog = new ShareDialog(this, "http://wiki.mob.com/%e5%88%86%e4%ba%ab%e5%88%b0%e6%8c%87%e5%ae%9a%e5%b9%b3%e5%8f%b0/", "哈哈");
        shareDialog.show();
    }

    @Override
    public void findViews() {

    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.share_btn);
    }
}
