package yy.doctor.model.form;

import lib.ys.form.FormBuilder;
import lib.yy.model.form.FormItem;

/**
 * @author CaiXiang
 * @since 2017/4/6
 */
public class Builder extends FormBuilder<FormItem> {

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
            case FormType.profile_checkbox: {
                item = new FICheckBox();
            }
            break;

            case FormType.et_register: {
                item = new FIEditRegister();
            }
            break;

        }

        saveItemValues(item);

        return item;
    }

    @Override
    protected boolean initEnable() {
        return true;
    }

}
