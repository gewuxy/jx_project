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
        ShareDialog shareDialog = new ShareDialog(this, 1, "哈哈","..");
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
