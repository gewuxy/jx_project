package yaya.csp.model.form;

import lib.yy.model.form.BaseForm;
import yaya.csp.model.form.divider.DividerMarginForm;
import yaya.csp.model.form.divider.DividerLargeForm;
import yaya.csp.model.form.text.IntentForm;
import yaya.csp.model.form.text.MeForm;
import yaya.csp.model.form.text.TextForm;

/**
 * @auther HuoXuYu
 * @since 2017/9/21
 */

public class Form {

    private Form() {
    }

    public static BaseForm create(int type) {
        BaseForm form = null;

        switch (type) {
            case FormType.text: {
                form = new TextForm();
            }
            break;
            case FormType.text_intent: {
                form = new IntentForm();
            }
            break;
            case FormType.text_intent_me: {
                form = new MeForm();
            }
            break;
            case FormType.divider_margin: {
                form = new DividerMarginForm();
            }
            break;
            case FormType.divider_large: {
                form = new DividerLargeForm();
            }
            break;
        }

        if (form != null) {
            form.enable(true);
        }
        return form;
    }
}
