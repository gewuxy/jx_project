package jx.csp.ui.activity.login;

import android.support.annotation.CallSuper;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import jx.csp.R;
import jx.csp.model.Profile;
import jx.csp.network.JsonParser;
import jx.csp.util.Util;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseFormActivity;

/**
 * @auther : GuoXuan
 * @since : 2017/7/24
 */
abstract public class BaseLoginActivity extends BaseFormActivity implements TextWatcher {

    private TextView mTvSet;

    @Override
    public final int getContentViewId() {
        return R.layout.activity_base_login;
    }

    @Override
    public final void initNavBar(NavBar bar) {
        Util.addCloseIcon(bar, getNavBarText(), this);
    }

    @CallSuper
    @Override
    public void findViews() {
        super.findViews();

        mTvSet = findView(R.id.base_set_tv_set);
    }

    @CallSuper
    @Override
    public void setViews() {
        super.setViews();

        mTvSet.setEnabled(false);
        mTvSet.setText(getSetText());
        setOnClickListener(R.id.base_set_tv_set);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.base_set_tv_set: {
                toSet();
            }
            break;
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
    }

    /**
     * 获取标题文本
     *
     * @return 标题的文本
     */
    abstract protected CharSequence getNavBarText();

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

}
