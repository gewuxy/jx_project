package jx.csp.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import jx.csp.R;
import lib.jx.dialog.BaseDialog;

/**
 * 功能指引
 *
 * @auther HuoXuYu
 * @since 2018/2/3
 */

public class FunctionGuideDialog extends BaseDialog {

    private View mLayout1;
    private View mLayout2;
    private View mLayout3;
    private ImageView mIv;

    public FunctionGuideDialog(Context context) {
        super(context);
    }

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_function_guide;
    }

    @Override
    public void findViews() {
        mLayout1 = findView(R.id.function_1);
        mLayout2 = findView(R.id.function_2);
        mLayout3 = findView(R.id.function_3);
        mIv = findView(R.id.function_iv_experience);
    }

    @Override
    public void setViews() {
        setOnClickListener(mLayout1);
        setOnClickListener(mLayout2);
        setOnClickListener(mIv);

        setGravity(Gravity.BOTTOM);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.function_iv_experience: {
                setCanceledOnTouchOutside(false);
                dismiss();
            }
            break;
            case R.id.function_1: {
                goneView(mLayout1);
                showView(mLayout2);
            }
            break;
            case R.id.function_2: {
                goneView(mLayout2);
                showView(mLayout3);
            }
            break;
        }
    }
}
