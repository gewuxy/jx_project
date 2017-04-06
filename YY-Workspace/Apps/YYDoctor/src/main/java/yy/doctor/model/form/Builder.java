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
        }

        saveItemValues(item);

        return item;
    }

    @Override
    protected boolean initEnable() {
        return true;
    }

}
