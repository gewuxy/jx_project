package yy.doctor.model.form.edit;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import lib.ys.util.KeyboardUtil;
import lib.yy.adapter.VH.FormVH;
import yy.doctor.R;
import yy.doctor.model.form.FormType;

/**
 * @auther WangLan
 * @since 2017/7/4
 */

public class EditPhoneNumberForm extends EditNumberForm {

    private boolean mIsAdd;

    @NonNull
    @Override
    public int getType() {
        return FormType.et_phone_number;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_edit_phone_number;
    }


    @Override
    protected void init(FormVH holder) {
        super.init(holder);

        EditText editText = holder.getEt();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //after只能等于1和0，等于1是输入，等于0是删除
                if (after == 1) {
                    mIsAdd = true;
                } else {
                    mIsAdd = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if ((str.length() == 3 || str.length() == 8) && str.charAt(str.length() - 1) != ' ' && before > count) {
                    str = str.substring(0, str.length() - 1);
                    editText.setText(str);
                    editText.setSelection(str.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mIsAdd) {
                    String str = s.toString();
                    int length = s.length();
                    if (length == 3 || length == 8) {
                        String str1 = str + " "; //手动添加空格
                        editText.setText(str1);
                        editText.setSelection(str1.length());
                    }
                    if (length >= 13) {
                        KeyboardUtil.hideFromView(editText);
                    }
                }
            }
        });
    }

}
