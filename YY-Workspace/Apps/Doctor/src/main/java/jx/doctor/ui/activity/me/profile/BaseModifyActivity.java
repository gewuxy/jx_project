package jx.doctor.ui.activity.me.profile;

import android.content.Intent;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import inject.annotation.router.Arg;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.jx.ui.activity.base.BaseActivity;
import jx.doctor.Extra;
import jx.doctor.R;
import jx.doctor.model.Profile;
import jx.doctor.model.Profile.TProfile;
import jx.doctor.network.JsonParser;
import jx.doctor.network.NetFactory;
import jx.doctor.util.Util;

/**
 * @auther HuoXuYu
 * @since 2017/7/24
 */
abstract public class BaseModifyActivity extends BaseActivity {

    @Arg
    TProfile mAttr;
    @Arg
    @StringRes
    int mTitleId;
    @Arg
    @StringRes
    int mHint;


    @CallSuper
    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, getString(mTitleId), this);

        bar.addTextViewRight(R.string.save, v -> {
            refresh(RefreshWay.dialog);
            doModify();
        });
    }

    @CallSuper
    @Override
    public void setViews() {
        getEt().setHint(mHint);
        getEt().setText(getVal());
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
                if (s.toString().contains(" ")) {
                    String[] str = s.toString().split(" ");
                    String str1 = "";
                    for (int i = 0; i < str.length; i++) {
                        str1 += str[i];
                    }
                    et.setText(str1);
                    et.setSelection(start);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (ivClear == null) {
                    return;
                }
                if (et.hasFocus() && TextUtil.isNotEmpty(s)) {
                    showView(ivClear);
                } else {
                    hideView(ivClear);
                }
            }
        });

        et.setOnFocusChangeListener((v, hasFocus) -> {
            if (ivClear == null) {
                return;
            }
            // iv是否显示
            if (hasFocus && TextUtil.isNotEmpty(Util.getEtString(et))) {
                showView(ivClear);
            } else {
                hideView(ivClear);
            }
        });
    }

    @Override
    public IResult onNetworkResponse(int id, NetworkResp resp) throws Exception {
        return JsonParser.error(resp.getText());
    }

    @Override
    public void onNetworkSuccess(int id, IResult r) {
        if (r.isSucceed()) {
            stopRefresh();
            onModifySuccess();
        } else {
            onNetworkError(id, r.getError());
        }
    }

    protected void doModify() {
        refresh(RefreshWay.dialog);
        exeNetworkReq(NetFactory.modifyProfile(mAttr.name(), getEt().getText().toString()));
    }

    protected void onModifySuccess() {
        String text = Util.getEtString(getEt());
        Profile.inst().put(mAttr, text);
        Profile.inst().saveToSp();

        Intent i = new Intent().putExtra(Extra.KData, text);
        setResult(RESULT_OK, i);
        finish();
        showToast(R.string.user_save_success);
    }

    abstract protected EditText getEt();

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
