package lib.ys.ui.interfaces.opts.impl;

import android.content.Intent;
import android.support.annotation.StringRes;
import android.view.View;

import lib.ys.AppEx;
import lib.ys.ui.interfaces.opts.CommonOpt;
import lib.ys.util.LaunchUtil;
import lib.ys.util.view.ViewUtil;

/**
 * @auther yuansui
 * @since 2017/5/8
 */

public class CommonOptImpl implements CommonOpt {

    private Object mHost;

    public CommonOptImpl(Object host) {
        mHost = host;
    }

    @Override
    public void showView(View v) {
        ViewUtil.showView(v);
    }

    @Override
    public void hideView(View v) {
        ViewUtil.hideView(v);
    }

    @Override
    public void goneView(View v) {
        ViewUtil.goneView(v);
    }

    @Override
    public void startActivity(Class<?> clz) {
        LaunchUtil.startActivity(mHost, clz);
    }

    @Override
    public void startActivity(Intent intent) {
        LaunchUtil.startActivity(mHost, intent);
    }

    @Override
    public void startActivityForResult(Class<?> clz, int requestCode) {
        LaunchUtil.startActivityForResult(mHost, clz, requestCode);
    }

    @Override
    public void showToast(String content) {
        AppEx.showToast(content);
    }

    @Override
    public void showToast(@StringRes int... resId) {
        AppEx.showToast(resId);
    }
}
