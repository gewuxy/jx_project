package jx.csp.model.form;

import lib.yy.model.form.BaseForm;
import jx.csp.model.form.divider.DividerForm;
import jx.csp.model.form.divider.DividerLargeForm;
import jx.csp.model.form.divider.DividerMarginForm;
import jx.csp.model.form.edit.EditCaptchaForm;
import jx.csp.model.form.edit.EditForm;
import jx.csp.model.form.edit.EditPhoneNumberForm;
import jx.csp.model.form.edit.EditPwdForm;
import jx.csp.model.form.text.BindForm;
import jx.csp.model.form.text.IntentForm;
import jx.csp.model.form.text.MeForm;
import jx.csp.model.form.text.TextForm;

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
            case FormType.text_intent_bind: {
                form = new BindForm();
            }
            break;
            case FormType.divider: {
                form = new DividerForm();
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
            case FormType.et: {
                form = new EditForm();
            }
            break;
            case FormType.et_pwd: {
                form = new EditPwdForm();
            }
            break;
            case FormType.et_phone_number: {
                form = new EditPhoneNumberForm();
            }
            break;
            case FormType.et_captcha: {
                form = new EditCaptchaForm();
            }
            break;

        }

        if (form != null) {
            form.enable(true);
        }
        return form;
    }
}