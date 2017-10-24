package jx.csp.ui.activity.me.profile;

import android.content.Intent;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import inject.annotation.router.Arg;
import jx.csp.Extra;
import jx.csp.R;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.network.JsonParser;
import jx.csp.util.Util;
import lib.network.model.NetworkResp;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.network.Result;
import lib.yy.ui.activity.base.BaseActivity;

/**
 * 个人中心基类
 *
 * @auther HuoXuYu
 * @since 2017/9/21
 */

abstract public class BaseMyMessageActivity extends BaseActivity {

    @Arg
    TProfile mAttr;
    @Arg
    boolean mFlag;

    private TextView mTv;

    @NonNull
    @Override
    public int getContentViewId() {
        return 0;
    }

    @CallSuper
    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.my_message, this);
        mTv = bar.addTextViewRight(R.string.my_message_save, v -> {
            doSet();
        });
    }

    @Override
    public void setViews() {
        mTv.setTextColor(ResLoader.getColor(R.color.text_167afe));
        getEt().setText(getVal());
        getEt().setSelection(getEt().getText().length());

        if (mFlag == false) {
            if (TextUtil.isEmpty(getEt().getText())) {
                mTv.setEnabled(false);
            }
        } else {
            mTv.setEnabled(true);
        }
    }

    protected void addTextChangedListener(@NonNull EditText et, @Nullable View ivClear) {
        if (et == null) {
            return;
        }

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mFlag == false) {
                    if (TextUtil.isEmpty(getEt().getText())) {
                        mTv.setEnabled(false);
                    } else {
                        mTv.setEnabled(true);
                    }
                } else {
                    mTv.setEnabled(true);
                }

                if (et.hasFocus() && TextUtil.isNotEmpty(s)) {
                    showView(ivClear);
                } else {
                    hideView(ivClear);
                }
            }
        });

        et.setOnFocusChangeListener((v, hasFocus) -> {
            // iv是否显示
            if (hasFocus && TextUtil.isNotEmpty(Util.getEtString(et))) {
                showView(ivClear);
            } else {
                hideView(ivClear);
            }
        });
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        Result r = (Result) result;
        stopRefresh();
        if (r.isSucceed()) {
            onModifySuccess();
        } else {
            onNetworkError(id, r.getError());
        }
    }

    protected void onModifySuccess() {
        String text = Util.getEtString(getEt());
        Profile.inst().put(mAttr, text);
        Profile.inst().saveToSp();

        Intent i = new Intent().putExtra(Extra.KData, text);
        setResult(RESULT_OK, i);
        finish();
        showToast(R.string.my_message_save_success);
    }

    /**
     * 获取内容
     *
     * @return
     */
    abstract protected EditText getEt();

    /**
     * 点击按钮的操作
     */
    abstract protected void doSet();

    /**
     * 获取Profile里面对应enum的值
     *
     * @return
     */
    @NonNull
    protected String getVal() {
        return Profile.inst().getString(mAttr);
    }
}
