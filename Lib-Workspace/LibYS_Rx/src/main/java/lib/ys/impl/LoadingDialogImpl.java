package lib.ys.impl;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;

import lib.ys.R;
import lib.ys.ui.decor.IProgressView;
import lib.ys.ui.dialog.MatchScreenDialog;


public class LoadingDialogImpl extends MatchScreenDialog implements OnCancelListener, OnDismissListener {

    private IProgressView mProgressView;

    private OnCancelListener mOnCancelListener;
    private OnDismissListener mOnDismissListener;

    public LoadingDialogImpl(Context context) {
        super(context);

        setCanceledOnTouchOutside(false);
        setCancelable(true);
        setDimAmount(0.5f);
    }

    @Override
    public int getContentViewId() {
        return R.layout.dialog_loading;
    }

    @Override
    public void initData() {
    }

    @Override
    public void findViews() {
        mProgressView = findView(R.id.progress_view);
    }

    @Override
    public void setViews() {
        setOnCancelListener(this);
        setOnDismissListener(this);
    }

    @Override
    public void setOnCancelListener(OnCancelListener listener) {
        super.setOnCancelListener(this);
        mOnCancelListener = listener;
    }

    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        super.setOnDismissListener(this);
        mOnDismissListener = listener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mOnDismissListener != this) {
            mOnDismissListener.onDismiss(dialog);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mOnCancelListener != this) {
            mOnCancelListener.onCancel(dialog);
        }
    }

    @Override
    public void show() {
        super.show();
        if (mProgressView != null) {
            mProgressView.start();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mProgressView != null) {
            mProgressView.stop();
        }
    }
}
