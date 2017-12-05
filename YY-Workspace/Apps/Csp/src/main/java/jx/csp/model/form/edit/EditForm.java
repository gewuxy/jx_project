package jx.csp.model.form.edit;

import android.os.Build.VERSION_CODES;
import android.support.annotation.CallSuper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jx.csp.R;
import jx.csp.util.Util;
import lib.jx.adapter.VH.FormVH;
import lib.jx.model.form.BaseForm;
import lib.ys.ConstantsEx;
import lib.ys.util.DeviceUtil;
import lib.ys.util.TextUtil;
import lib.ys.util.view.ViewUtil;


/**
 * @author CaiXiang
 * @since 2017/4/17
 */
public class EditForm extends BaseForm implements TextWatcher {

    @Override
    public boolean check() {
        return checkInput();
    }

    @Override
    public int getContentViewResId() {
        int layout = getLayoutId();
        if (layout != ConstantsEx.KInvalidValue) {
            return layout;
        }
        return R.layout.form_edit;
    }

    @Override
    protected void init(FormVH holder) {
        EditText et = holder.getEt();
        et.addTextChangedListener(this);
        // 设置
        View clean = getHolder().getIvClean();
        if (clean != null) {
            setOnClickListener(clean);

            et.setOnFocusChangeListener((v, hasFocus) -> {
                // iv是否显示
                if (hasFocus && TextUtil.isNotEmpty(Util.getEtString(et))) {
                    ViewUtil.showView(clean);
                } else {
                    ViewUtil.goneView(clean);
                }
            });
        }

        // 5.0以上的版本设置水波纹点击背景
        if (DeviceUtil.getSDKVersion() >= VERSION_CODES.LOLLIPOP) {
            if (holder.getIv() != null) {
                holder.getIv().setBackgroundResource(R.drawable.item_selector_unbound);
            }
        }

        // 限制长度
        if (getLimit() != ConstantsEx.KInvalidValue) {
            ViewUtil.limitInputCount(holder.getEt(), getLimit());
        }

        // 限制输入的类型
        InputFilter[] inputFilter = getInputFilter();
        if (inputFilter != null && inputFilter.length > 0) {

            // 会覆盖getLimit()用法,要添加到输入类型中
            if (getLimit() != ConstantsEx.KInvalidValue) {

                // 不能直接添加否则UnsupportedOperationException
                List<InputFilter> newFilters = Arrays.asList(inputFilter);

                List<InputFilter> f = new ArrayList(newFilters);
                f.add(new LengthFilter(getLimit()));

                InputFilter[] newInputFilter = f.toArray(new InputFilter[f.size()]);

                et.setFilters(newInputFilter);
            } else {
                et.setFilters(inputFilter);
            }

        }
        TextWatcher textWatcher = getTextWatcher();
        if (textWatcher != null) {
            et.addTextChangedListener(textWatcher);
        }
    }

    @Override
    protected void refresh(FormVH holder) {
        super.refresh(holder);

        int d = getDrawable();
        ImageView iv = holder.getIv();
        if (iv != null) {
            if (d != ConstantsEx.KInvalidValue) {
                iv.setOnClickListener(this);
            }
            setIvIfValid(iv, d);
        }

        EditText et = holder.getEt();
        //  et.setEnabled(isEnabled());
        et.setText(getText());
        et.setHint(getHint());
    }

    @CallSuper
    @Override
    protected boolean onViewClick(View v) {
        int id = v.getId();
        if (id == R.id.form_iv_clean) {
            getHolder().getEt().setText("");
            return true;
        }
        return super.onViewClick(v);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        save(s.toString(), s.toString());

        View clean = getHolder().getIvClean();
        if (clean != null) {
            if (TextUtil.isNotEmpty(s)) {
                ViewUtil.showView(clean);
            } else {
                ViewUtil.goneView(clean);
            }
        }
    }
}
