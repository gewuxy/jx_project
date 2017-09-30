package jx.csp.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;

import jx.csp.R;
import lib.yy.dialog.BaseDialog;

/**
 * @auther Huoxuyu
 * @since 2017/9/28
 */

public class PlatformDialog extends BaseDialog{

    public PlatformDialog(Context context) {
        super(context);
    }

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_platform_hint;
    }

    @Override
    public void findViews() {
    }

    @Override
    public void setViews() {
        setGravity(Gravity.TOP);
    }
}
