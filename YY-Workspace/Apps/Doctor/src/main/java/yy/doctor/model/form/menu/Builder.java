package yy.doctor.model.form.menu;

import lib.ys.form.FormBuilder;
import lib.yy.model.form.FormItem;

/**
 * @author CaiXiang
 * @since 2017/4/5
 */
public class Builder extends FormBuilder<FormItem> {

    public Builder(int type) {
        super(type);
    }

    @Override
    protected FormItem build(int type) {
        FormItem item = null;

        switch (type) {
            case MenuType.child: {
                item = new FIChild();
            }
            break;
            case MenuType.group: {
                item = new FIGroup();
            }
            break;
            case MenuType.divider: {
                item = new FIDivider();
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
