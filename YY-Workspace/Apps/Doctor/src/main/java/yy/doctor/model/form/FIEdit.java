package yy.doctor.model.form;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import lib.yy.adapter.VH.FormItemVH;
import lib.yy.model.form.FormItem;
import yy.doctor.Constants;
import yy.doctor.R;

/**
 * @author CaiXiang
 * @since 2017/4/17
 */
public class FIEdit extends FormItem {

    @NonNull
    @Override
    public int getType() {
        return FormType.et;
    }

    @Override
    public boolean check() {
        return checkInput();
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_item_edit;
    }

    @Override
    protected void init(FormItemVH holder) {
        super.init(holder);

        holder.getEt().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                put(TFormElem.val, s.toString());
            }
        });
    }

    @Override
    protected void refresh(FormItemVH holder) {
        super.refresh(holder);

        holder.getEt().setEnabled(getBoolean(TFormElem.enable));
        //holder.getEt().setText(getString(TFormElem.text));
        holder.getEt().setText(getString(TFormElem.hint));

        int d = getInt(TFormElem.drawable);
        if (d != Constants.KInvalidValue) {
            holder.getIv().setOnClickListener(this);
            setIvIfValid(holder.getIv(), d);
        }
    }

    @Override
    protected boolean onViewClick(View v) {
        return super.onViewClick(v);
    }
}
