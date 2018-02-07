package jx.csp.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

import jx.csp.R;
import jx.csp.ui.activity.me.GreenHandsGuideActivityRouter;
import jx.csp.util.Util;
import lib.jx.dialog.BaseDialog;

/**
 * 新手指引
 *
 * @auther HuoXuYu
 * @since 2018/2/3
 */

public class GuideDialog extends BaseDialog {

    public GuideDialog(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        // do nothing
    }

    @Override
    public int getContentViewId() {
        return R.layout.dialog_guide;
    }

    @Override
    public void findViews() {
        // do nothing
    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.guide_cancel);
        setOnClickListener(R.id.guide_watch);

        setGravity(Gravity.CENTER);

        setOnDismissListener(dialog -> {
            FunctionGuideDialog d = new FunctionGuideDialog(getContext());
            d.setCancelable(false);
            d.show();
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.guide_cancel: {
                setCancelable(false);
                dismiss();
            }
            break;
            case R.id.guide_watch: {
                Util.runOnUIThread(this::dismiss, 500);

                if (Util.checkAppCn()) {
                    GreenHandsGuideActivityRouter.create("1").route(getContext());
                } else {
                    GreenHandsGuideActivityRouter.create("2").route(getContext());
                }
            }
            break;
        }
    }
}
