package jx.doctor.model.form;

import lib.jx.model.form.BaseForm;
import jx.doctor.model.form.divider.DividerForm;
import jx.doctor.model.form.divider.DividerLargeForm;
import jx.doctor.model.form.divider.DividerMarginForm;
import jx.doctor.model.form.edit.EditCaptchaForm;
import jx.doctor.model.form.edit.EditForm;
import jx.doctor.model.form.edit.EditIntentForm;
import jx.doctor.model.form.edit.EditPhoneNumberForm;
import jx.doctor.model.form.edit.EditPwdForm;
import jx.doctor.model.form.text.MeForm;
import jx.doctor.model.form.text.TextDialogForm;
import jx.doctor.model.form.text.TextForm;
import jx.doctor.model.form.text.intent.IntentForm;
import jx.doctor.model.form.text.intent.IntentNoNameForm;

/**
 * @author yuansui
 * @since 2017/4/6
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
            case FormType.text_dialog: {
                form = new TextDialogForm();
            }
            break;
            case FormType.text_intent_no_name: {
                form = new IntentNoNameForm();
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
            case FormType.divider_margin: {
                form = new DividerMarginForm();
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

            case FormType.profile_checkbox: {
                form = new CheckBoxForm();
            }
            break;

            case FormType.toggle_button: {
                form = new ToggleButtonForm();
            }
            break;
        }

        if (form != null) {
            form.enable(true);
        }

        return form;
    }
}
