package lib.ys.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * 验证码的view
 *
 * @author yuansui
 */
public class CaptchaView extends TextView {

    private static final String KDefaultResendText = "重新获取";
    private static final String KPrefix = "(";
    private static final String KSuffix = "s)";
    private static final String KSecond = "秒";

    private static final int KMaxCount = 60;

    private String mResendText = KDefaultResendText;

    private int mMaxCount = KMaxCount;

    private DisposableSubscriber<Long> mSub;

    public CaptchaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }
    }

    public void start() {
        dispose();

        Flowable.interval(1, TimeUnit.SECONDS)
                .take(mMaxCount + 1) // 多一秒
                .map(aLong -> mMaxCount - aLong) // 转换成倒数的时间
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> setEnabled(false)) // 开始计时后不可点击
                .subscribe(createSub());
    }

    private DisposableSubscriber<Long> createSub() {
        mSub = new DisposableSubscriber<Long>() {

            @Override
            public void onNext(@NonNull Long aLong) {
                //setText(mResendText + KPrefix + aLong + KSuffix);
                setText( aLong + KSecond);
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
            }

            @Override
            public void onComplete() {
                setEnabled(true);
                setText(mResendText);
            }
        };

        return mSub;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        dispose();
    }

    private void dispose() {
        if (mSub != null && !mSub.isDisposed()) {
            mSub.dispose();
            mSub = null;
        }
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
