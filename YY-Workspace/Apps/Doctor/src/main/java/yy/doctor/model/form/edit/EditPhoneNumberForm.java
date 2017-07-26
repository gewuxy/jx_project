package yy.doctor.model.form.edit;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import lib.ys.ConstantsEx;
import lib.yy.adapter.VH.FormVH;
import lib.yy.notify.Notifier;
import lib.yy.notify.Notifier.NotifyType;
import yy.doctor.R;
import yy.doctor.model.form.FormType;

import static lib.ys.util.view.ViewUtil.hideView;
import static lib.ys.util.view.ViewUtil.showView;

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
        int layout = getInt(TForm.layout);
        if (layout != ConstantsEx.KInvalidValue) {
            return layout;
        }
        return R.layout.form_edit_phone_number;
    }

    @Override
    public boolean check() {
        return super.check();
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
                    // TODO: tttttttttttttttttttt
                    editText.setSelection(str.length());
                }
                    if (str.length() != 0) {
                        showView(holder.getIv());
                    }else {
                        hideView(holder.getIv());
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = s.length();
                if (mIsAdd) {
                    String str = s.toString();
                    if (length == 3 || length == 8) {
                        String str1 = str + " "; //手动添加空格
                        editText.setText(str1);
                        editText.setSelection(str1.length());
                    } else if (length == 13) {
                        //KeyboardUtil.hideFromView(editText);
                        Notifier.inst().notify(NotifyType.fetch_message_captcha);
                    }
                } else {
                    Notifier.inst().notify(NotifyType.disable_fetch_message_captcha);
                }
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
