package lib.ys.inst;

import android.content.Context;
import android.util.AttributeSet;

import lib.ys.R;
import lib.ys.decor.ErrorDecorEx;


public class ErrorDecorInst extends ErrorDecorEx {

    public ErrorDecorInst(Context context) {
        super(context);
    }

    public ErrorDecorInst(Context context, AttributeSet attrs) {
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
