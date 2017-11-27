package jx.csp.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import jx.csp.R;
import lib.ys.fitter.Fitter;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;

/**
 * 部分英文的定制CommonDialog
 *
 * @auther : GuoXuan
 * @since : 2017/11/7
 */

public class BtnVerticalDialog extends CommonDialog {

    public BtnVerticalDialog(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_btn_vertical;
    }

    @NonNull
    @Override
    protected LinearLayout.LayoutParams getDividerParams() {
        LinearLayout.LayoutParams params;
        if (mLayoutButton.getOrientation() == LinearLayout.HORIZONTAL) {
            params = super.getDividerParams();
        } else {
            params = LayoutUtil.getLinearParams(LayoutUtil.MATCH_PARENT, Fitter.dp(1));
        }
        return params;
    }

    @NonNull
    protected LinearLayout.LayoutParams getButtonParams() {
        LinearLayout.LayoutParams params;
        if (mLayoutButton.getOrientation() == LinearLayout.HORIZONTAL) {
            params = super.getButtonParams();
        } else {
            params = LayoutUtil.getLinearParams(LayoutUtil.MATCH_PARENT, Fitter.dp(46));
        }
        return params;
    }

    public void setTextHint(CharSequence c) {
        TextView tv = new TextView(getContext());
        tv.setGravity(Gravity.CENTER);
        tv.setText(c);
        tv.setTextColor(ResLoader.getColor(R.color.text_333));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, Fitter.dp(16));
        addHintView(tv);
    }

}
