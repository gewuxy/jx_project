package jx.csp.presenter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import jx.csp.contact.NickNameContract;
import jx.csp.contact.NickNameContract.V;
import jx.csp.util.Util;
import lib.ys.util.TextUtil;
import lib.ys.util.view.ViewUtil;
import lib.yy.contract.BasePresenterImpl;

/**
 * fixme : 暂时设置2个监听，合并setText会循环，导致报错（不知道怎么改）
 *
 * @auther Huoxuyu
 * @since 2017/11/22
 */

public class NickNamePresenterImpl extends BasePresenterImpl<NickNameContract.V> implements NickNameContract.P {

    private TextWatcher mWatcher;

    public NickNamePresenterImpl(V v) {
        super(v);
    }

    @Override
    public void onTextChangedListener(@NonNull TextView tv, @NonNull EditText et, @Nullable View iv) {
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
                tv.setEnabled(true);

                if (et.hasFocus() && TextUtil.isNotEmpty(s)) {
                    ViewUtil.showView(iv);
                } else {
                    ViewUtil.hideView(iv);
                }
            }
        });

        et.setOnFocusChangeListener((v, hasFocus) -> {
            // iv是否显示
            if (hasFocus && TextUtil.isNotEmpty(Util.getEtString(et))) {
                ViewUtil.showView(iv);
            } else {
                ViewUtil.hideView(iv);
            }
        });
    }

    @Override
    public void onTextBlankListener(EditText et) {
        mWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                text = text.replaceAll(" ", "");

                et.removeTextChangedListener(mWatcher);
                getView().inhibitInputBlank(et, text);
                et.addTextChangedListener(mWatcher);
            }
        };
        et.addTextChangedListener(mWatcher);
    }
}
