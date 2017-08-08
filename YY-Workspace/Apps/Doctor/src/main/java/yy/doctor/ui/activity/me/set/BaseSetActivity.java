package yy.doctor.ui.activity.me.set;

import android.support.annotation.CallSuper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import lib.network.model.NetworkResp;
import lib.ys.ui.other.NavBar;
import lib.yy.ui.activity.base.BaseFormActivity;
import yy.doctor.R;
import yy.doctor.network.JsonParser;
import yy.doctor.util.Util;

/**
 * @auther : GuoXuan
 * @since : 2017/7/24
 */
public abstract class BaseSetActivity extends BaseFormActivity implements TextWatcher {

    private TextView mTvSet;

    @CallSuper
    @Override
    public void findViews() {
        super.findViews();

        mTvSet = findView(R.id.activity_tv_set_set);
    }

    @Override
    public final int getContentViewId() {
        return R.layout.activity_set;
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
        setOnClickListener(R.id.activity_tv_set_set);
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public final void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_tv_set_set: {
                toSet();
            }
            break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    protected final void setChanged(boolean enabled) {
        mTvSet.setEnabled(enabled);
    }

    protected abstract CharSequence getNavBarText();

    protected abstract CharSequence getSetText();

    protected abstract void toSet();
}
