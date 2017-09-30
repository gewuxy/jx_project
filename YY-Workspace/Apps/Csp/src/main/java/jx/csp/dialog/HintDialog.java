package jx.csp.dialog;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import lib.ys.fitter.DpFitter;
import lib.ys.fitter.LayoutFitter;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;
import lib.yy.dialog.BaseDialog;
import jx.csp.R;

/**
 * 可变的对话框
 *
 * @author : GuoXuan
 * @since : 2017/5/23
 */
public class HintDialog extends BaseDialog {

    private LinearLayout mLayoutHint; // 提示语
    private LinearLayout mLayoutButton; // 底部按钮的容器

    public HintDialog(Context context) {
        super(context);
    }

    @Override
    public void initData() {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_base_hint;
    }

    @CallSuper
    @Override
    public void findViews() {
        mLayoutHint = findView(R.id.dialog_layout_hint);
        mLayoutButton = findView(R.id.dialog_layout_button);
    }

    @Override
    public void setViews() {
    }

    /**
     * 提示的布局
     *
     * @param hintView
     */
    public void addHintView(View hintView) {
        LayoutParams params = LayoutUtil.getLinearParams(LayoutUtil.MATCH_PARENT, LayoutUtil.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        LayoutFitter.fit(hintView);
        mLayoutHint.addView(hintView, params);
    }

    /**
     * 按钮的布局
     *
     * @param view
     */
    public void addButtons(View... view) {
        LayoutParams params = LayoutUtil.getLinearParams(0, LayoutUtil.MATCH_PARENT);
        LayoutParams dividerParams = LayoutUtil.getLinearParams(DpFitter.dp(1), LayoutUtil.MATCH_PARENT);
        params.weight = 1;
        params.gravity = Gravity.CENTER;
        View divider;

        // 块间加分割线
        if (mLayoutButton.getChildCount() > 0) {
            divider = new View(getContext());
            divider.setBackgroundResource(R.color.divider);
            LayoutFitter.fit(divider);
            mLayoutButton.addView(divider, dividerParams);
        }

        for (int i = 0; i < view.length; i++) {
            // 块内加分割线
            if (i != 0) {
                divider = new View(getContext());
                divider.setBackgroundResource(R.color.divider);
                LayoutFitter.fit(divider);
                mLayoutButton.addView(divider, dividerParams);
            }

            LayoutFitter.fit(view[i]);
            mLayoutButton.addView(view[i], params);
        }
    }

    /**
     * 16dp大小的按钮(自带dismiss)
     */
    public void addButton(String text, @ColorRes int resId, OnClickListener l) {
        TextView tv = new TextView(getContext());

        tv.setBackgroundResource(R.drawable.item_selector);
        tv.setGravity(Gravity.CENTER);
        tv.setText(text);
        tv.setTextColor(ResLoader.getColor(resId));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, DpFitter.dp(16));
        tv.setOnClickListener(v -> {
            if (l != null){
                l.onClick(v);
            }
            dismiss();
        });

        addButtons(tv);
    }

    public void addButton(@StringRes int strRes, @ColorRes int resId, OnClickListener l) {
        addButton(getContext().getString(strRes), resId, l);
    }

    /**
     * 按钮颜色为666,且没有点击事件的
     */
    public void addGrayButton(@StringRes int strRes) {
        addButton(getContext().getString(strRes), R.color.text_333, null);
    }

    /**
     * 按钮颜色为167afe
     */
    public void addBlueButton(@StringRes int strRes) {
        addButton(getContext().getString(strRes), R.color.text_167afe, null);
    }

    /**
     * 按钮颜色为167afe
     */
    public void addBlueButton(String text, OnClickListener l) {
        addButton(text, R.color.text_167afe, l);
    }


    /**
     * 按钮颜色为333333
     */
    public void addBlackButton(String text, OnClickListener l) {
        addButton(text, R.color.text_333, l);
    }

}
