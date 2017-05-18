package yy.doctor.model.form;

import lib.ys.form.FormBuilderEx;
import lib.yy.model.form.FormItem;
import yy.doctor.model.form.edit.FIEdit;
import yy.doctor.model.form.edit.FIEditIntent;
import yy.doctor.model.form.edit.FIEditRegister;
import yy.doctor.model.form.text.FIContent;
import yy.doctor.model.form.text.FIContentText;
import yy.doctor.model.form.text.FIText;
import yy.doctor.model.form.text.FITextIntent;
import yy.doctor.model.form.text.FITextDialog;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class Builder extends FormBuilderEx<FormItem> {

    public Builder(int type) {
        super(type);
    }

    @Override
    protected FormItem build(int type) {
        FormItem item = null;

        switch (type) {

            case FormType.content: {
                item = new FIContent();
            }
            break;
            case FormType.content_text: {
                item = new FIContentText();
            }
            break;
            case FormType.text: {
                item = new FIText();
            }
            break;
            case FormType.text_intent: {
                item = new FITextIntent();
            }
            break;
            case FormType.text_dialog: {
                item = new FITextDialog();
            }
            break;

            case FormType.divider: {
                item = new FIDivider();
            }
            break;
            case FormType.divider_large: {
                item = new FIDividerLarge();
            }
            break;

            case FormType.et: {
                item = new FIEdit();
            }
            break;
            case FormType.et_intent: {
                item = new FIEditIntent();
            }
            break;
            case FormType.et_register: {
                item = new FIEditRegister();
            }
            break;

            case FormType.profile_checkbox: {
                item = new FICheckBox();
            }
            break;
        }

        return item;
    }

    @Override
    protected boolean initEnable() {
        return true;
    }

}
