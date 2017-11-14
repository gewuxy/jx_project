package jx.csp.ui.activity.me.profile;

import android.content.Intent;
import android.os.Bundle;
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
import jx.csp.contact.MyMessageContract;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.presenter.MyMessagePresenterImpl;
import jx.csp.util.Util;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;
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

    public final int KNickNameCode = 0;
    public final int KInfoCode = 1;

    public final int KTextLength = 600;

    public MyMessageContract.P mPresenter;
    public MyMessageContract.V mView;

    @Override
    public void initData(Bundle state) {
        mView = new MyMessageViewImpl();
        mPresenter = new MyMessagePresenterImpl(mView);
    }

    @NonNull
    @Override
    public int getContentViewId() {
        return 0;
    }

    @CallSuper
    @Override
    public void initNavBar(NavBar bar) {
        mView.setNavBar(bar);
    }

    @Override
    public void setViews() {
        mView.setNavBarTextColor();
        mView.getEtData();
        mView.setTextButtonStatus();
    }

    protected void addTextChangedListener(@NonNull EditText et, @Nullable View ivClear) {
        mView.onTextChangedListener(et, ivClear);
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

    protected class MyMessageViewImpl implements MyMessageContract.V {

        @Override
        public void setNavBar(NavBar bar) {
            Util.addBackIcon(bar, R.string.my_message, BaseMyMessageActivity.this);
            mTv = bar.addTextViewRight(R.string.my_message_save, v -> {
                if (mFlag == false) {
                    if (TextUtil.isEmpty(getEt().getText())) {
                        showToast("不可保存为空");
                    } else {
                        refresh(RefreshWay.dialog);
                        doSet();
                    }
                } else {
                    refresh(RefreshWay.dialog);
                    doSet();

                }
            });
        }

        @Override
        public void setNavBarTextColor() {
            mTv.setTextColor(ResLoader.getColor(R.color.text_167afe));
        }

        @Override
        public void setTextButtonStatus() {
            mTv.setEnabled(true);
        }

        @Override
        public void saveRevisedData() {
            String text = Util.getEtString(getEt());
            Profile.inst().put(mAttr, text);
            Profile.inst().saveToSp();

            Intent i = new Intent().putExtra(Extra.KData, text);
            setResult(RESULT_OK, i);
            finish();
            showToast(R.string.my_message_save_success);
        }

        @Override
        public void getEtData() {
            getEt().setText(getVal());
            getEt().setSelection(getEt().getText().length());
        }

        @Override
        public void onTextChangedListener(@NonNull EditText et, @Nullable View ivClear) {
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
                    setTextButtonStatus();

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
        public void setNickNameTextListener(Editable s, EditText et, TextWatcher watcher) {
            String text = s.toString();
            text = text.replaceAll(" ", "");

            et.removeTextChangedListener(watcher);
            et.setText(text);
            et.setSelection(text.length());
            et.addTextChangedListener(watcher);
        }

        @Override
        public void setClear(EditText et) {
            et.setText("");
        }

        @Override
        public void setIntroTextLength(int length, TextView tv) {
            if (length > KTextLength) {
                length = 0;
            }
            tv.setText(String.format(getString(R.string.my_message_intro_unit), length));
        }

        @Override
        public void setIntroChangedTextLength(Editable s, TextView tv) {
            if (TextUtil.isEmpty(s)) {
                setIntroTextLength(KTextLength, tv);
            } else {
                setIntroTextLength(KTextLength - s.length(), tv);
            }
        }

        @Override
        public void onStopRefresh() {
            stopRefresh();
        }

        @Override
        public void setViewState(int state) {

        }
    }
}
