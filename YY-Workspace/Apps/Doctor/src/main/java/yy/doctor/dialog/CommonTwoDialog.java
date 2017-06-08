package yy.doctor.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import lib.yy.dialog.BaseDialog;
import yy.doctor.R;

/**
 * 双按钮(2行提示)的提示框
 * 形如: **********************
 * ******************
 * ******       *******
 *
 * @author : GuoXuan
 * @since : 2017/5/23
 */

public class CommonTwoDialog extends BaseDialog {

    private TextView mTvMainHint;//主提示
    private TextView mTvSecondaryHint;//副提示
    private TextView mTvLeft;//左键
    private TextView mTvRight;//右键
    private OnLayoutListener mOnLayoutListener;

    public interface OnLayoutListener {
        void leftClick(View v);

        void rightClick(View v);
    }

    public void setLayoutListener(OnLayoutListener onLayoutListener) {
        mOnLayoutListener = onLayoutListener;
    }

    public CommonTwoDialog(Context context) {
        super(context);
    }

    public CommonTwoDialog setTvMainHint(String tvMainHint) {
        mTvMainHint.setText(tvMainHint);
        return this;
    }

    public CommonTwoDialog setTvSecondaryHint(String tvSecondaryHint) {
        mTvSecondaryHint.setText(tvSecondaryHint);
        return this;
    }

    public CommonTwoDialog mTvLeft(String left) {
        mTvLeft.setText(left);
        return this;
    }

    public CommonTwoDialog mTvRight(String right) {
        mTvRight.setText(right);
        return this;
    }

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_common_two;
    }

    @Override
    public void findViews() {
        mTvMainHint = findView(R.id.common_two_tv_hint_main);
        mTvSecondaryHint = findView(R.id.common_two_tv_hint_secondary);
        mTvLeft = findView(R.id.common_two_tv_left);
        mTvRight = findView(R.id.common_two_tv_right);
    }

    @Override
    public void setViews() {
        setOnClickListener(mTvLeft);
        setOnClickListener(mTvRight);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_two_tv_left:
                dismiss();
                if (mOnLayoutListener != null) {
                    mOnLayoutListener.leftClick(v);
                }
                break;
            case R.id.common_two_tv_right:
                dismiss();
                if (mOnLayoutListener != null) {
                    mOnLayoutListener.rightClick(v);
                }
                break;
        }
    }

    public void hideSecond() {
        mTvSecondaryHint.setVisibility(View.GONE);
    }
}
