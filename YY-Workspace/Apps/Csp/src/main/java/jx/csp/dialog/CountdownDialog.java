package jx.csp.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.TextView;

import jx.csp.R;
import lib.ys.YSLog;
import lib.ys.view.ProgressView;
import lib.yy.dialog.BaseDialog;
import lib.yy.notify.LiveNotifier;
import lib.yy.notify.LiveNotifier.LiveNotifyType;
import lib.yy.util.CountDown;
import lib.yy.util.CountDown.OnCountDownListener;

/**
 * @auther yuansui
 * @since 2017/11/1
 */

public class CountdownDialog extends BaseDialog implements OnCountDownListener {

    private TextView mTvSecond;
    private ProgressView mProgressView;
    private CountDown mCountDown;
    private int mCountDownNum;

    public CountdownDialog(Context context, int num) {
        super(context);
        mCountDownNum = num;
    }

    @Override
    public void initData(Bundle state) {
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_count_down;
    }

    @Override
    public void findViews() {
        mTvSecond = findView(R.id.dialog_count_down_tv_second);
        mProgressView = findView(R.id.dialog_count_down_progress_bar);
    }

    @Override
    public void setViews() {
        mCountDown = new CountDown();
        mCountDown.setListener(this);
        setGravity(Gravity.CENTER);
    }

    @Override
    public void onCountDown(long remainCount) {
        if (remainCount == 0) {
            YSLog.d(TAG, "15秒倒计时结束, 发同意进入指令");
            // 倒计时结束前没有收到指令，默认同意进入会议
            LiveNotifier.inst().notify(LiveNotifyType.accept);
            dismiss();
        }
        // 不需要显示倒计时
        // mTvSecond.setText(remainCount + "s");
    }

    @Override
    public void onCountDownErr() {
    }

    @Override
    public void show() {
        super.show();

        mCountDown.start(mCountDownNum);
        mProgressView.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();

        mProgressView.stop();
        mCountDown.stop();
    }
}
