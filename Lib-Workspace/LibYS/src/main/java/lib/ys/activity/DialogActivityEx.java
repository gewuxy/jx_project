package lib.ys.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import lib.ys.R;
import lib.ys.ex.NavBar;
import lib.ys.util.UIUtil;

/**
 * 对话框模板
 *
 * @author yuansui
 */
abstract public class DialogActivityEx extends ActivityEx {

    private OnClickListener mFinishLsn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setAttrs();

        // setCanceledOnTouchOutside();
        if (bgDimEnabled()) {
            setTheme(R.style.dialog_dim_enable);
            setDimAmount(getDimAmount());
        } else {
            setTheme(R.style.dialog_dim_unable);
            setDimAmount(0);
        }
    }

    protected float getDimAmount() {
        return 0.5f;
    }

    /**
     * 设置背景色, 弹出动画和是否全屏等
     */
    protected void setAttrs() {
        setBackgroundColor(Color.TRANSPARENT);
        UIUtil.setWindowToFullScreen(this);
    }

    @Override
    public void initNavBar(NavBar bar) {
    }

    @Override
    protected void startInAnim() {
        startActSwitchAnim(R.anim.no_effct, R.anim.no_effct);
    }

    @Override
    protected void startOutAnim() {
        startActSwitchAnim(R.anim.no_effct, R.anim.no_effct);
    }

    /**
     * 设置点击可以关闭dialog的v
     *
     * @param v
     */
    protected void setFinishClicker(View v) {
        if (v == null) {
            return;
        }

        if (mFinishLsn == null) {
            mFinishLsn = v1 -> finish();
        }

        v.setOnClickListener(mFinishLsn);
    }

    protected void setFinishClicker(int viewId) {
        if (mFinishLsn == null) {
            mFinishLsn = v -> finish();
        }

        View v = findView(viewId);
        if (v != null) {
            v.setOnClickListener(mFinishLsn);
        }
    }

    protected void setCanceledOnTouchOutside() {
        setFinishClicker(getDecorView());
    }

    /**
     * 是否允许黑背景
     *
     * @return
     */
    protected boolean bgDimEnabled() {
        return true;
    }

    /**
     * 设置空白处黑暗度
     *
     * @param amount 0-1.0, 0为全透明. 1为全黑
     */
    protected void setDimAmount(float amount) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.dimAmount = amount;
        getWindow().setAttributes(params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @Override
    protected final Boolean enableSwipeFinish() {
        return false;
    }
}
