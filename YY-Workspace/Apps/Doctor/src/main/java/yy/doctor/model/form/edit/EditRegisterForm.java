package yy.doctor.model.form.edit;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import lib.yy.adapter.VH.FormVH;
import yy.doctor.R;
import yy.doctor.model.form.FormType;

import static lib.ys.util.view.ViewUtil.hideView;
import static lib.ys.util.view.ViewUtil.showView;

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
        EditText editText = holder.getEt();
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)});//姓名限制18位
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (str.length() != 0) {
                    showView(holder.getIv());
                }else {
                    hideView(holder.getIv());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        setOnClickListener(holder.getIv());
    }
    @Override
    protected boolean onViewClick(View v) {
        getHolder().getEt().setText("");
        return super.onViewClick(v);

    }
}
