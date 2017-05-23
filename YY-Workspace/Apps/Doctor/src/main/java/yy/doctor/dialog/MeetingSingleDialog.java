package yy.doctor.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import lib.yy.dialog.BaseDialog;
import yy.doctor.R;

/**
 * 会议中简单(2行,主提示副提示)的提示框
 *
 * @author : GuoXuan
 * @since : 2017/5/22
 */

public class MeetingSingleDialog extends BaseDialog {

    private TextView mTvMainHint;//主提示
    private TextView mTvSecondaryHint;//副提示
    private TextView mTvSure;

    private OnClickListener mOnClickListener;

    public MeetingSingleDialog(Context context) {
        super(context);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public MeetingSingleDialog setTvMainHint(String tvMainHint) {
        mTvMainHint.setText(tvMainHint);
        return this;
    }

    public MeetingSingleDialog setTvSecondaryHint(String tvSecondaryHint) {
        mTvSecondaryHint.setText(tvSecondaryHint);
        return this;
    }

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_meeting_single;
    }

    @Override
    public void findViews() {
        mTvMainHint = findView(R.id.meeting_single_tv_hint_main);
        mTvSecondaryHint = findView(R.id.meeting_single_tv_hint_secondary);
        mTvSure = findView(R.id.meeting_single_tv_sure);
    }

    @Override
    public void setViews() {
        setOnClickListener(mTvSure);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meeting_single_tv_sure:
                dismiss();
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(v);
                }
                break;
            default:
                break;
        }
    }

}
