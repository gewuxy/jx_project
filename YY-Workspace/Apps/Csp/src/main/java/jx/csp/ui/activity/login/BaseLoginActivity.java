package jx.csp.ui.activity.login;

import android.graphics.Rect;
import android.support.annotation.CallSuper;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jx.csp.R;
import jx.csp.constant.Constants;
import jx.csp.model.Profile;
import jx.csp.network.JsonParser;
import jx.csp.util.UISetter;
import lib.jx.model.form.BaseForm;
import lib.jx.ui.activity.base.BaseFormActivity;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.ConstantsEx;
import lib.ys.fitter.Fitter;
import lib.ys.ui.decor.DecorViewEx.TNavBarState;
import lib.ys.ui.other.NavBar;

/**
 * @auther : GuoXuan
 * @since : 2017/7/24
 */
abstract public class BaseLoginActivity extends BaseFormActivity implements TextWatcher {

    private TextView mTvSet;
    protected TextView mTvWelcome;

    private View mLayout;

    @Override
    public int getContentViewId() {
        return R.layout.activity_base_login;
    }

    @Override
    public void initNavBar(NavBar bar) {
        bar.setBackgroundResource(R.color.transparent);
        bar.addViewRight(R.drawable.default_ic_close, v -> finish());
    }

    @Override
    protected TNavBarState getNavBarState() {
        return TNavBarState.above;
    }

    @CallSuper
    @Override
    public void findViews() {
        super.findViews();

        mLayout = findView(R.id.layout_btn);
        mTvSet = findView(R.id.base_set_tv_set);
        mTvWelcome = findView(R.id.login_tv_welcome);
    }

    @CallSuper
    @Override
    public void setViews() {
        super.setViews();

        mTvSet.setEnabled(false);
        mTvSet.setText(getSetText());
        setOnClickListener(R.id.base_set_tv_set);
    }

    @CallSuper
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.base_set_tv_set: {
                toSet();
            }
            break;
        }
    }

    @Override
    public boolean interceptNetSuccess(int id, IResult r) {
        if (r.getCode() == Constants.KAccountFrozen) {
            stopRefresh();
            UISetter.showFrozenDialog(r.getMessage(), BaseLoginActivity.this);
            return true;
        } else {
            return false;
        }
    }

    @Deprecated
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Deprecated
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    /**
     * 设置按钮状态
     *
     * @param enabled true可以点击, false不可点击
     */
    protected final void setChanged(boolean enabled) {
        mTvSet.setEnabled(enabled);
        mTvSet.setSelected(enabled);
    }

    /**
     * 获取按钮文本
     *
     * @return 按钮的文本
     */
    abstract protected CharSequence getSetText();

    /**
     * 点击按钮的操作
     */
    abstract protected void toSet();

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.ev(resp.getText(), Profile.class);
    }

    public String getRelatedString(Object related) {
        BaseForm form = getRelatedItem(related);
        if (form != null) {
            return form.getVal();
        } else {
            return ConstantsEx.KEmpty;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;

        mLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            mLayout.getWindowVisibleDisplayFrame(r);
            int keyboardHeight = screenHeight - r.bottom;
            //界面高度高于150视为键盘弹起
            if (keyboardHeight >= 150) {
                //键盘弹起
                changeLocation(40);
            } else {
                //键盘收起
                changeLocation(110);
            }
        });
    }

    protected void changeLocation(int topDp) {
        ViewGroup.LayoutParams params = mLayout.getLayoutParams();
        if (params instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) params;
            p.topMargin = Fitter.dp(topDp);
            mLayout.setLayoutParams(params);
        } else if (params instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) params;
            p.topMargin = Fitter.dp(topDp);
            mLayout.setLayoutParams(params);
        }
    }
}
