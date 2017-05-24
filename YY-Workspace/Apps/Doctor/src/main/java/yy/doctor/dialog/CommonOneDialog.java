package yy.doctor.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import lib.yy.dialog.BaseDialog;
import yy.doctor.R;

/**
 * 单按钮(2行提示)的提示框
 * * 形如: **********************
 *          ******************
 *            ************
 *
 * @author : GuoXuan
 * @since : 2017/5/22
 */

public class CommonOneDialog extends BaseDialog {

    private TextView mTvMainHint;//主提示
    private TextView mTvSecondaryHint;//副提示
    private TextView mTvSure;

    private OnClickListener mOnClickListener;
    private DisposableSubscriber<Long> mSub;

    public CommonOneDialog(Context context) {
        super(context);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public CommonOneDialog setTvMainHint(String tvMainHint) {
        mTvMainHint.setText(tvMainHint);
        return this;
    }

    public CommonOneDialog setTvSecondaryHint(String tvSecondaryHint) {
        mTvSecondaryHint.setText(tvSecondaryHint);
        return this;
    }

    public CommonOneDialog setTvSecondaryColor(int color) {
        mTvSecondaryHint.setTextColor(color);
        return this;
    }

    public CommonOneDialog setTvSecondarySize(int sizePx) {
        mTvSecondaryHint.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizePx);
        return this;
    }

    public CommonOneDialog mTvSureColor(int color) {
        mTvSure.setTextColor(color);
        return this;
    }

    @Override
    public void initData() {

    }

    @NonNull
    @Override
    public int getContentViewId() {
        return R.layout.dialog_common_one;
    }

    @Override
    public void findViews() {
        mTvMainHint = findView(R.id.common_one_tv_hint_main);
        mTvSecondaryHint = findView(R.id.common_one_tv_hint_secondary);
        mTvSure = findView(R.id.common_one_tv_sure);
    }

    @Override
    public void setViews() {
        setOnClickListener(mTvSure);
        setTvSecondary(mTvSecondaryHint);
        setTvMain(mTvMainHint);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common_one_tv_sure:
                dismiss();
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(v);
                }
                break;
        }
    }

    /**
     * 倒数
     *
     * @param second
     */
    public void start(int second) {
        show();
        Flowable.interval(0, 1, TimeUnit.SECONDS)
                .take(second + 1)
                .map(aLong -> second - aLong) // 转换成倒数的时间
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createSub());
    }

    private DisposableSubscriber<Long> createSub() {
        mSub = new DisposableSubscriber<Long>() {

            @Override
            public void onNext(@NonNull Long aLong) {
                close(aLong);
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
            }

            @Override
            public void onComplete() {
                dismiss();
            }
        };
        return mSub;
    }

    public void close(Long aLong) {

    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mSub != null && !mSub.isDisposed()) {
            mSub.dispose();
            mSub = null;
        }
    }

    public void setTvSecondary(TextView tvSecondary) {
    }

    public void setTvMain(TextView tvMain) {
    }
}
