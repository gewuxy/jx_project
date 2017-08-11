package yy.doctor.model.form.edit;

import android.text.Editable;
import android.widget.EditText;

import lib.ys.ConstantsEx;
import lib.ys.util.TextUtil;
import lib.ys.util.view.ViewUtil;
import lib.yy.notify.Notifier;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.R;

/**
 * @auther WangLan
 * @since 2017/7/4
 */

public class EditPhoneNumberForm extends EditForm {

    private boolean mIsAdd;

    @Override
    public int getContentViewResId() {
        int layout = getLayoutId();
        if (layout != ConstantsEx.KInvalidValue) {
            return layout;
        }
        return R.layout.form_edit_phone_number;
    }

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

            EditText et = getHolder().getEt();
            et.setText(str);
            et.setSelection(str.length());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        super.afterTextChanged(s);

        int length = s.length();
        if (mIsAdd) {
            String str = s.toString();
            if (length == 3 || length == 8) {
                String str1 = str + " "; //手动添加空格

                EditText et = getHolder().getEt();
                et.setText(str1);
                et.setSelection(str1.length());
            } else if (length == 13) {
                //KeyboardUtil.hideFromView(editText);
                Notifier.inst().notify(NotifyType.fetch_message_captcha);
            }
        } else {
            Notifier.inst().notify(NotifyType.disable_fetch_message_captcha);
        }

        if (TextUtil.isNotEmpty(s)) {
            ViewUtil.showView(getHolder().getIvClean());
        } else {
            ViewUtil.goneView(getHolder().getIvClean());
        }
    }
}
