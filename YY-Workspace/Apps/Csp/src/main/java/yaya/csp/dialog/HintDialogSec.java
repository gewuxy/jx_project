package yaya.csp.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.TextView;

import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;
import yaya.csp.R;

/**
 * 带计时双提示的提示框
 *
 * @author : GuoXuan
 * @since : 2017/5/22
 */
public class HintDialogSec extends HintDialog implements OnCountDownListener {

    private TextView mTvMainHint; // 主提示
    private TextView mTvSecHint; // 副提示
    private CountDown mCountDown; // 倒计时
    private String mCountHint; // 倒计时的提示语

    public HintDialogSec(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_hint_sec;
    }

    @Override
    public void setViews() {
        super.setViews();

        mTvMainHint = findView(R.id.dialog_sec_tv_main);
        mTvSecHint = findView(R.id.dialog_sec_tv_sec);
    }

    public void setMainHint(String hintMain) {
        mTvMainHint.setText(hintMain);
    }

    public void setMainHint(@StringRes int strRes) {
        mTvMainHint.setText(getContext().getString(strRes));
    }

    public void setSecHint(String hintSec) {
        mTvSecHint.setText(hintSec);
    }

    public void setSecHint(@StringRes int strRes) {
        mTvSecHint.setText(getContext().getString(strRes));
    }

    public void setCountHint(String hint) {
        mCountHint = hint;
    }

    public void setCountHint(@StringRes int strRes) {
        mCountHint = getContext().getResources().getString(strRes);
    }

    public void start(long time) {
        mCountDown = new CountDown(time);
        mCountDown.setListener(this);
        mCountDown.start();
    }

    @Override
    public void onCountDown(long remainCount) {
        mTvSecHint.setText(remainCount + mCountHint);
        if (remainCount == 0) {
            dismiss();
        }
    }

    @Override
    public void onCountDownErr() {
    }

    @Override
    public void dismiss() {
        super.dismiss();

        if (mCountDown != null) {
            mCountDown.stop();
        }
    }

}
