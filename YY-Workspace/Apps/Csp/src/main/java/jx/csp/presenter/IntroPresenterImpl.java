package jx.csp.presenter;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import jx.csp.contact.IntroContract;
import jx.csp.contact.IntroContract.V;
import lib.ys.util.TextUtil;
import lib.jx.contract.BasePresenterImpl;

/**
 * @auther Huoxuyu
 * @since 2017/11/22
 */

public class IntroPresenterImpl extends BasePresenterImpl<IntroContract.V> implements IntroContract.P{

    private int KTextLength = 600;

    public IntroPresenterImpl(V v) {
        super(v);
    }

    @Override
    public void onTextChangedListener(EditText et) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtil.isEmpty(s)) {
                    getView().setIntroTextLength(KTextLength);
                } else {
                    getView().setIntroTextLength(KTextLength - s.length());
                }
            }
        });
    }
}
