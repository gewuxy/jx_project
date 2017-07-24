package yy.doctor.model.form.edit;

import android.support.annotation.NonNull;
import android.text.InputFilter;

import lib.yy.adapter.VH.FormVH;
import yy.doctor.R;
import yy.doctor.model.form.FormType;

/**
 * @author CaiXiang
 * @since 2017/4/17
 */
public class EditRegisterForm extends EditForm {

    @NonNull
    @Override
    public int getType() {
        return FormType.et_register;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_edit_register;
    }

    @Override
    protected void init(FormVH holder) {
        super.init(holder);
       /* InputFilter filter = (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                if (!UISetter.isChinese(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        };
        holder.getEt().setFilters(new InputFilter[]{filter});*/
        holder.getEt().setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)});//姓名限制18位
    }
}
