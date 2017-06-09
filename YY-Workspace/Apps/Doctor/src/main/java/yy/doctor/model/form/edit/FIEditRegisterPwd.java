package yy.doctor.model.form.edit;

import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.method.NumberKeyListener;

import lib.yy.adapter.VH.FormItemVH;
import yy.doctor.model.form.FormType;

/**
 * @author GuoXuan
 * @since 2017/6/8
 */
public class FIEditRegisterPwd extends FIEditRegister {

    @NonNull
    @Override
    public int getType() {
        return FormType.et_register_pwd;
    }

    @Override
    protected void refresh(FormItemVH holder) {
        super.refresh(holder);

        holder.getEt().setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        holder.getEt().setKeyListener(new NumberKeyListener() {

            @Override
            protected char[] getAcceptedChars() {
                String chars = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
                return chars.toCharArray();
            }

            @Override
            public int getInputType() {
                return InputType.TYPE_TEXT_VARIATION_PASSWORD;
            }
        });
    }
}
