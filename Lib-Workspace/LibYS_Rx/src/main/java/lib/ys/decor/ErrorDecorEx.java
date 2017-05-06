package lib.ys.decor;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import lib.ys.AppEx;
import lib.ys.config.AppConfig;
import lib.ys.ex.NavBar;
import lib.ys.interfaces.IInitialize;
import lib.ys.interfaces.OnRetryClickListener;
import lib.ys.util.res.ResLoader;
import lib.ys.util.view.LayoutUtil;

abstract public class ErrorDecorEx extends RelativeLayout implements IInitialize {

    private OnRetryClickListener mOnRetryClickListener;

    public ErrorDecorEx(Context context) {
        super(context);
        init();
    }

    public ErrorDecorEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        /**
         * 背景色
         */
        AppConfig c = AppEx.getConfig();
        if (c.getBgRes() != 0) {
            setBackgroundResource(c.getBgRes());
        } else {
            if (c.getBgColorRes() != 0) {
                setBackgroundColor(ResLoader.getColor(c.getBgColorRes()));
            }
        }

        View v = LayoutInflater.from(getContext()).inflate(getContentViewId(), null);
        LayoutParams params = LayoutUtil.getRelativeParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(v, params);

        initData();
        findViews();
        setViews();
    }

    @Nullable
    @Override
    public int getContentHeaderViewId() {
        return 0;
    }

    @Nullable
    @Override
    public int getContentFooterViewId() {
        return 0;
    }

    protected void setOnRetryClick(View v) {
        if (v == null) {
            return;
        }

        v.setOnClickListener(v1 -> {
            if (mOnRetryClickListener != null) {
                mOnRetryClickListener.onRetryClick();
            }
        });
    }

    protected void setOnRetryClick(@IdRes int id) {
        View v = findViewById(id);
        if (v == null) {
            return;
        }

        v.setOnClickListener(v1 -> {
            if (mOnRetryClickListener != null) {
                mOnRetryClickListener.onRetryClick();
            }
        });
    }

    public void setOnRetryClickListener(OnRetryClickListener listener) {
        mOnRetryClickListener = listener;
    }

    @Override
    public void initData() {
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

}
