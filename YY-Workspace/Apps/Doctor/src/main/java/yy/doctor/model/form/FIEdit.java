package yy.doctor.model.form;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;

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
        return true;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_item_edit;
    }

    @Override
    protected void refresh(FormItemVH holder) {
        super.refresh(holder);

        holder.getEt().setEnabled(getBoolean(TFormElem.enable));
        holder.getEt().setText(getString(TFormElem.text));

        int d = getInt(TFormElem.drawable);
        if (d != Constants.KInvalidValue) {
            holder.getIv().setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                }
            });
        }

        setIvIfValid(holder.getIv(), d);
    }
}
