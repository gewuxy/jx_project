package jx.csp.ui.activity;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;

import jx.csp.R;
import jx.csp.view.CustomVideoView;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseActivity;

/**
 * @auther WangLan
 * @since 2017/10/13
 */

public class LoginVideoActivtity extends BaseActivity {

    private CustomVideoView mCustomVideoView;

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.activity_login_video;
    }

    @Override
    public void initNavBar(NavBar bar) {

    }

    @Override
    public void findViews() {
        mCustomVideoView = findView(R.id.videoview);
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.video_start);
        mCustomVideoView.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.media));
        mCustomVideoView.start();
        mCustomVideoView.setOnCompletionListener(mp -> mCustomVideoView.start());
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        showToast("进入主页");
    }
}
