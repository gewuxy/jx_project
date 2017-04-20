package lib.ys.view;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.TextView;

import lib.ys.util.MilliUtil;

/**
 * 验证码的view
 *
 * @author yuansui
 */
public class CaptchaView extends TextView {

    private static final String KDefaultResendText = "重新获取";
    private static final int KMaxCount = 60;

    private String mResendText = KDefaultResendText;

    private int mMaxCount = KMaxCount;

    private CountDownTimer mCountDownTimer;

    public CaptchaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }

        mCountDownTimer = new CountDownTimer(MilliUtil.second(mMaxCount), MilliUtil.second(1)) {

            @Override
            public void onTick(long millisUntilFinished) {
                setText(mResendText + "(" + MilliUtil.toSecond(millisUntilFinished) + "s)");
            }

            @Override
            public void onFinish() {
                setEnabled(true);
                setText(mResendText);
            }
        };
    }

    public void start() {
        setEnabled(false);
        mCountDownTimer.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mCountDownTimer.cancel();
        mCountDownTimer = null;
    }

    /**
     * 设置等待状态显示的文字
     *
     * @param text
     */
    public void setResendText(String text) {
        mResendText = text;
    }

    public void setResendText(@StringRes int resId) {
        mResendText = getContext().getResources().getString(resId);
    }

    /**
     * 设置最大倒数次数
     *
     * @param count
     */
    public void setMaxCount(int count) {
        mMaxCount = count;
    }
}
