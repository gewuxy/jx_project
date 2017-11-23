package yy.doctor.ui.activity.me.set;

import android.support.annotation.CallSuper;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.ConstantsEx;
import lib.ys.ui.other.NavBar;
import lib.yy.model.form.BaseForm;
import lib.yy.ui.activity.base.BaseFormActivity;
import yy.doctor.R;
import yy.doctor.network.JsonParser;
import yy.doctor.util.Util;

/**
 * @auther : GuoXuan
 * @since : 2017/7/24
 */
abstract public class BaseSetActivity extends BaseFormActivity implements TextWatcher {

    private TextView mTvSet;

    @CallSuper
    @Override
    public void findViews() {
        super.findViews();

        mTvSet = findView(R.id.base_set_tv_set);
    }

    @Override
    public final int getContentViewId() {
        return R.layout.activity_base_set;
    }

    @Override
    public final void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, getNavBarText(), this);
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
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.error(resp.getText());
    }

    @Override
    public final void onClick(View v) {
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

    public String getRelatedString(Object related) {
        BaseForm form = getRelatedItem(related);
        if (form != null) {
            return form.getVal();
        } else {
            return ConstantsEx.KEmpty;
        }
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
}
