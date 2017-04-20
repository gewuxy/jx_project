package lib.ys.decor;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import lib.ys.R;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.dialog.DialogEx;
import lib.ys.util.view.LayoutUtil;

/**
 * {@link DecorViewEx}专用的内部loading页面
 *
 * @author yuansui
 */
public class LoadingDecorEx extends RelativeLayout {

    private IDecorProgressView mPv;
    private DialogEx mDialogEx;

    public LoadingDecorEx(Context context, @RefreshWay int style, DialogEx dialog) {
        super(context);

        mDialogEx = dialog;

        if (style == RefreshWay.embed) {
            // 内嵌式才加入内部loadingView
            View v = inflate(getContext(), R.layout.dialog_loading, null);
            LayoutParams params = LayoutUtil.getRelativeParams(LayoutUtil.WRAP_CONTENT, LayoutUtil.WRAP_CONTENT);
            params.addRule(CENTER_IN_PARENT);
            addView(v, params);
            mPv = (IDecorProgressView) findViewById(R.id.progress_view);
        }
    }


    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        switch (visibility) {
            case VISIBLE: {
                start();
            }
            break;
            case INVISIBLE:
            case GONE: {
                stop();
            }
            break;
        }
    }

    private void start() {
        if (mPv != null) {
            mPv.start();
        } else {
            showDialog();
        }
    }

    private void stop() {
        if (mPv != null) {
            mPv.stop();
        } else {
            dismissDialog();
        }
    }

    public void showDialog() {
        if (mDialogEx != null) {
            mDialogEx.show();
        }
    }

    public void dismissDialog() {
        if (mDialogEx != null) {
            mDialogEx.dismiss();
        }
    }
}
