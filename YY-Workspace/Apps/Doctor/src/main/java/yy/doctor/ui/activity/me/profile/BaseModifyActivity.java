package yy.doctor.ui.activity.me.profile;

import android.content.Intent;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import lib.network.model.NetworkResp;
import lib.network.model.err.ParseError;
import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.TextUtil;
import lib.ys.util.res.ResLoader;
import lib.yy.ui.activity.base.BaseActivity;
import yy.doctor.Extra;
import yy.doctor.R;
import yy.doctor.model.Profile;
import yy.doctor.model.Profile.TProfile;
import yy.doctor.network.JsonParser;
import yy.doctor.util.Util;

/**
 * @auther Huoxuyu
 * @since 2017/7/24
 */

abstract public class BaseModifyActivity extends BaseActivity {

    private TProfile mEnum;
    private TextView mTv;

    @CallSuper
    @Override
    public void initData() {
        mEnum = (TProfile) getIntent().getSerializableExtra(Extra.KData);
        if (mEnum == null) {
            throw new NullPointerException("tProfile can not be null");
        }
    }

    @CallSuper
    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, getIntent().getStringExtra(Extra.KTitle), this);

        mTv = bar.addTextViewRight(R.string.save, v -> {
            refresh(RefreshWay.dialog);
            doModify();
        });
    }

    @CallSuper
    @Override
    public void setViews() {
        mTv.setEnabled(false);
        mTv.setTextColor(ResLoader.getColor(R.color.text_b3));

        getEt().setText(Profile.inst().getString(mEnum));
    }

    protected void addTextChangedListener(@NonNull EditText et, @NonNull View ivClear) {
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
                if (TextUtil.isEmpty(et.getText())) {
                    mTv.setEnabled(false);
                    mTv.setTextColor(ResLoader.getColor(R.color.text_b3));

                    if (ivClear != null) {
                        hideView(ivClear);
                    }
                }else {
                    mTv.setEnabled(true);
                    mTv.setTextColor(ResLoader.getColor(R.color.white));

                    if (ivClear != null) {
                        showView(ivClear);
                    }
                }
            }
        });
    }

    @Override
    public Object onNetworkResponse(int id, NetworkResp r) throws Exception {
        return JsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        IResult r = (IResult) result;
        if (r.isSucceed()) {
            stopRefresh();
            onModifySuccess();
        } else {
            onNetworkError(id, new ParseError());
        }
    }

    abstract protected void doModify();

    protected void onModifySuccess() {
        String text = getEt().getText().toString().trim();
        Profile.inst().put(mEnum, text);
        Profile.inst().saveToSp();

        Intent i = new Intent().putExtra(Extra.KData, text);
        setResult(RESULT_OK, i);
        finish();
    }

    abstract protected EditText getEt();
}
