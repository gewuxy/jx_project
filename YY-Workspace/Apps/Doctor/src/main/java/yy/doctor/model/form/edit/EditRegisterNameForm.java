package yy.doctor.model.form.edit;

import android.text.InputFilter;
import android.widget.EditText;

import lib.yy.adapter.VH.FormVH;
import yy.doctor.util.input.InputFilterChineseImpl;

/**
 * @auther WangLan
 * @since 2017/8/8
 */

public class EditRegisterNameForm extends EditRegisterForm {
    @Override
    protected void init(FormVH holder) {
        super.init(holder);
        EditText editText = holder.getEt();
        editText.setFilters(new InputFilter[]{new InputFilterChineseImpl(), new InputFilter.LengthFilter(18)});//姓名限制18位
    }
}
