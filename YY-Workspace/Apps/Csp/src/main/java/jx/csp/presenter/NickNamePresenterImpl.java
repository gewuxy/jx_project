package jx.csp.presenter;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import io.reactivex.annotations.NonNull;
import jx.csp.contact.NickNameContract;
import jx.csp.contact.NickNameContract.V;
import jx.csp.util.Util;
import lib.jx.contract.BasePresenterImpl;

/**
 * @auther HuoXuYu
 * @since 2017/11/22
 */

public class NickNamePresenterImpl extends BasePresenterImpl<NickNameContract.V> implements NickNameContract.P {

    private TextWatcher mWatcher;

    public NickNamePresenterImpl(V v) {
        super(v);
    }

    @Override
    public void onTextChangedListener(@NonNull EditText et) {
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
                getView().buttonStatus();
                getView().setClearButton(String.valueOf(s));

            }
        });

        et.setOnFocusChangeListener((v, hasFocus) -> {
            // iv是否显示
            getView().setClearButton(Util.getEtString(et));
        });
    }
}
