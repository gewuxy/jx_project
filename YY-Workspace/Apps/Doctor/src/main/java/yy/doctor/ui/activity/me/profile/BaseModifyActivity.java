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
import yy.doctor.network.NetFactory;
import yy.doctor.util.Util;

/**
 * @auther HuoXuYu
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
        getEt().setHint("请输入" + getIntent().getStringExtra(Extra.KTitle));
        getEt().setText(getVal());
        if (TextUtil.isEmpty(getEt().getText())) {
            mTv.setEnabled(false);
            mTv.setTextColor(ResLoader.getColor(R.color.text_b3));
        } else {
            mTv.setEnabled(true);
            mTv.setTextColor(ResLoader.getColor(R.color.white));
        }
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

                } else {
                    mTv.setEnabled(true);
                    mTv.setTextColor(ResLoader.getColor(R.color.white));

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
        IResult r = (IResult) result;
        if (r.isSucceed()) {
            stopRefresh();
            onModifySuccess();
        } else {
            onNetworkError(id, new ParseError(r.getError()));
        }
    }

    protected void doModify() {
        refresh(RefreshWay.dialog);
        exeNetworkReq(NetFactory.modifyProfile(mEnum.name(), getEt().getText().toString()));
    }

    protected void onModifySuccess() {
        String text = Util.getEtString(getEt());
        Profile.inst().put(mEnum, text);
        Profile.inst().saveToSp();

        Intent i = new Intent().putExtra(Extra.KData, text);
        setResult(RESULT_OK, i);
        finish();
    }

    abstract protected EditText getEt();

    /**
     * 获取Profile里面对应enum的值
     *
     * @return
     */
    @NonNull
    protected String getVal() {
        return Profile.inst().getString(mEnum);
    }
}