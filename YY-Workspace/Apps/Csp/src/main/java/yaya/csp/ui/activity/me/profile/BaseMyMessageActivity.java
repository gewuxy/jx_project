package yaya.csp.ui.activity.me.profile;

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
import lib.ys.ui.other.NavBar;
import lib.ys.util.res.ResLoader;
import lib.yy.network.BaseJsonParser;
import lib.yy.ui.activity.base.BaseActivity;
import yaya.csp.Extra;
import yaya.csp.R;
import yaya.csp.model.Profile;
import yaya.csp.model.Profile.TProfile;
import yaya.csp.util.Util;

/**
 * @auther HuoXuYu
 * @since 2017/9/21
 */

abstract public class BaseMyMessageActivity extends BaseActivity{

    @Arg
    TProfile mAttr;

    private TextView mTV;

    @NonNull
    @Override
    public int getContentViewId() {
        return 0;
    }

    @CallSuper
    @Override
    public void initNavBar(NavBar bar) {
        Util.addBackIcon(bar, R.string.my_message, this);
        mTV = bar.addTextViewRight(R.string.save, v -> {
//            refresh(RefreshWay.dialog);
//            exeNetworkReq(NetFactory.modifyProfile(mAttr.name(), getEt().getText().toString()));
            onModifySuccess();
        });
    }

    @Override
    public void findViews() {

    }

    @Override
    public void setViews() {
        mTV.setTextColor(ResLoader.getColor(R.color.text_167afe));
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
        return BaseJsonParser.error(r.getText());
    }

    @Override
    public void onNetworkSuccess(int id, Object result) {
        super.onNetworkSuccess(id, result);
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
        showToast(R.string.save_success);
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
