package yy.doctor.model.form.text.intent;

import yy.doctor.R;

/**
 * 学术专长
 *
 * @auther Huoxuyu
 * @since 2017/7/24
 */

public class IntentSkillForm extends IntentForm {

    @Override
    public int getContentViewResId() {
        return R.layout.form_text_academic;
    }

    @Override
    public boolean check() {
        return true;
    }
}
