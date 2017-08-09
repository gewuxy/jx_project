package yy.doctor.model.form.edit;

import android.text.InputFilter;
import android.widget.EditText;

import lib.yy.adapter.VH.FormVH;
import yy.doctor.util.input.InputSpaceFilter;

/**
 * @auther WangLan
 * @since 2017/8/8
 */

public class EditRegisterDepartmentForm extends EditRegisterForm {
    @Override
    protected void init(FormVH holder) {
        super.init(holder);
        EditText editText = holder.getEt();
        editText.setFilters(new InputFilter[]{new InputSpaceFilter(),new InputFilter.LengthFilter(24)});//部门限制24位
    }
}
