package jx.doctor.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import lib.jx.dialog.BaseDialog;
import jx.doctor.R;

/**
 * 获取定位信息失败提示框
 *
 * @author : GuoXuan
 * @since : 2017/5/12
 */

public class SignErrDialog extends BaseDialog {

    private OnSignAgainListener mOnSignAgainListener;

    public interface OnSignAgainListener {
        /**
         * 重试
         *
         * @param v
         */
        void onAgain(View v);
    }

    public SignErrDialog(Context context) {
        super(context);
    }

    public void setLocationListener(OnSignAgainListener onLocationListener) {
        mOnSignAgainListener = onLocationListener;
    }

    @Override
    public void initData(Bundle state) {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_sign_err;
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
            case R.id.dialog_location_tv_again: {
                if (mOnSignAgainListener != null) {
                    mOnSignAgainListener.onAgain(v);
                }
            }
            break;
        }
        dismiss();
    }

}
