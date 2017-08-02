package yy.doctor.model.form;

import lib.yy.model.form.BaseForm;
import yy.doctor.model.form.divider.DividerForm;
import yy.doctor.model.form.divider.DividerLargeForm;
import yy.doctor.model.form.divider.DividerMarginForm;
import yy.doctor.model.form.edit.EditCaptchaForm;
import yy.doctor.model.form.edit.EditEmailForm;
import yy.doctor.model.form.edit.EditForgetCaptchaForm;
import yy.doctor.model.form.edit.EditForgetPhoneForm;
import yy.doctor.model.form.edit.EditForgetPwdForm;
import yy.doctor.model.form.edit.EditForm;
import yy.doctor.model.form.edit.EditIntentForm;
import yy.doctor.model.form.edit.EditNumberForm;
import yy.doctor.model.form.edit.EditPhoneNumberForm;
import yy.doctor.model.form.edit.EditRegisterForm;
import yy.doctor.model.form.edit.EditRegisterPwdForm;
import yy.doctor.model.form.text.MeForm;
import yy.doctor.model.form.text.TextDialogForm;
import yy.doctor.model.form.text.TextForm;
import yy.doctor.model.form.text.intent.IntentForm;
import yy.doctor.model.form.text.intent.IntentHospitalForm;
import yy.doctor.model.form.text.intent.IntentNoNameForm;
import yy.doctor.model.form.text.intent.IntentSkillForm;

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
            case FormType.modify_intent_skill: {
                form = new IntentSkillForm();
            }
            break;
            case FormType.text_intent_hospital: {
                form = new IntentHospitalForm();
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
            case FormType.et_register: {
                form = new EditRegisterForm();
            }
            break;
            case FormType.et_register_pwd: {
                form = new EditRegisterPwdForm();
            }
            break;
            case FormType.et_forget_pwd: {
                form = new EditForgetPwdForm();
            }
            break;
            case FormType.et_number: {
                form = new EditNumberForm();
            }
            break;
            case FormType.et_phone_number: {
                form = new EditPhoneNumberForm();
            }
            break;
            case FormType.et_forget_phone: {
                form = new EditForgetPhoneForm();
            }
            break;
            case FormType.et_email: {
                form = new EditEmailForm();
            }
            break;
            case FormType.et_captcha: {
                form = new EditCaptchaForm();
            }
            break;
            case FormType.et_forget_captcha: {
                form = new EditForgetCaptchaForm();
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

        form.enable(true);

        return form;
    }
}
