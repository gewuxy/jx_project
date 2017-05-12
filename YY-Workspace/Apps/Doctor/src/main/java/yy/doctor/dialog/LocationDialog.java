package yy.doctor.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import lib.yy.dialog.BaseDialog;
import yy.doctor.R;

/**
 * @author : GuoXuan
 * @since : 2017/5/12
 */

public class LocationDialog extends BaseDialog {

    private OnAgainListener mOnAgainListener;

    public interface OnAgainListener {
        void onAgain(View v);
    }

    public LocationDialog(Context context) {
        super(context);
    }

    public void setOnAgainListener(OnAgainListener onAgainListener) {
        mOnAgainListener = onAgainListener;
    }

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_location;
    }

    @Override
    public void findViews() {

    }

    @Override
    public void setViews() {
        setOnClickListener(R.id.dialog_location_tv_again);
        setOnClickListener(R.id.dialog_location_tv_cancel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_location_tv_again:
                if (mOnAgainListener != null) {
                    mOnAgainListener.onAgain(v);
                }
                dismiss();
                break;
            case R.id.dialog_location_tv_cancel:
                dismiss();
                break;
        }

    }
}
