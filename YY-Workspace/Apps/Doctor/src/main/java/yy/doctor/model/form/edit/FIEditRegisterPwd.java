package yy.doctor.model.form.edit;

import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.method.NumberKeyListener;
import android.text.method.PasswordTransformationMethod;

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
    protected void init(FormItemVH holder) {
        super.init(holder);

        // 设置输入digits
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

        // FIXME: 由于上面指定了key listener, 导致input type的text隐藏失效, 所以手动设置为密码隐藏模式
        holder.getEt().setTransformationMethod(PasswordTransformationMethod.getInstance());
    }
}
