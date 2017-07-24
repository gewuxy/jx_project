package yy.doctor.model.form.edit;

import android.support.annotation.NonNull;

import lib.yy.adapter.VH.FormVH;
import yy.doctor.model.form.FormType;
import yy.doctor.util.UISetter;

/**
 * @author GuoXuan
 * @since 2017/6/8
 */
public class EditRegisterPwdForm extends EditRegisterForm {

    @NonNull
    @Override
    public int getType() {
        return FormType.et_register_pwd;
    }

    @Override
    protected void init(FormVH holder) {
        super.init(holder);

        // 设置输入digits
        UISetter.setPwdRange(holder.getEt());


        // FIXME: 由于上面指定了key listener, 导致input type的text隐藏失效, 所以手动设置为密码隐藏模式
        //holder.getEt().setTransformationMethod(PasswordTransformationMethod.getInstance());
        holder.getIv().setSelected(true);
    }

    @Override
    protected void refresh(FormVH holder) {
        super.refresh(holder);

        // TODO: dsfdf
    }
}
