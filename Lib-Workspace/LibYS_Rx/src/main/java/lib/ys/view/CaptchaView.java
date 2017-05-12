package lib.ys.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

/**
 * 验证码的view
 *
 * @author yuansui
 */
public class CaptchaView extends TextView {

    private static final String KDefaultResendText = "重新获取";
    private static final String KPrefix = "(";
    private static final String KSuffix = "s)";

    private static final int KMaxCount = 60;

    private String mResendText = KDefaultResendText;

    private int mMaxCount = KMaxCount;

    private DisposableObserver<Long> mDisposable;

    public CaptchaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }

        mDisposable = new DisposableObserver<Long>() {

            @Override
            public void onNext(@NonNull Long aLong) {
                setText(mResendText + KPrefix + (mMaxCount - aLong) + KSuffix);
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
    }

    public void start() {
        setEnabled(false);

        Observable.intervalRange(0, mMaxCount, 0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mDisposable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
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
