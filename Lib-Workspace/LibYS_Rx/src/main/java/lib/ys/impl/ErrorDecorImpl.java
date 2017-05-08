package lib.ys.impl;

import android.content.Context;
import android.util.AttributeSet;

import lib.ys.R;
import lib.ys.ui.decor.ErrorDecorEx;


public class ErrorDecorImpl extends ErrorDecorEx {

    public ErrorDecorImpl(Context context) {
        super(context);
    }

    public ErrorDecorImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getContentViewId() {
        return R.layout.layout_net_error;
    }

    @Override
    public void findViews() {
    }

    @Override
    public void setViews() {
        setOnRetryClick(this);
    }

}
