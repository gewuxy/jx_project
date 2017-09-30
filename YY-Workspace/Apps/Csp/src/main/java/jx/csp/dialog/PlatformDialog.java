package jx.csp.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.LinearLayout;

import jx.csp.R;
import lib.yy.dialog.BaseDialog;

/**
 * @auther Huoxuyu
 * @since 2017/9/28
 */

public class PlatformDialog extends BaseDialog{

    private LinearLayout mLayout;

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
        mLayout = findView(R.id.dialog_layout_platform);
    }

    @Override
    public void setViews() {
        setGravity(Gravity.TOP);
        mLayout.getBackground().setAlpha(100);
    }
}
