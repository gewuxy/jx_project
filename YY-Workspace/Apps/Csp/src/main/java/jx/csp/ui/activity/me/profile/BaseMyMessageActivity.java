package jx.csp.ui.activity.me.profile;

import android.content.Intent;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import inject.annotation.router.Arg;
import lib.network.model.NetworkResp;
import lib.network.model.interfaces.IResult;
import lib.ys.config.AppConfig.RefreshWay;
import lib.ys.ui.other.NavBar;
import lib.ys.util.res.ResLoader;
import lib.yy.ui.activity.base.BaseActivity;
import jx.csp.Extra;
import jx.csp.R;
import jx.csp.model.Profile;
import jx.csp.model.Profile.TProfile;
import jx.csp.network.JsonParser;
import jx.csp.network.NetFactory;
import jx.csp.util.Util;

/**
 * 个人中心基类
 *
 * @auther HuoXuYu
 * @since 2017/9/21
 */

abstract public class BaseMyMessageActivity extends BaseActivity{

    @Arg
    TProfile mAttr;

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
            refresh(RefreshWay.dialog);
            exeNetworkReq(NetFactory.modifyProfile(mAttr.name(), getEt().getText().toString()));
        });
    }

    @Override
    public void setViews() {
        mTv.setTextColor(ResLoader.getColor(R.color.text_167afe));
        getEt().setText(getVal());
        getEt().setSelection(getEt().getText().length());
    }

    protected void addTextChangeListener(@NonNull EditText et){
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