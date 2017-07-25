package yy.doctor.model.form.text.intent;

import android.support.annotation.NonNull;

import yy.doctor.R;
import yy.doctor.model.form.FormType;

/**
 * @author CaiXiang
 * @since 2017/5/24
 */

public class IntentNoNameForm extends IntentForm {

    @NonNull
    @Override
    public int getType() {
        return FormType.text_intent_no_name;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.form_text_intent_no_name;
    }
}
