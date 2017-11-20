package jx.csp.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import jx.csp.R;
import lib.ys.fitter.Fitter;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;
import lib.yy.dialog.BaseDialog;

/**
 * 可变的对话框
 *
 * @author : GuoXuan
 * @since : 2017/5/23
 */
public class CommonDialog extends BaseDialog {

    private LinearLayout mLayoutHint; // 提示语
    protected LinearLayout mLayoutButton; // 底部按钮的容器

    public CommonDialog(Context context) {
        super(context);
    }

    @Override
    public void initData(Bundle state) {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_base;
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
        Fitter.view(hintView);
        mLayoutHint.addView(hintView, params);
    }

    /**
     * 按钮的布局
     *
     * @param view
     */
    public void addButtons(View... view) {
        LayoutParams params = getButtonParams();
        LayoutParams dividerParams = getDividerParams();
        params.weight = 1;
        params.gravity = Gravity.CENTER;
        View divider;

        // 块间加分割线
        if (mLayoutButton.getChildCount() > 0) {
            divider = new View(getContext());
            divider.setBackgroundResource(R.color.divider);
            Fitter.view(divider);
            mLayoutButton.addView(divider, dividerParams);
        }

        for (int i = 0; i < view.length; i++) {
            // 块内加分割线
            if (i != 0) {
                divider = new View(getContext());
                divider.setBackgroundResource(R.color.divider);
                Fitter.view(divider);
                mLayoutButton.addView(divider, dividerParams);
            }

            Fitter.view(view[i]);
            mLayoutButton.addView(view[i], params);
        }
    }

    @NonNull
    protected LayoutParams getButtonParams() {
        return LayoutUtil.getLinearParams(0, LayoutUtil.MATCH_PARENT);
    }

    @NonNull
    protected LayoutParams getDividerParams() {
        return LayoutUtil.getLinearParams(Fitter.dp(1), LayoutUtil.MATCH_PARENT);
    }

    /**
     * 16dp大小的按钮(自带dismiss)
     */
    public TextView addButton(String text, @ColorRes int resId, @Nullable OnClickListener l) {
        TextView tv = new TextView(getContext());

        tv.setBackgroundResource(R.drawable.item_selector);
        tv.setGravity(Gravity.CENTER);
        tv.setText(text);
        tv.setTextColor(ResLoader.getColor(resId));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, Fitter.dp(16));
        tv.setOnClickListener(v -> {
            if (l != null) {
                l.onClick(v);
            }
            dismiss();
        });

        addButtons(tv);
        return tv;
    }

    public void addButton(@StringRes int strRes, @ColorRes int resId, @Nullable OnClickListener l) {
        addButton(getContext().getString(strRes), resId, l);
    }

    /**
     * 按钮颜色为666,且没有点击事件的
     */
    public void addGrayButton(@StringRes int strRes) {
        addButton(getContext().getString(strRes), R.color.text_666, null);
    }

    /**
     * 按钮颜色为666,有点击事件
     */
    public void addGrayButton(@StringRes int strRes, @Nullable OnClickListener l) {
        addButton(getContext().getString(strRes), R.color.text_666, l);
    }

    /**
     * 按钮颜色为167afe
     */
    public void addBlueButton(@StringRes int strRes) {
        addButton(getContext().getString(strRes), R.color.text_167afe, null);
    }

    public TextView addBlueButton(@StringRes int strRes, @Nullable OnClickListener l) {
        return addButton(getContext().getString(strRes), R.color.text_167afe, l);
    }

    /**
     * 按钮颜色为167afe
     */
    public void addBlueButton(String text, @Nullable OnClickListener l) {
        addButton(text, R.color.text_167afe, l);
    }


    /**
     * 按钮颜色为333333
     */
    public void addBlackButton(String text, @Nullable OnClickListener l) {
        addButton(text, R.color.text_333, l);
    }

    /**
     * 按钮颜色为333333
     */
    public void addBlackButton(@StringRes int id, @Nullable OnClickListener l) {
        addButton(getContext().getString(id), R.color.text_333, l);
    }

}
