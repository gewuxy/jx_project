package jx.csp.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

import jx.csp.R;
import lib.jx.dialog.BaseDialog;

/**
 * 新手指引
 *
 * @auther HuoXuYu
 * @since 2018/2/3
 */

public class GuideDialog extends BaseDialog {

    private OnDialogWatchListener mListener;

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
                mListener.startActForResult();
                dismiss();
            }
            break;
        }
    }

    public void setWatchListener(OnDialogWatchListener l) {
        mListener = l;
    }

    public interface OnDialogWatchListener {
        void startActForResult();
    }
}
