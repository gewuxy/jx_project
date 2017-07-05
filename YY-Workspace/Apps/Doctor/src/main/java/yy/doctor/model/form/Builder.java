package yy.doctor.model.form;

import android.app.Activity;

import lib.ys.form.FormBuilderEx;
import lib.yy.model.form.BaseForm;
import yy.doctor.model.form.edit.EdiRegisterNumForm;
import yy.doctor.model.form.edit.EditForm;
import yy.doctor.model.form.edit.EditIntentForm;
import yy.doctor.model.form.edit.EditNumberForm;
import yy.doctor.model.form.edit.EditRegisterForm;
import yy.doctor.model.form.edit.EditRegisterPwdForm;
import yy.doctor.model.form.text.ContentForm;
import yy.doctor.model.form.text.ContentTextForm;
import yy.doctor.model.form.text.PicTextForm;
import yy.doctor.model.form.text.TextDialogForm;
import yy.doctor.model.form.text.TextForm;
import yy.doctor.model.form.text.TextIntentForm;
import yy.doctor.model.form.text.TextRegisterIntentForm;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class Builder extends FormBuilderEx<BaseForm> {
    Activity mActivity;
    public Builder(int type) {
        super(type);
    }
    public Builder(int type, Activity activity){
        super(type);
        this.mActivity = activity;
    }

    @Override
    protected BaseForm build(int type) {
        BaseForm form = null;

        switch (type) {
            case FormType.content: {
                form = new ContentForm();
            }
            break;
            case FormType.content_text: {
                form = new ContentTextForm();
            }

            break;
            case FormType.text: {
                form = new TextForm();
            }
            break;
            case FormType.text_intent: {
                form = new TextIntentForm();
            }
            break;
            case FormType.text_dialog: {
                form = new TextDialogForm();
            }
            break;
            case FormType.text_register_intent: {
                form = new TextRegisterIntentForm();
            }
            break;

            case FormType.divider: {
                form = new DividerForm();
            }
            break;
            case FormType.divider_large: {
                form = new DividerLargeForm();
            }
            break;

            case FormType.et: {
                form = new EditForm();
            }
            break;
            case FormType.et_intent: {
                form = new EditIntentForm();
            }
            break;
            case FormType.et_register: {
                form = new EditRegisterForm();
            }
            break;
            case FormType.et_register_pwd: {
                form = new EditRegisterPwdForm();
            }
            break;
            case FormType.et_number: {
                form = new EditNumberForm();
            }
            break;
            case FormType.et_register_num:{
                form = new EdiRegisterNumForm(mActivity);
            }
            break;
            case FormType.profile_checkbox: {
                form = new CheckBoxForm();
            }
            break;

            case FormType.pic_text:{
                form = new PicTextForm();
            }
        }

        return form;
    }

    @Override
    protected boolean initEnable() {
        return true;
    }

}
