package jx.csp.dialog;

import android.content.Context;
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
import lib.jx.dialog.BaseDialog;
import lib.ys.fitter.Fitter;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;

/**
 * @author CaiXiang
 * @since 2017/12/25
 */

public class UpdateNoticeDialog extends BaseDialog {

    private TextView mTvTitle;
    private TextView mTvContent;
    private LinearLayout mLayoutBtn;

    public UpdateNoticeDialog(Context context) {
        super(context);
    }

    @Override
    public void initData() {}

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_update_notice;
    }

    @Override
    public void findViews() {
        mTvTitle = findView(R.id.dialog_update_notice_tv_title);
        mTvContent = findView(R.id.dialog_update_notice_tv_content);
        mLayoutBtn = findView(R.id.dialog_update_notice_btn_layout);
    }

    @Override
    public void setViews() {
        setGravity(Gravity.CENTER);
    }

    public void setVersion(String s) {
        mTvTitle.setText(String.format(ResLoader.getString(R.string.have_new_version), s));
    }

    public void setContent(String content) {
        mTvContent.setText(content);
    }

    public TextView addButton(@StringRes int strRes, @ColorRes int resId, @Nullable OnClickListener l) {
        TextView tv = new TextView(getContext());

        tv.setBackgroundResource(R.drawable.item_selector);
        tv.setGravity(Gravity.CENTER);
        tv.setText(getContext().getString(strRes));
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
        if (mLayoutBtn.getChildCount() > 0) {
            divider = new View(getContext());
            divider.setBackgroundResource(R.color.divider);
            Fitter.view(divider);
            mLayoutBtn.addView(divider, dividerParams);
        }

        for (int i = 0; i < view.length; i++) {
            // 块内加分割线
            if (i != 0) {
                divider = new View(getContext());
                divider.setBackgroundResource(R.color.divider);
                Fitter.view(divider);
                mLayoutBtn.addView(divider, dividerParams);
            }

            Fitter.view(view[i]);
            mLayoutBtn.addView(view[i], params);
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

}
